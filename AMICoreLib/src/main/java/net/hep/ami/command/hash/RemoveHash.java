package net.hep.ami.command.hash;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import net.hep.ami.utility.*;
import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true)
public class RemoveHash extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveHash(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		Update update;

		/*------------------------------------------------------------------------------------------------------------*/

		String id = arguments.get("id");

		if(!Empty.is(id, Empty.STRING_NULL_EMPTY_BLANK))
		{
			update = getQuerier("self").executeSQLUpdate("router_short_url", "DELETE FROM `router_short_url` WHERE `id` = ?0", id);
		}
		else
		{
			String hash = arguments.get("hash");

			if(!Empty.is(hash, Empty.STRING_NULL_EMPTY_BLANK))
			{
				update = getQuerier("self").executeSQLUpdate("router_short_url", "DELETE FROM `router_short_url` WHERE `hash` = ?0", hash);
			}
			else
			{
				throw new Exception("invalid usage");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Remove a hash.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-id=\"\" | -hash=\"\")";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
