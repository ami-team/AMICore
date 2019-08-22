package net.hep.ami.command.misc;

import java.io.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class SendFile extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SendFile(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String fileName = arguments.getOrDefault("fileName", "");
		String filePath = arguments.getOrDefault("filePath", "");
		String fileData = arguments.getOrDefault("fileData", "");

		fileName = fileName.trim();
		filePath = filePath.trim();

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

		String rootPath = ConfigSingleton.getProperty("storage_root_path").trim();

		if(rootPath.isEmpty()
		   ||
		   rootPath.equals("/")
		 ) {
			throw new Exception("wrong storage root path");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		File absPath = new File(rootPath, filePath);

		absPath.mkdirs();

		/*------------------------------------------------------------------------------------------------------------*/

		try(OutputStream outputStream = new FileOutputStream(new File(absPath, fileName), false))
		{
			outputStream.write(fileData.getBytes());
			outputStream.flush();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Send a file on the server.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-fileName=\"\" (-filePath=\"\")? (-fileData=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
