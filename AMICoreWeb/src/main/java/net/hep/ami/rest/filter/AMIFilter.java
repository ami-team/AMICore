package net.hep.ami.rest.filter;

import java.io.*;

import javax.ws.rs.core.*;
import javax.ws.rs.container.*;

public class AMIFilter implements ContainerResponseFilter
{
	/*---------------------------------------------------------------------*/

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException
	{
/*
		String origin = request.getHeaderString("Origin");

		if(origin != null)
		{
			MultivaluedMap<String, Object> headers = response.getHeaders();

			headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE");
			headers.add("Access-Control-Allow-Credentials", "true");
			headers.add("Access-Control-Allow-Origin", origin);
		}
*/
	}

	/*---------------------------------------------------------------------*/
}
