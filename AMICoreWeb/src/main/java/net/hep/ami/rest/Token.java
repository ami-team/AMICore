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

	/*---------------------------------------------------------------------*/

	@GET
	@Path("certificate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getBycertificate(
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

		/*-----------------------------------------------------------------*/

		return Response.status(Response.Status.UNAUTHORIZED).build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@DELETE
	@Path("{token}")
	public Response delete(
		@Context HttpServletRequest request
	 ) {
		request.getSession(true).removeAttribute("token");

		return Response.status(Response.Status./*-*/OK/*-*/).build();
	}

	/*---------------------------------------------------------------------*/

	private Response get(HttpServletRequest request, @Nullable String AMIUser, @Nullable String AMIPass, @Nullable String clientDN, @Nullable String issuerDN, String notBefore, String notAfter)
	{
		/*-----------------------------------------------------------------*/
		/* CHECK CREDENTIALS                                               */
		/*-----------------------------------------------------------------*/

		try
		{
			SimpleQuerier basicQuerier = new SimpleQuerier("self");

			try
			{
				List<Row> rows;

				/**/ if(clientDN != null
				        &&
				        issuerDN != null
				 ) {
					rows = basicQuerier.executeSQLQuery("SELECT AMIUser, AMIPass FROM `router_user` WHERE `clientDN` = ? AND `issuerDN` = ?", SecuritySingleton.encrypt(clientDN), SecuritySingleton.encrypt(issuerDN)).getAll();
				}
				else if(AMIUser != null
				        &&
				        AMIPass != null
				 ) {
					rows = basicQuerier.executeSQLQuery("SELECT AMIUser, AMIPass FROM `router_user` WHERE `AMIUser` = ? AND `AMIPass` = ?", /* must not be crypted */(AMIUser), SecuritySingleton.encrypt(AMIPass)).getAll();
				}
				else
				{
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}

				if(rows.size() != 1)
				{
					return Response.status(Response.Status.UNAUTHORIZED).build();
				}

				AMIUser = /*---------------------*/(rows.get(0).getValue(0));
				AMIPass = SecuritySingleton.decrypt(rows.get(0).getValue(1));
			}
			finally
			{
				basicQuerier.rollbackAndRelease();
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		/*-----------------------------------------------------------------*/
		/* BUILD TOKEN                                                     */
		/*-----------------------------------------------------------------*/

		request.getSession(true).setAttribute("token", new Tuple7<>(
			System.currentTimeMillis(),
			AMIUser,
			AMIPass,
			clientDN != null ? clientDN : "",
			issuerDN != null ? issuerDN : "",
			notBefore != null ? notBefore : "",
			notAfter != null ? notAfter : ""
		));

		/*-----------------------------------------------------------------*/

		return Response.ok("success").build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
