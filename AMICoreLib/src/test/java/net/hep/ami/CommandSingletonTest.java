package net.hep.ami;

@SuppressWarnings("all")
public class CommandSingletonTest
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		try
		{
			ClassSingleton.reload();

			//System.out.println(ConfigSingleton.getConfigFileName());
			MailSingleton.sendMessage("atlas.metadata@cern.ch","fabian.lambert@lpsc.in2p3.fr",null,"Test", "This is a test from JetBrain with atlas.metadata@cern.ch as from.");
			MailSingleton.sendMessage("ami@lpsc.in2p3.fr","fabian.lambert@lpsc.in2p3.fr",null,"Test", "This is a test from JetBrain with ami@lpsc.in2p3.fr as from.");


			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_user\" -mql=\"SELECT * WHERE 1 = 1\" -limit=\"10\" -offset=\"0\"", false).replace(">", ">\n"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}

		System.out.println("bye.");

		System.exit(0);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
