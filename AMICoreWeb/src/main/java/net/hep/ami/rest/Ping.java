package net.hep.ami.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.servlet.http.*;

import org.jetbrains.annotations.*;

@Path("/ping")
public class Ping
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping(
		@NotNull @Context HttpServletRequest request
	 ) {
		HttpSession session = request.getSession(true);

		session.setMaxInactiveInterval(7200);

		return Response.ok("pong").build();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
