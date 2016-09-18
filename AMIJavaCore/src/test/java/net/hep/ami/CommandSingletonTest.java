package net.hep.ami;

import java.util.*;

public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		Map<String, String> arguments = new HashMap<String, String>();

		LogSingleton.defaultLogger.debug("Hello World!");

/*		arguments.put("attachCert", "");
		arguments.put("amiLogin", "jodier");
		arguments.put("amiPassword", "Xk3mgg256");
		arguments.put("clientDN", "/C=FR/L=Grenoble/O=CNRS/OU=LPSC-AMI/CN=Jerome Odier");
		arguments.put("issuerDN", "/C=FR/L=Grenoble/O=CNRS/OU=LPSC-AMI/CN=AMI Root Certification Authority");
		System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments).replace(">", ">\n"));
*/
/*
		System.out.println(CommandSingleton.executeCommand("GetSchemes", arguments).replace(">", ">\n"));

		System.out.println("--------");

		System.out.println(CommandSingleton.executeCommand("ListCatalogs", arguments).replace(">", ">\n"));

		System.out.println("--------");

		System.out.println(CommandSingleton.executeCommand("ListDrivers", arguments).replace(">", ">\n"));

		arguments.put("country", "FR");
		arguments.put("locality", "Grenoble");
		arguments.put("organization", "CNRS");
		arguments.put("organizationalUnit", "LPSC-AMI");
		arguments.put("commonName", "Jerome Odier");
		arguments.put("password", "????");
		arguments.put("validity", "10");
		System.out.println(CommandSingleton.executeCommand("GenerateCertificate", arguments).replace(">", ">\n"));
*/
		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
