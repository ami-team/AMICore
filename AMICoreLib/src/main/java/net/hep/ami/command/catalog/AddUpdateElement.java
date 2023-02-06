package net.hep.ami.command.catalog;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_WRITER", visible = true)
public class AddUpdateElement extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddUpdateElement(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? Pattern.quote(arguments.get("separator"))
		                                                      : ","
		;

		String[] keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
		                                                        : new String[] {}
		;

		String[] keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
		                                                        : new String[] {}
		;

		String where = arguments.get("where");

		if(catalog == null || entity == null || keyFields.length != keyValues.length || (keyFields.length == 0 && where == null))
		{
			throw new Exception("invalid usage");
		}

		/*----------------------------------------------------------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(int i = 0; i < keyFields.length; i++)
		{
			whereList.add(QId.parseQId(keyFields[i], QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD) + " = '" + keyValues[i].trim().replace("'", "''") + "'");
		}

		/*----------------------------------------------------------------------------------------------------------------*/

		XQLSelect query = new XQLSelect().addSelectPart("COUNT(*)")
		                                 .addWherePart(whereList)
		                                 .addWherePart(where)
		;

		/*------------------------------------------------------------------------------------------------------------*/

		String nb = getQuerier(catalog).executeMQLQuery(entity, query.toString()).getAll().get(0).getValue(0);

		/**/ if("0".equalsIgnoreCase(nb))
		{
			return new AddElement(m_userRoles, m_arguments, m_transactionId).main(m_arguments);
		}
		else if("1".equalsIgnoreCase(nb))
		{
			return new UpdateElements(m_userRoles, m_arguments, m_transactionId).main(m_arguments);
		}
		else
		{
			throw new Exception("more than one element found");
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Add or update one element.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? (-single)? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
