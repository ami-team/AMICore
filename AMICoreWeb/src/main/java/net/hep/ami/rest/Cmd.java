package net.hep.ami.rest;

import java.io.*;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

@Path("/cmd")
public class Cmd
{
	/*---------------------------------------------------------------------*/

	private static final TypeReference<HashMap<String, String>> s_typeReference = new TypeReference<HashMap<String, String>>() {};

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJson(@QueryParam("token") @DefaultValue("") String token, @QueryParam("converter") @DefaultValue("") String converter, @PathParam("command") String command, String arguments)
	{
		try
		{
			return execute(token, command, new ObjectMapper().readValue(arguments, s_typeReference), converter);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}/xml")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputXml(@QueryParam("token") @DefaultValue("") String token, @PathParam("command") String command, String arguments)
	{
		try
		{
			return execute(token, command, new ObjectMapper().readValue(arguments, s_typeReference), /*----*/""/*----*/);
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputJson(@QueryParam("token") @DefaultValue("") String token, @PathParam("command") String command, String arguments)
	{
		try
		{
			return execute(token, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToJson.xsl");
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}/csv")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputCsv(@QueryParam("token") @DefaultValue("") String token, @PathParam("command") String command, String arguments)
	{
		try
		{
			return execute(token, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToCsv.xsl");
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	@GET
	@Path("{command}/text")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response executeInputJsonOutputText(@QueryParam("token") @DefaultValue("") String token, @PathParam("command") String command, String arguments)
	{
		try
		{
			return execute(token, command, new ObjectMapper().readValue(arguments, s_typeReference), "AMIXmlToText.xsl");
		}
		catch(Exception e)
		{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/*---------------------------------------------------------------------*/

	private Response execute(String token, String command, Map<String, String> arguments, String converter)
	{
		/*-----------------------------------------------------------------*/
		/* CHECK CRENDENTIALS                                              */
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
		/* EXECUTE COMMAND                                                 */
		/*-----------------------------------------------------------------*/

		arguments.put("AMIUser", credentials.x);
		arguments.put("AMIPass", credentials.y);

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
