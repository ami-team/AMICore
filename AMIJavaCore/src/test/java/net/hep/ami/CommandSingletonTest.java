package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;

public class CommandSingletonTest
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		Map<String, String> arguments = new HashMap<String, String>();

		try
		{
			/*System.out.println(*/CatalogSingleton.listCatalogs()/*)*/;

			int nb = 3;

			/**/ if(nb == 0)
			{
				arguments.clear();
				arguments.put("country", "FR");
				arguments.put("locality", "Grenoble");
				arguments.put("organization", "CNRS");
				arguments.put("organizationalUnit", "LPSC-AMI");
				arguments.put("commonName", "AMI Root Certification Authority");
				System.out.println(CommandSingleton.executeCommand("GenerateAuthority", arguments).replace(">", ">\n"));
			}
			else if(nb == 1)
			{
				arguments.clear();
				arguments.put("country", "FR");
				arguments.put("locality", "Grenoble");
				arguments.put("organization", "CNRS");
				arguments.put("organizationalUnit", "LPSC-AMI");
				arguments.put("commonName", "localhost");
				arguments.put("password", "");
				System.out.println(CommandSingleton.executeCommand("GenerateCertificate", arguments).replace(">", ">\n"));
			}
			else if(nb == 2)
			{
				arguments.put("parent", "B");
				arguments.put("role", "C");
				System.out.println(">>" + CommandSingleton.executeCommand("AddRole", arguments).replace(">", ">\n"));
			}
			else if(nb == 3)
			{
				arguments.clear();
				arguments.put("AMIUser", "jodier");
				arguments.put("AMIPass", 																							"Xk3mgg256");

				try
				{
					RoleSingleton.checkRoles("GetSessionInfo", arguments);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}

/*			arguments.put("catalog", "self");
			arguments.put("glite", "SELECT COUNT(`router_command`.*) AS `nb` WHERE (1=1)");
			System.out.println(">>" + CommandSingleton.executeCommand("SearchQuery", arguments).replace(">", ">\n"));
*/
/*			arguments.put("parent", "AMI_guest_role");
			arguments.put("role", "B");
			System.out.println(">>" + CommandSingleton.executeCommand("AddRole", arguments).replace(">", ">\n"));
*/
/*
			arguments.put("AMIUser", "jodier");
			arguments.put("AMIPass", "X");
			arguments.put("detachCert", "");
			System.out.println(CommandSingleton.executeCommand("GetSessionInfo", arguments).replace(">", ">\n"));
*/
			//System.out.println(CommandSingleton.executeCommand("CloudListServers", arguments));

			//arguments.put("catalog", "self");
			//arguments.put("glite", "SELECT COUNT(*) AS `nb` WHERE `router_role`.`role`='AMI_guest_role'");
			//System.out.println(CommandSingleton.executeCommand("BrowseQuery", arguments).replace(">", ">\n"));

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
