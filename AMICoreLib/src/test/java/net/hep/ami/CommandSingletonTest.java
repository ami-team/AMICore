package net.hep.ami;

@SuppressWarnings("all")
public class CommandSingletonTest
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		try
		{
			//ClassSingleton.reload();

			//System.out.println(ConfigSingleton.getConfigFileName());
			//MailSingleton.sendMessage("atlas.metadata@cern.ch","fabian.lambert@lpsc.in2p3.fr",null,"Test", "This is a test from JetBrain with atlas.metadata@cern.ch as from.");
			//MailSingleton.sendMessage("ami@lpsc.in2p3.fr","fabian.lambert@lpsc.in2p3.fr",null,"Test", "This is a test from JetBrain with ami@lpsc.in2p3.fr as from.");
			//System.out.println(CommandSingleton.executeCommand("AnalyzeQuery -xql=\"SELECT CONCAT('BYPASS:',`DD`.`BB`, `EE`.`CC`) AS DD, `EE`.`CC` AS UU FROM `DD` `EE` WHERE EE.`AMIUser` LIKE 'MCCubbin%';\"", false).replace("<field", "\n<field"));
			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_command\" -sql=\"SELECT 'BYPASS:' ||`AMIPass` FROM `router_user` WHERE `AMIUser` LIKE 'MCCubbin%';\"", false));
			//System.out.println(CommandSingleton.executeCommand("SearchQuery -catalog=\"self\" -entity=\"router_user\" -mql=\"SELECT * WHERE 1 = 1\" -limit=\"10\" -offset=\"0\"", false).replace(">", ">\n"));
			//System.out.println(CommandSingleton.executeCommand("GetElementInfo -catalog=\"AMI_TEST\" -entity=\"mc_dataset_2\"  -primaryFieldName=\"id\" -primaryFieldValue=\"157571\" -hideBigContent -expandedLinkedElements=\"\" ", false));
			System.out.println(CommandSingleton.executeCommand("GetElementInfo -catalog=\"AMI_TEST\" -entity=\"task\" -primaryFieldName=\"id\" -primaryFieldValue=\"19795054\" -hideBigContent -expandedLinkedElements=\"\" ", false));



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
