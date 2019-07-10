package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class SetWidgetProperties extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SetWidgetProperties(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/


		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the given widget property.";
	}

	/*---------------------------------------------------------------------*/
}
