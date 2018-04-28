package net.hep.ami.rest.filter;

import javax.ws.rs.core.*;

import com.sun.jersey.spi.container.*;

public class CORS implements ContainerResponseFilter
{
	/*---------------------------------------------------------------------*/

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response)
	{
		/*-----------------------------------------------------------------*/

		String token = request.getHeaderValue("AMI-Token");

		if(token != null)
		{
			MultivaluedMap<String, Object> headers = response.getHttpHeaders();

			headers.add("Cookie", "JSESSIONID=" + token);

			headers.remove("AMI-Token");
		}

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
