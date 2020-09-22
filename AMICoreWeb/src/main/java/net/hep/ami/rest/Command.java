package net.hep.ami.rest;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

import java.io.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;

import net.hep.ami.*;

import org.jetbrains.annotations.*;

@Path("/command")
public class Command
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final TypeReference<LinkedHashMap<String, String>> s_typeReference = new TypeReference<>() {};

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, String> s_help = new LinkedHashMap<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		s_help.put("help", "");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private Map<String, String> parseArguments(@NotNull String json) throws Exception
	{
		return new ObjectMapper().readValue(!json.isEmpty() ? json : "{}", s_typeReference);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	@GET
	@Path("{command}/help/{format}")
	public Response help2(
		@Context HttpServletRequest request,
		@PathParam("command") String command,
		@PathParam("format") String format
	 ) {
		try
		{
			/**/ if("xml".equalsIgnoreCase(format)) {
				return execute(request, command, s_help, "AMIXmlToXml.xsl");
			}
			else if("json".equalsIgnoreCase(format)) {
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

	/*----------------------------------------------------------------------------------------------------------------*/

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
			return execute(request, command, parseArguments(arguments), converter);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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
			/**/ if("xml".equalsIgnoreCase(format)) {
				return execute(request, command, parseArguments(arguments), "AMIXmlToXml.xsl");
			}
			else if("json".equalsIgnoreCase(format)) {
				return execute(request, command, parseArguments(arguments), "AMIXmlToJson.xsl");
			}
			else if("csv".equalsIgnoreCase(format)) {
				return execute(request, command, parseArguments(arguments), "AMIXmlToCsv.xsl");
			}
			else if("text".equalsIgnoreCase(format)) {
				return execute(request, command, parseArguments(arguments), "AMIXmlToText.xsl");
			}
			else {
				return execute(request, command, parseArguments(arguments), /*----*/""/*----*/);
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private Response execute(@NotNull HttpServletRequest request, @NotNull String command, @NotNull Map<String, String> arguments, String converter)
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET SESSION OF REQUEST                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(7200);

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK CREDENTIALS                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		Token.Tuple tuple;

		try
		{
			tuple = (Token.Tuple) session.getAttribute("token");

			if(tuple == null)
			{
				return Response.ok("no token").status(Response.Status.FORBIDDEN).build();
			}
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE COMMAND                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		arguments.put("AMIUser", tuple.x);
		arguments.put("AMIPass", tuple.y);

		arguments.put("clientDN", tuple.z);
		arguments.put("issuerDN", tuple.t);
		arguments.put("notBefore", tuple.u);
		arguments.put("notAfter", tuple.v);

		/**/

		arguments.put("isSecure", request.isSecure() ? "true"
		                                             : "false"
		);

		/**/

		String agent = request.getHeader("User-Agent");

		if(agent != null
		   &&
		   (
		     agent.startsWith("cami")
		     ||
		     agent.startsWith("jami")
		     ||
		     agent.startsWith("pami")
		     ||
		     agent.startsWith("pyAMI")
		   )
		 ) {
			arguments.put("userAgent", agent);
		}
		else
		{
			arguments.put("userAgent", "web");
		}

		/**/

		arguments.put("userSession", session.getId());

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* CONVERT RESULT                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		String mime;

		if(!converter.isEmpty())
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

		/*------------------------------------------------------------------------------------------------------------*/

		return Response.ok(data + " | " + session.getId()).type(mime).build();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
