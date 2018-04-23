package net.hep.ami.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

@Path("/ping")
public class Ping
{
	/*---------------------------------------------------------------------*/

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping(
		@Context HttpServletRequest request
	 ) {
		request.getSession(true);

		return Response.ok("pong").build();
	}

	/*---------------------------------------------------------------------*/
}
