package net.hep.ami.command.misc;

import java.io.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class SendFile extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SendFile(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String fileName = arguments.containsKey("fileName") ? arguments.get("fileName").trim()
		                                                  : ""
		;

		String filePath = arguments.containsKey("filePath") ? arguments.get("filePath").trim()
		                                                  : ""
		;

		String fileData = arguments.containsKey("fileData") ? arguments.get("fileData") ////()
		                                                  : ""
		;

		if(fileName.isEmpty()
		   ||
		   fileName.contains("~")
		   ||
		   fileName.contains("..")
		   ||
		   filePath.contains("~")
		   ||
		   filePath.contains("..")
		 ) {
			throw new Exception("unauthorized path");
		}

		/*-----------------------------------------------------------------*/

		String rootPath = ConfigSingleton.getProperty("server_storage_root").trim();

		if(rootPath.isEmpty()
		   ||
		   rootPath.equals("/")
		 ) {
			throw new Exception("wrong server storage configuration");
		}

		/*-----------------------------------------------------------------*/

		File absPath = new File(rootPath, filePath);

		absPath.mkdirs();

		/*-----------------------------------------------------------------*/

		OutputStream outputStream = new FileOutputStream(new File(absPath, fileName), false);

		try
		{
			outputStream.write(fileData.getBytes());
			outputStream.flush();
		} 
		finally
		{
			outputStream.close();
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Send a file on the server.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-fileName=\"\" (-filePath=\"\")? (-fileData=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
