package net.hep.ami.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

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

		return Response.ok("pong").build();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
