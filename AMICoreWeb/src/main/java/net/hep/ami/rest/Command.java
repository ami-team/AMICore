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

@Path("/command")
public class Command
{
	/*---------------------------------------------------------------------*/

	private static final TypeReference<LinkedHashMap<String, String>> s_typeReference = new TypeReference<LinkedHashMap<String, String>>() {};

	/*---------------------------------------------------------------------*/

	private static final Map<String, String> s_help = new LinkedHashMap<>();

	/*---------------------------------------------------------------------*/

	static
	{
		s_help.put("help", "");
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}/help")
	public Response help1(
		@Context HttpServletRequest request,
		@QueryParam("converter") @DefaultValue("") String converter,
		@PathParam("command") String command
	 ) {
		try
		{
			return execute(request, command, s_help, converter);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}/help/{format}")
	public Response help2(
		@Context HttpServletRequest request,
		@PathParam("command") String command,
		@PathParam("format") String format
	 ) {
		try
		{
			/**/ if("json".equalsIgnoreCase(format)) {
				return execute(request, command, s_help, "AMIXmlToJson.xsl");
			}
			else if("csv".equalsIgnoreCase(format)) {
				return execute(request, command, s_help, "AMIXmlToCsv.xsl");
			}
			else if("text".equalsIgnoreCase(format)) {
				return execute(request, command, s_help, "AMIXmlToText.xsl");
			}
			else {
				return execute(request, command, s_help, /*----*/""/*----*/);
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response execute1(
		@Context HttpServletRequest request,
		@QueryParam("converter") @DefaultValue("") String converter,
		@PathParam("command") String command,
		String arguments
	 ) {
		try
		{
			return execute(request, command, new ObjectMapper().readValue(arguments, s_typeReference), converter);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@POST
	@Path("{command}/{format}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response execute2(
		@Context HttpServletRequest request,
		@PathParam("command") String command,
		@PathParam("format") String format,
		String arguments
	 ) {
		try
		{
			/**/ if("json".equalsIgnoreCase(format)) {
				return execute(request, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToJson.xsl");
			}
			else if("csv".equalsIgnoreCase(format)) {
				return execute(request, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToCsv.xsl");
			}
			else if("text".equalsIgnoreCase(format)) {
				return execute(request, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToText.xsl");
			}
			else {
				return execute(request, command, new ObjectMapper().readValue(arguments, s_typeReference), /*----*/""/*----*/);
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@SuppressWarnings("unchecked")
	private Response execute(HttpServletRequest request, String command, Map<String, String> arguments, String converter)
	{
		/*-----------------------------------------------------------------*/
		/* CHECK CRENDENTIALS                                              */
		/*-----------------------------------------------------------------*/

		Tuple7<Long, String, String, String, String, String, String> tuple;

		try
		{
			tuple = (Tuple7<Long, String, String, String, String, String, String>) request.getSession(true).getAttribute("token");

			if(tuple == null)
			{
				return Response.status(Response.Status.FORBIDDEN).build();
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", tuple.y);
		arguments.put("AMIPass", tuple.z);

		arguments.put("clientDN", tuple.t);
		arguments.put("issuerDN", tuple.u);
		arguments.put("notBefore", tuple.v);
		arguments.put("notAfter", tuple.w);

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
