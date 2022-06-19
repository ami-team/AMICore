package net.hep.ami.rest;

import java.text.*;
import java.util.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;

import org.jetbrains.annotations.*;

@Path("/token")
public class Token
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@GET
	@Path("password")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getByPassword(
		@Context HttpServletRequest request,
		@QueryParam("username") @DefaultValue("") String username,
		@QueryParam("password") @DefaultValue("") String password
	 ) {
		return get(request, username, password, null, null, null, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@GET
	@Path("certificate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getByCertificate(
		@NotNull @Context HttpServletRequest request
	 ) {
		/*------------------------------------------------------------------------------------------------------------*/

		java.security.cert.X509Certificate[] certificates = (java.security.cert.X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

		if(certificates != null)
		{
			for(java.security.cert.X509Certificate certificate: certificates)
			{
				if(!SecuritySingleton.isProxy(certificate))
				{
					return get(
						request,
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

		/*------------------------------------------------------------------------------------------------------------*/

		return Response.status(Response.Status.UNAUTHORIZED).build();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@DELETE
	public Response delete(
		@NotNull @Context HttpServletRequest request
	 ) {
		request.getSession(true).removeAttribute("token");

		return Response.status(Response.Status./*-*/OK/*-*/).build();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private Response get(HttpServletRequest request, @Nullable String AMIUser, @Nullable String AMIPass, @Nullable String clientDN, @Nullable String issuerDN, String notBefore, String notAfter)
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK CREDENTIALS                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			RouterQuerier querier = new RouterQuerier();

			try
			{
				List<Row> rows;

				boolean checkPasswork;

				/*----------------------------------------------------------------------------------------------------*/

				/**/ if(clientDN != null
				        &&
				        issuerDN != null
				 ) {
					rows = querier.executeSQLQuery("router_user", "SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `clientDN` = ?#0 AND `issuerDN` = ?#1", clientDN, issuerDN).getAll();

					checkPasswork = false;
				}
				else if(AMIUser != null
				        &&
				        AMIPass != null
				 ) {
					rows = querier.executeSQLQuery("router_user", "SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `AMIUser` = ?0", AMIUser).getAll();

					checkPasswork = true;
				}
				else
				{
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}

				/*----------------------------------------------------------------------------------------------------*/

				if(rows.size() != 1)
				{
					return Response.status(Response.Status.UNAUTHORIZED).build();
				}

				AMIUser = rows.get(0).getValue(0);

				/*----------------------------------------------------------------------------------------------------*/

				if(checkPasswork)
				{
					try
					{
						String hashed = rows.get(0).getValue(1);

						SecuritySingleton.checkPassword(AMIUser, AMIPass, hashed);
					}
					catch(Exception e)
					{
						return Response.status(Response.Status.UNAUTHORIZED).build();
					}
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			finally
			{
				querier.rollbackAndRelease();
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		/*-----------------------------------------------------------------*/
		/* BUILD TOKEN                                                     */
		/*-----------------------------------------------------------------*/

		HttpSession session = request.getSession(true);

		session.setMaxInactiveInterval(7200);

		/*-----------------------------------------------------------------*/

		Map<String, String> token = new HashMap<>();

		token.put("AMIUser", AMIUser);
		token.put("clientDN", clientDN != null ? clientDN : "");
		token.put("issuerDN", issuerDN != null ? issuerDN : "");
		token.put("notBefore", notBefore != null ? notBefore : "");
		token.put("notAfter", notAfter != null ? notAfter : "");

		/*-----------------------------------------------------------------*/

		session.setAttribute("token", token);

		/*------------------------------------------------------------------------------------------------------------*/

		return Response.ok(session.getId()).build();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
