package net.hep.ami.jdbc.query.mql;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;

public class Helper
{
	/*---------------------------------------------------------------------*/

	private Helper() {}

	/*---------------------------------------------------------------------*/

	public static String isolate(String stdExternalCatalog, String stdInternalCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, CharSequence expression, boolean isFieldNameOnly) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* BUILD JOINS                                                     */
		/*-----------------------------------------------------------------*/

		QId mainPrimarykeyQId = new QId(stdInternalCatalog, stdEntity, stdPrimaryKey);

		/*-----------------------------------------------------------------*/

		Set<String> localJoinList = new LinkedHashSet<>();

		for(Resolution resolution: resolutionList) 
		{
			String tmpInternalCatalog = resolution.getInternalQId().getCatalog();
			String tmpEntity          = resolution.getExternalQId().getEntity ();

			if(tmpInternalCatalog.equalsIgnoreCase(stdInternalCatalog) == false
			   ||
			   tmpEntity.equalsIgnoreCase(stdEntity) == false
			 ) {
				/*---------------------------------------------------------*/

				/*---------------------------------------------------------*/

				Set<QId> tmpFromSet = new LinkedHashSet<>();

				Set<String> tmpJoinSet = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
				{
					/*-----------------------------------------------------*/

					Set<SchemaSingleton.FrgnKey> tmpWhereList = new LinkedHashSet<>();

					for(SchemaSingleton.FrgnKey frgnKey: /*-------*/ frgnKeys /*-------*/)
					{
						tmpFromSet.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkTable, null));

						tmpFromSet.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkTable, null));

						tmpWhereList.add(frgnKey);
					}

					/*-----------------------------------------------------*/

					if(tmpWhereList.isEmpty() == false)
					{
						/*-------------------------------------------------*/

						SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
						                                 .addFromPart(tmpFromSet.stream().map(x -> x.toString()).collect(Collectors.toList()))
						                                 .addWherePart(expression)
						                                 .addWherePart(tmpWhereList.stream().map(x -> x.toString()).collect(Collectors.toList()))

						;

						/*-------------------------------------------------*/

						tmpJoinSet.add(
							new StringBuilder().append(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
							                   .append(" IN (")
							                   .append(query)
							                   .append(")")
							                   .toString()
						);

						/*-------------------------------------------------*/
					}

					/*-----------------------------------------------------*/
				}

				/*---------------------------------------------------------*/

				if(tmpJoinSet.isEmpty() == false)
				{
					localJoinList.add("(" + String.join(" OR ", tmpJoinSet) + ")");
				}

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/
		/* ISOLATE EXPRESSION                                              */
		/*-----------------------------------------------------------------*/

		if(localJoinList.isEmpty() == false)
		{
			/*-------------------------------------------------------------*/

			SelectObj query = new SelectObj().addSelectPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY_FIELD))
			                                 .addFromPart(mainPrimarykeyQId.toString(QId.MASK_CATALOG_ENTITY))
			                                 .addWherePart(localJoinList)
			;

			/*-------------------------------------------------------------*/

			expression = new StringBuilder().append(mainPrimarykeyQId.toString(isFieldNameOnly == false ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD))
			                                .append(" IN (")
			                                .append(query)
			                                .append(")")
			;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return expression.toString();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple2<List<String>, List<String>> resolve(String stdExternalCatalog, String stdEntity, String stdPrimaryKey, List<Resolution> resolutionList, List<? extends CharSequence> expressionList, String AMIUser, boolean isAdmin, boolean insert) throws Exception
	{
		return new Tuple2<List<String>, List<String>>(null, null);
	}

	/*---------------------------------------------------------------------*/
}
