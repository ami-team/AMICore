package net.hep.ami.rest;

import java.text.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@Path("/token")
public class Token
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected static final class Tuple extends Tuple6<String, String, String, String, String, String>
	{
		private static final long serialVersionUID = -8015590076924252736L;

		public Tuple(String _x, String _y, String _z, String _t, String _u, String _v)
		{
			super(_x, _y, _z, _t, _u, _v);
		}
	}

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
			Router router = new Router();

			try
			{
				List<Row> rows;

				/**/ if(clientDN != null
				        &&
				        issuerDN != null
				 ) {
					rows = router.executeSQLQuery("router_user", "SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `clientDN` = ?#0 AND `issuerDN` = ?#1", clientDN, issuerDN).getAll();
				}
				else if(AMIUser != null
				        &&
				        AMIPass != null
				 ) {
					rows = router.executeSQLQuery("router_user", "SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `AMIUser` = ?0 AND `AMIPass` = ?#1", AMIUser, AMIPass).getAll();
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
				router.rollbackAndRelease();
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

		session.setAttribute("token", new Tuple(
			AMIUser,
			AMIPass,
			clientDN != null ? clientDN : "",
			issuerDN != null ? issuerDN : "",
			notBefore != null ? notBefore : "",
			notAfter != null ? notAfter : ""
		));

		/*------------------------------------------------------------------------------------------------------------*/

		return Response.ok(session.getId() + " | " + ((Token.Tuple) session.getAttribute("token")).toString()).build();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
