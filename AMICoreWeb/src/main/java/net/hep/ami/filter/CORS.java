package net.hep.ami.filter;

import javax.ws.rs.core.*;

import com.sun.jersey.spi.container.*;

public class CORS implements ContainerResponseFilter
{
	/*---------------------------------------------------------------------*/

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response)
	{
		/*-----------------------------------------------------------------*/

		String origin = request.getHeaderValue("Origin");

		if(origin != null)
		{
			MultivaluedMap<String, Object> headers = response.getHttpHeaders();

			headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE");
			headers.add("Access-Control-Allow-Credentials", "true");
			headers.add("Access-Control-Allow-Origin", origin);
		}

		/*-----------------------------------------------------------------*/

		return response;
	}

	/*---------------------------------------------------------------------*/
}
