package net.hep.ami.rest;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

@Path("/auth")
public class Auth
{
	/*---------------------------------------------------------------------*/

	private static final int PASSWORD = 0;
	private static final int CERTIFICATE = 1;

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple3<String, String, Long>> s_tokens = new java.util.concurrent.ConcurrentHashMap<>();

	/*---------------------------------------------------------------------*/

	@GET
	@Path("password")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType./*----*/WILDCARD/*----*/)
	public Response passwordAuthGet(@QueryParam("username") @DefaultValue("") String username, @QueryParam("password") @DefaultValue("") String password)
	{
		/*-----------------------------------------------------------------*/

		return auth(username, password, PASSWORD);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("password")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response passwordAuthPost(@FormParam("username") @DefaultValue("") String username, @FormParam("password")  @DefaultValue("") String password)
	{
		/*-----------------------------------------------------------------*/

		return auth(username, password, PASSWORD);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("certificate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response certificateAuth(@Context HttpServletRequest request)
	{
		/*-----------------------------------------------------------------*/

		java.security.cert.X509Certificate[] certificates = (java.security.cert.X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null)
		{
			for(java.security.cert.X509Certificate certificate: certificates)
			{
				if(SecuritySingleton.isProxy(certificate) == false)
				{
					return auth(
						SecuritySingleton.getDN(certificate.getSubjectX500Principal()),
						SecuritySingleton.getDN(certificate.getIssuerX500Principal()),
						CERTIFICATE
					);
				}
			}
		}

		/*-----------------------------------------------------------------*/

		return Response.status(Response.Status.FORBIDDEN).build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private Response auth(String X, String Y, int mode)
	{
		/*-----------------------------------------------------------------*/
		/* REMOVE OLD TOKENS                                               */
		/*-----------------------------------------------------------------*/

		removeOldToken();

		/*-----------------------------------------------------------------*/
		/* CHECK USER                                                      */
		/*-----------------------------------------------------------------*/

		String AMIUser;
		String AMIPass;

		try
		{
			/*-------------------------------------------------------------*/

			SimpleQuerier basicQuerier = new SimpleQuerier("self");

			try
			{
				List<Row> row;

				switch(mode)
				{
					case PASSWORD:
						row = basicQuerier.executeSQLQuery("SELECT AMIUser, AMIPass FROM `router_user` WHERE `AMIUser` = ? AND `AMIPass` = ?", /*---------------------*/(X), SecuritySingleton.encrypt(Y)).getAll();
						break;

					case CERTIFICATE:
						row = basicQuerier.executeSQLQuery("SELECT AMIUser, AMIPass FROM `router_user` WHERE `clientDN` = ? AND `issuerDN` = ?", SecuritySingleton.encrypt(X), SecuritySingleton.encrypt(Y)).getAll();
						break;

					default:
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

		s_tokens.put(result, new Tuple3<>(AMIUser, AMIPass, System.currentTimeMillis()));

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

		for(Map.Entry<String, Tuple3<String, String, Long>> entry: s_tokens.entrySet())
		{
			if((currentTime - entry.getValue().z) > (2 * 60 * 60 * 1000))
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

	public static Tuple2<String, String> getCredentials(String token) throws Exception
	{
		Tuple3<String, String, Long> tuple = s_tokens.get(token);

		if(tuple == null)
		{
			throw new Exception("invalid token");
		}

		return new Tuple2<>(tuple.x, tuple.y);
	}

	/*---------------------------------------------------------------------*/
}
