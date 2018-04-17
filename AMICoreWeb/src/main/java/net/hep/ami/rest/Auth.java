package net.hep.ami.rest;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

@Path("/auth")
public class Auth
{
	/*---------------------------------------------------------------------*/

	private static final int PASSWORD = 0;
	private static final int CERTIFICATE = 1;

	/*---------------------------------------------------------------------*/

	private static final Map<String, Long> s_tokens = new java.util.concurrent.ConcurrentHashMap<>();

	/*---------------------------------------------------------------------*/

	@Context
	private HttpServletRequest m_request;

	/*---------------------------------------------------------------------*/

	@GET
	@Path("password")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType./*----*/WILDCARD/*----*/)
	public Response password_alt(@QueryParam("username") @DefaultValue("") String username, @QueryParam("password") @DefaultValue("") String password)
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
	public Response password(@FormParam("username") @DefaultValue("") String username, @FormParam("password")  @DefaultValue("") String password)
	{
		/*-----------------------------------------------------------------*/

		return auth(username, password, PASSWORD);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("certificate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response certificate()
	{
		/*-----------------------------------------------------------------*/

		java.security.cert.X509Certificate[] certificates = (java.security.cert.X509Certificate[]) m_request.getAttribute("javax.servlet.request.X509Certificate");

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
						row = basicQuerier.executeSQLQuery("SELECT COUNT(*) FROM `router_user` WHERE `AMIUser` = ? AND `AMIPass` = ?", /*---------------------*/(X), SecuritySingleton.encrypt(Y)).getAll();
						break;

					case CERTIFICATE:
						row = basicQuerier.executeSQLQuery("SELECT COUNT(*) FROM `router_user` WHERE `clientDN` = ? AND `issuerDN` = ?", SecuritySingleton.encrypt(X), SecuritySingleton.encrypt(Y)).getAll();
						break;

					default:
						return Response.status(Response.Status.FORBIDDEN).build();
				}

				if("1".equals(row.get(0).getValue(0)) == false)
				{
					return Response.status(Response.Status.FORBIDDEN).build();
				}
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

		s_tokens.put(result, System.currentTimeMillis());

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

		for(Map.Entry<String, Long> entry: s_tokens.entrySet())
		{
			if((currentTime - entry.getValue()) > (2 * 60 * 60 * 1000))
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
}
