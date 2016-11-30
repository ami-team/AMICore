package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class FindCommands extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public FindCommands(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET TRANSACTIONAL QUERIER                                       */
		/*-----------------------------------------------------------------*/

		TransactionalQuerier transactionalQuerier = getQuerier("self");

		/*-----------------------------------------------------------------*/
		/* FIND COMMANDS                                                   */
		/*-----------------------------------------------------------------*/

		List<String> classNames = new ArrayList<String>(ClassFinder.findClassNames("net.hep.ami.command"));

		Collections.sort(classNames);

		/*-----------------------------------------------------------------*/
		/* ADD COMMANDS                                                    */
		/*-----------------------------------------------------------------*/

		Set<String> commands = new HashSet<String>();

		for(String className: classNames)
		{
			if(RouterBuilder.addCommand(transactionalQuerier, className))
			{
				commands.add(className);
			}
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, " + commands + "]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Automatically find commands.";
	}

	/*---------------------------------------------------------------------*/
}
