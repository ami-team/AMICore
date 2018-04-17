package net.hep.ami.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import net.hep.ami.utility.Tuple2;

@Path("/cmd")
public class Cmd
{
	/*---------------------------------------------------------------------*/

	@GET
	@Path("password")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType./*----*/WILDCARD/*----*/)
	public Response password_alt(@QueryParam("token") @DefaultValue("") String token)
	{
		/*-----------------------------------------------------------------*/

		Tuple2<String, String> credentials;

		try
		{
			credentials = Auth.getCredentials(token);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		/*-----------------------------------------------------------------*/

		return Response.ok(credentials.x).build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
