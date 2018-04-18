package net.hep.ami.rest;

import java.io.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

@Path("/cmd")
public class Cmd
{
	/*---------------------------------------------------------------------*/

	private static final TypeReference<LinkedHashMap<String, String>> s_typeReference = new TypeReference<LinkedHashMap<String, String>>() {};

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJson(
		@Context HttpServletRequest request,
		@QueryParam("token") @DefaultValue("") String token,
		@QueryParam("converter") @DefaultValue("") String converter,
		@PathParam("command") String command,
		String arguments
	 ) {
		try
		{
			return execute(request, token, command, new ObjectMapper().readValue(arguments, s_typeReference), converter);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}/xml")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputXml(
		@Context HttpServletRequest request,
		@QueryParam("token") @DefaultValue("") String token,
		@PathParam("command") String command,
		String arguments
	 ) {
		try
		{
			return execute(request, token, command, new ObjectMapper().readValue(arguments, s_typeReference), /*----*/""/*----*/);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputJson(
		@Context HttpServletRequest request,
		@QueryParam("token") @DefaultValue("") String token,
		@PathParam("command") String command,
		String arguments
	 ) {
		try
		{
			return execute(request, token, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToJson.xsl");
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}/csv")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputCsv(
		@Context HttpServletRequest request,
		@QueryParam("token") @DefaultValue("") String token,
		@PathParam("command") String command,
		String arguments
	 ) {
		try
		{
			return execute(request, token, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToCsv.xsl");
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}/text")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputText(
		@Context HttpServletRequest request,
		@QueryParam("token") @DefaultValue("") String token,
		@PathParam("command") String command,
		String arguments
	 ) {
		try
		{
			return execute(request, token, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToText.xsl");
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	private Response execute(HttpServletRequest request, String token, String command, Map<String, String> arguments, String converter)
	{
		/*-----------------------------------------------------------------*/
		/* CHECK CRENDENTIALS                                              */
		/*-----------------------------------------------------------------*/

		Tuple7<Long, String, String, String, String, String, String> credentials;

		try
		{
			credentials = Token.getCredentials(token);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", credentials.y);
		arguments.put("AMIPass", credentials.z);

		arguments.put("clientDN", credentials.t);
		arguments.put("issuerDN", credentials.u);

		arguments.put("notBefore", credentials.v);
		arguments.put("notAfter", credentials.w);

		/**/

		arguments.put("isSecure", request.isSecure() ? "true"
		                                             : "false"
		);

		/**/

		String agent = request.getHeader("User-Agent");

		if(agent.startsWith("cami")
		   ||
		   agent.startsWith("jami")
		   ||
		   agent.startsWith("pami")
		   ||
		   agent.startsWith("pyAMI")
		 ) {
			arguments.put("userAgent", agent);
		}
		else {
			arguments.put("userAgent", "web");
		}

		/*-----------------------------------------------------------------*/

		String data;

		try
		{
			data = CommandSingleton.executeCommand(command, arguments);
		}
		catch(Exception e)
		{
			data = XMLTemplates.error(
				e.getMessage()
			);
		}

		/*-----------------------------------------------------------------*/
		/* CONVERT RESULT                                                  */
		/*-----------------------------------------------------------------*/

		String mime;

		if(converter.isEmpty() == false)
		{
			StringReader stringReader = new StringReader(data);
			StringWriter stringWriter = new StringWriter(/**/);

			try
			{
				mime = ConverterSingleton.convert(converter, stringReader, stringWriter);

				data = stringWriter.toString();
			}
			catch(Exception e)
			{
				data = XMLTemplates.error(
					e.getMessage()
				);

				mime = "text/xml";
			}
		}
		else
		{
			mime = "text/xml";
		}

		/*-----------------------------------------------------------------*/

		return Response.ok(data).type(mime).build();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
