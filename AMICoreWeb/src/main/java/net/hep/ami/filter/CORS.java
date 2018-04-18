package net.hep.ami.filter;

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
			response.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE");
			response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
			response.getHttpHeaders().add("Access-Control-Allow-Origin", origin);
		}

		/*-----------------------------------------------------------------*/

		return response;
	}

	/*---------------------------------------------------------------------*/
}
