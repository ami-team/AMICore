package net.hep.ami.rest;

import java.text.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

@Path("/token")
public class Token
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple7<Long, String, String, String, String, String, String>> s_tokens = new java.util.concurrent.ConcurrentHashMap<>();

	/*---------------------------------------------------------------------*/

	@GET
	@Path("password")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType./*----*/WILDCARD/*----*/)
	public Response passwordAuthGet(
		@QueryParam("username") @DefaultValue("") String username,
		@QueryParam("password") @DefaultValue("") String password
	 ) {
		return token_get(username, password, null, null, null, null);
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("certificate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response certificateAuth(
		@Context HttpServletRequest request
	 ) {
		/*-----------------------------------------------------------------*/

		java.security.cert.X509Certificate[] certificates = (java.security.cert.X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null)
		{
			for(java.security.cert.X509Certificate certificate: certificates)
			{
				if(SecuritySingleton.isProxy(certificate) == false)
				{
					return token_get(
						null,
						null,
						SecuritySingleton.getDN(certificate.getSubjectX500Principal()),
						SecuritySingleton.getDN(certificate.getIssuerX500Principal()),
						new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(
							certificate.getNotBefore()
						),
						new SimpleDateFormat("EEE, d MMM yyyy", Locale.US).format(
							certificate.getNotAfter()
						)
					);
				}
			}
		}

		/*-----------------------------------------------------------------*/

		return Response.status(Response.Status.FORBIDDEN).build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@DELETE
	public Response token_del(
		@QueryParam("token") @DefaultValue("") String token
	 ) {
		Object tuple = s_tokens.remove(token);

		if(tuple != null) {
			return Response.status(Response.Status./*-*/OK/*-*/).build();
		}
		else {
			return Response.status(Response.Status.NOT_MODIFIED).build();
		}
	}

	/*---------------------------------------------------------------------*/

	private Response token_get(@Nullable String AMIUser, @Nullable String AMIPass, @Nullable String clientDN, @Nullable String issuerDN, String notBefore, String notAfter)
	{
		/*-----------------------------------------------------------------*/
		/* REMOVE OLD TOKENS                                               */
		/*-----------------------------------------------------------------*/

		removeOldToken();

		/*-----------------------------------------------------------------*/
		/* CHECK USER                                                      */
		/*-----------------------------------------------------------------*/

		try
		{
			/*-------------------------------------------------------------*/

			SimpleQuerier basicQuerier = new SimpleQuerier("self");

			try
			{
				List<Row> row;

				/**/ if(clientDN != null
				        &&
				        issuerDN != null
				 ) {
					row = basicQuerier.executeSQLQuery("SELECT AMIUser, AMIPass FROM `router_user` WHERE `clientDN` = ? AND `issuerDN` = ?", SecuritySingleton.encrypt(clientDN), SecuritySingleton.encrypt(issuerDN)).getAll();
				}
				else if(AMIUser != null
				        &&
				        AMIPass != null
				 ) {
					row = basicQuerier.executeSQLQuery("SELECT AMIUser, AMIPass FROM `router_user` WHERE `AMIUser` = ? AND `AMIPass` = ?", /*---------------------*/(AMIUser), SecuritySingleton.encrypt(AMIPass)).getAll();
				}
				else
				{
					return Response.status(Response.Status.FORBIDDEN).build();
				}

				if(row.size() != 1)
				{
					return Response.status(Response.Status.FORBIDDEN).build();
				}

				AMIUser = row.get(0).getValue(0);
				AMIPass = row.get(0).getValue(1);
			}
			finally
			{
				basicQuerier.rollbackAndRelease();
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		/*-----------------------------------------------------------------*/
		/* GET TOKEN                                                       */
		/*-----------------------------------------------------------------*/

		String result = UUID.randomUUID().toString();

		s_tokens.put(result, new Tuple7<>(System.currentTimeMillis(), AMIUser, AMIPass, clientDN, issuerDN, notBefore, notAfter));

		/*-----------------------------------------------------------------*/

		return Response.ok(result).build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void removeOldToken()
	{
		List<String> oldTokens = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		long currentTime = System.currentTimeMillis();

		for(Map.Entry<String, Tuple7<Long, String, String, String, String, String, String>> entry: s_tokens.entrySet())
		{
			if((currentTime - entry.getValue().x) > (2 * 60 * 60 * 1000))
			{
				oldTokens.add(entry.getKey());
			}
		}

		/*-----------------------------------------------------------------*/

		for(String token: oldTokens)
		{
			s_tokens.remove(token);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple7<Long, String, String, String, String, String, String> getCredentials(String token) throws Exception
	{
		Tuple7<Long, String, String, String, String, String, String> result = s_tokens.get(token);

		if(result == null)
		{
			throw new Exception("invalid token");
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
