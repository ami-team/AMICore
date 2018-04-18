package net.hep.ami.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/ping")
public class Ping
{
	/*---------------------------------------------------------------------*/

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping()
	{
		return Response.ok("pong").build();
	}

	/*---------------------------------------------------------------------*/
}
