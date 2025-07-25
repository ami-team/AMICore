package net.hep.ami.jdbc.query.mql;

import lombok.*;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.reflexion.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class Helper
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private Helper() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Set<String> getFromSetFromResolutionList(@NotNull QId primaryKey, @NotNull List<Resolution> resolutionList)
	{
		Set<String> result = new LinkedHashSet<>();

		/*------------------------------------------------------------------------------------------------------------*/

		result.add(primaryKey.toString(QId.MASK_CATALOG_ENTITY));

		for(Resolution resolution: resolutionList)
		{
			result.add(resolution.getInternalQId().toString(QId.MASK_CATALOG_ENTITY));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static boolean isTrivialQuery(@NotNull List<Resolution> resolutionList)
	{
		if(!resolutionList.isEmpty())
		{
			int oldPathHashCode = 0;
			int newPathHashCode = 0;

			for(Resolution resolution: resolutionList)
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(resolution.getPaths().size() > 1)
				{
					return false;
				}

				/*----------------------------------------------------------------------------------------------------*/

				newPathHashCode = resolution.getPathHashCode();

				/*----------------------------------------------------------------------------------------------------*/

				if(newPathHashCode != 1)
				{
					if(oldPathHashCode == 0)
					{
						oldPathHashCode = newPathHashCode;
					}
					else
					{
						if(oldPathHashCode != newPathHashCode)
						{
							return false;
						}
					}
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		return true;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class FromAndWhereSets
	{
		@NotNull private final Set<String> fromSet;
		@NotNull private final Set<String> whereSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _, _, _, _ -> new")
	public static Helper.FromAndWhereSets getIsolatedPath(String catalog, QId primaryKey, List<Resolution> resolutionList, int skip, boolean isFieldNameOnly) throws Exception
	{
		boolean dualNeeded = (CatalogSingleton.getFlags(catalog) & DriverMetadata.FLAG_HAS_DUAL) == DriverMetadata.FLAG_HAS_DUAL;

 		/*------------------------------------------------------------------------------------------------------------*/
		/* BUILD GLOBAL FROM SET                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> globalFromSet = getFromSetFromResolutionList(primaryKey, resolutionList);

		/*------------------------------------------------------------------------------------------------------------*/
		/* ISOLATE JOINS                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> globalWhereSet = new LinkedHashSet<>();

		/*------------------------------------------------------------------------------------------------------------*/

		if(isTrivialQuery(resolutionList))
		{
			for(Resolution resolution: resolutionList)
			{
				globalFromSet.add(resolution.getInternalQId().toString(QId.MASK_CATALOG_ENTITY));

				for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
				{
					int cnt = 0;

					for(SchemaSingleton.FrgnKey frgnKey: /*----*/ frgnKeys /*----*/)
					{
						if(cnt++ < skip)
						{
							continue;
						}

						globalFromSet.add(new QId(frgnKey.fkInternalCatalog, frgnKey.fkEntity, null).toString(QId.MASK_CATALOG_ENTITY));
						globalFromSet.add(new QId(frgnKey.pkInternalCatalog, frgnKey.pkEntity, null).toString(QId.MASK_CATALOG_ENTITY));

						globalWhereSet.add(frgnKey.toString());
					}
				}
			}

			return new FromAndWhereSets(globalFromSet, globalWhereSet);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		QId qId;

		String tmp;

		String query;

		for(Resolution resolution: resolutionList)
		{
			if(resolution.getMaxPathLen() == 0)
			{
				continue;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			Set<String> idSet = new TreeSet<>();

			Set<String> idSignatureSet = new TreeSet<>();

			Set<String> whereSet1 = new LinkedHashSet<>();
			Set<String> whereSet2 = new LinkedHashSet<>();

			idSet.add(primaryKey.toString(!isFieldNameOnly ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD));

			for(SchemaSingleton.FrgnKeys frgnKeys: resolution.getPaths())
			{
				/*----------------------------------------------------------------------------------------------------*/

				int cnt = 0;

				Set<String> tmpIdSet = new TreeSet<>();

				Set<String> tmpFromSet = new TreeSet<>();

				Set<String> tmpWhereSet = new LinkedHashSet<>();

				for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
				{
					if(cnt++ < skip)
					{
						continue;
					}

					/*------------------------------------------------------------------------------------------------*/

					qId = new QId(frgnKey.fkInternalCatalog, frgnKey.fkEntity, frgnKey.fkField);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(!globalFromSet.contains(tmp))
					{
						tmpFromSet.add(tmp);
					}
					else
					{
						tmp = new QId(SchemaSingleton.getPrimaryKey(frgnKey.fkExternalCatalog, frgnKey.fkEntity), true).toString(!isFieldNameOnly ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD);

						tmpIdSet.add(tmp);
						idSet.add(tmp);
					}

					/*------------------------------------------------------------------------------------------------*/

					qId = new QId(frgnKey.pkInternalCatalog, frgnKey.pkEntity, frgnKey.pkField);

					tmp = qId.toString(QId.MASK_CATALOG_ENTITY);

					if(!globalFromSet.contains(tmp))
					{
						tmpFromSet.add(tmp);
					}
					else
					{
						tmp = new QId(SchemaSingleton.getPrimaryKey(frgnKey.pkExternalCatalog, frgnKey.pkEntity), true).toString(!isFieldNameOnly ? QId.MASK_CATALOG_ENTITY_FIELD : QId.MASK_FIELD);

						tmpIdSet.add(tmp);
						idSet.add(tmp);
					}

					/*------------------------------------------------------------------------------------------------*/

					tmpWhereSet.add(frgnKey.toString());

					/*------------------------------------------------------------------------------------------------*/
				}

				/*----------------------------------------------------------------------------------------------------*/

				if(!tmpIdSet.isEmpty())
				{
					/*------------------------------------------------------------------------------------------------*/

					idSignatureSet.add(String.join("~", tmpIdSet));

					/*------------------------------------------------------------------------------------------------*/

					if(tmpFromSet.isEmpty())
					{
						if(dualNeeded)
						{
							tmpFromSet.add("DUAL");
						}
					}

					/*------------------------------------------------------------------------------------------------*/

					query = new XQLSelect().addSelectPart(tmpIdSet)
					                       .addFromPart(tmpFromSet)
					                       .addWherePart(tmpWhereSet)
					                       .toString()
					;

					whereSet1.add(query);

					/*------------------------------------------------------------------------------------------------*/

					query = new StringBuilder().append("(")
					                           .append(String.join(", ", tmpIdSet))
					                           .append(") IN (")
					                           .append(query)
					                           .append(")")
					                           .toString()
					;

					whereSet2.add(query);

					/*------------------------------------------------------------------------------------------------*/
				}

				/*----------------------------------------------------------------------------------------------------*/
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(!whereSet1.isEmpty())
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(idSignatureSet.size() == 1)
				{
					query = String.join(" UNION ", whereSet1);
				}
				else
				{
					query = String.join(((" OR ")), whereSet2);

					XQLSelect selectObj = new XQLSelect().addSelectPart(idSet)
					                                     .addWherePart(query)
					;

					if(dualNeeded)
					{
						selectObj.addFromPart("DUAL");
					}

					query = selectObj.toString();
				}

				/*----------------------------------------------------------------------------------------------------*/

				globalWhereSet.add(new StringBuilder().append("(")
				                                     .append(String.join(", ", idSet))
				                                     .append(") IN (")
				                                     .append(query)
				                                     .append(")")
				                                     .toString()
				);

				/*----------------------------------------------------------------------------------------------------*/
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new FromAndWhereSets(globalFromSet, globalWhereSet);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getIsolatedExpression(String catalog, QId primaryKey, List<Resolution> resolutionList, CharSequence expression, int skip, boolean noComp, boolean noEntity, boolean noPrimaryEntity) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* ISOLATE JOINS                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		FromAndWhereSets fromAndWhereSets = getIsolatedPath(
			catalog,
			primaryKey,
			resolutionList,
			skip,
			false
		);

		/*------------------------------------------------------------------------------------------------------------*/
		/* ISOLATE EXPRESSION                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		if(noComp || !fromAndWhereSets.getWhereSet().isEmpty())
		{
			/*--------------------------------------------------------------------------------------------------------*/

			if(noPrimaryEntity)
			{
				if(!"jdbc:oracle".equals(CatalogSingleton.getProto(catalog)))
				{
					fromAndWhereSets.getFromSet().remove(primaryKey.toString(QId.MASK_CATALOG_ENTITY));
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			XQLSelect query = new XQLSelect().addSelectPart(primaryKey.toString(QId.MASK_CATALOG_ENTITY_FIELD))
			                                 .addFromPart(fromAndWhereSets.getFromSet())
			                                 .addWherePart("(" + expression + ")")
			                                 .addWherePart(fromAndWhereSets.getWhereSet())
			;

			/*-------------------------------------------------------------*/

			StringBuilder stringBuilder = new StringBuilder();

			if(!noComp)
			{
				if(!noEntity)
				{
					stringBuilder.append(primaryKey.toString(QId.MASK_CATALOG_ENTITY_FIELD)).append(" IN ");
				}
				else
				{
					stringBuilder.append(primaryKey.toString(QId.MASK_FIELD)).append(" IN ");
				}
			}

			stringBuilder.append("(")
			             .append(query)
			             .append(")")
			;

			/*--------------------------------------------------------------------------------------------------------*/

			return stringBuilder.toString();
		}
		else
		{
			return expression.toString();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Pattern HHH = Pattern.compile("^\\s*\\?[0-9]+\\s*$");
	private static final Pattern III = Pattern.compile("^\\s*\\?\\^[0-9]+\\s*$");
	private static final Pattern JJJ = Pattern.compile("^\\s*\\?#[0-9]+\\s*$");
	private static final Pattern KKK = Pattern.compile("^\\s*'(''|[^'])*'\\s*$");

	/*----------------------------------------------------------------------------------------------------------------*/


	@Getter
	@Setter
	@AllArgsConstructor
	public static class FieldsAndValues
	{
		@NotNull private final List<String> fieldPart;
		@NotNull private final List<String> valuePart;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static class isolatedExpressionParams
	{
		@NotNull private final AMIValue<String> catalog;
		@NotNull private final AMIValue<QId> primaryKey;
		@NotNull private final List<Resolution> resolutionList;
		@NotNull private final Set<CharSequence> expression;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _, _, _, _, _, _ -> new")
	public static FieldsAndValues resolve(@NotNull String catalog, @NotNull QId primaryKey, @NotNull List<Resolution> resolutionList, @NotNull List<? extends CharSequence> expressionList, @NotNull String AMIUser, boolean isAdmin, boolean insert) throws Exception
	{
		final int nb1 = resolutionList.size();
		final int nb2 = expressionList.size();

		if(nb1 != nb2)
		{
			throw new Exception("internal error");
		}

		boolean backslashEscapes = (CatalogSingleton.getFlags(catalog) & DriverMetadata.FLAG_BACKSLASH_ESCAPE) == DriverMetadata.FLAG_BACKSLASH_ESCAPE;

		/*------------------------------------------------------------------------------------------------------------*/
		/* GROUP FIELDS                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		String field;

		Resolution resolution;
		CharSequence expression;

		Resolution tmpResolution;
		CharSequence tmpExpression;

		SchemaSingleton.Column column;

		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> locked = new HashSet<>();

		isolatedExpressionParams isolatedExpressionParams;

		Map<String, isolatedExpressionParams> entries = new LinkedHashMap<>();

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < nb1; i++)
		{
			resolution = resolutionList.get(i);
			expression = expressionList.get(i);

			column = resolution.getColumn();

			/*--------------------------------------------------------------------------------------------------------*/

			/**/ if(column.automatic
			        ||
			        column.created
			        ||
			        column.createdBy
			        ||
			        column.modified
			        ||
			        column.modifiedBy
			 ) {
				continue;
			}
			else if(column.adminOnly)
			{
				if(!isAdmin)
				{
					throw new Exception("user `" + AMIUser + "` not allow to modify admin-only field " + new QId(column, false));
				}

				/**/ if(HHH.matcher(expression).matches())
				{
					expression = "?<" + column.jdbcType + ">" + expression.toString().substring(1);
				}
				else if(III.matcher(expression).matches())
				{
					expression = "?^<" + column.jdbcType + ">" + expression.toString().substring(2);
				}
				else if(JJJ.matcher(expression).matches())
				{
					expression = "?#<" + column.jdbcType + ">" + expression.toString().substring(2);
				}
			}
			else if(column.hashed)
			{
				if(!isAdmin)
				{
					throw new Exception("user `" + AMIUser + "` not allow to modify hashed field " + new QId(column, false));
				}

				/**/ if(HHH.matcher(expression).matches())
				{
					expression = "?^<" + column.jdbcType + ">" + expression.toString().substring(1);
				}
				else if(III.matcher(expression).matches())
				{
					expression = "?^<" + column.jdbcType + ">" + expression.toString().substring(2);
				}
				else if(KKK.matcher(expression).matches())
				{
					expression = Utility.textToSqlVal(
							expression.toString(),
							backslashEscapes
					);
				}
			}
			else if(column.crypted)
			{
				if(!isAdmin)
				{
					throw new Exception("user `" + AMIUser + "` not allow to modify crypted field " + new QId(column, false));
				}

				/**/ if(HHH.matcher(expression).matches())
				{
					expression = "?#<" + column.jdbcType + ">" + expression.toString().substring(1);
				}
				else if(JJJ.matcher(expression).matches())
				{
					expression = "?#<" + column.jdbcType + ">" + expression.toString().substring(2);
				}
				else if(KKK.matcher(expression).matches())
				{
					expression = Utility.textToSqlVal(
						expression.toString(),
						backslashEscapes
					);
				}
			}
			else
			{
				/**/ if(HHH.matcher(expression).matches())
				{
					expression = "?<" + column.jdbcType + ">" + expression.toString().substring(1);
				}
				else if(III.matcher(expression).matches())
				{
					expression = "?^<" + column.jdbcType + ">" + expression.toString().substring(2);
				}
				else if(JJJ.matcher(expression).matches())
				{
					expression = "?#<" + column.jdbcType + ">" + expression.toString().substring(2);
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(resolution.getMaxPathLen() > 0)
			{
				tmpResolution = new Resolution();

				tmpExpression = resolution.getInternalQId().toString()
				                + " = " +
				                /*---------*/ expression /*---------*/
				;

				for(SchemaSingleton.FrgnKeys path: resolution.getPaths())
				{
					/*------------------------------------------------------------------------------------------------*/

					if(!path.get(0).fkEntity.equals(primaryKey.getEntity())
					   ||
					   !path.get(0).fkInternalCatalog.equals(primaryKey.getCatalog())
					 ) {
						continue;
					}

					/*------------------------------------------------------------------------------------------------*/

					field = path.get(0).fkField;

					if(!locked.contains(field))
					{
						tmpResolution.addPath(resolution.getExternalQId(), resolution.getColumn(), path);
					}
					else
					{
						continue;
					}

					/*------------------------------------------------------------------------------------------------*/

					isolatedExpressionParams = entries.get(field);

					if(isolatedExpressionParams == null)
					{
						entries.put(field, isolatedExpressionParams = new isolatedExpressionParams(
							new AMIValue<>(),
							new AMIValue<>(),
							new ArrayList<>(),
							new LinkedHashSet<>()
						));
					}

					isolatedExpressionParams.getCatalog().value = path.get(0).pkExternalCatalog;
					isolatedExpressionParams.getPrimaryKey().value = new QId(path.get(0).pkInternalCatalog, path.get(0).pkEntity, path.get(0).pkField);

					isolatedExpressionParams.getResolutionList().add(tmpResolution);
					isolatedExpressionParams.getExpression().add(tmpExpression);

					/*------------------------------------------------------------------------------------------------*/
				}
			}
			else
			{
				/*----------------------------------------------------------------------------------------------------*/

				field = resolution.getColumn().field;

				locked.add(field);

				/*----------------------------------------------------------------------------------------------------*/

				isolatedExpressionParams = entries.get(field);

				if(isolatedExpressionParams == null)
				{
					entries.put(field, isolatedExpressionParams = new isolatedExpressionParams(
						new AMIValue<>(),
						new AMIValue<>(),
						new ArrayList<>(),
						new LinkedHashSet<>()
					));
				}

				isolatedExpressionParams.getCatalog().value = /*--------*/ null /*--------*/;
				isolatedExpressionParams.getPrimaryKey().value = /*----------------------------------*/ null /*----------------------------------*/;

				isolatedExpressionParams.getResolutionList().clear();
				isolatedExpressionParams.getExpression().clear();

				isolatedExpressionParams.getExpression().add(expression);

				/*----------------------------------------------------------------------------------------------------*/
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ISOLATE EXPRESSIONS                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		List<String> X = new ArrayList<>();
		List<String> Y = new ArrayList<>();

		for(Map.Entry<String, isolatedExpressionParams> entry: entries.entrySet())
		{
			field = entry.getKey();
			isolatedExpressionParams = entry.getValue();

			X.add(Utility.textToSqlId(field));

			if(isolatedExpressionParams.getCatalog().value != null
			   &&
			   isolatedExpressionParams.getPrimaryKey().value != null
			 ) {
				Y.add(getIsolatedExpression(isolatedExpressionParams.getCatalog().value, isolatedExpressionParams.getPrimaryKey().value, isolatedExpressionParams.getResolutionList(), String.join(" AND ", isolatedExpressionParams.getExpression()), 1, true, true, false));
			}
			else
			{
				Y.add(isolatedExpressionParams.getExpression().iterator().next().toString());
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* FILL RESERVED FIELDS                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		for(SchemaSingleton.Column tmp: SchemaSingleton.getEntityInfo(catalog, primaryKey.getEntity()).columns.values())
		{
			if(tmp.created && insert) {
				X.add(Utility.textToSqlId(tmp.field)); Y.add("CURRENT_TIMESTAMP");
			}

			if(tmp.createdBy && insert) {
				X.add(Utility.textToSqlId(tmp.field)); Y.add(Utility.textToSqlVal(AMIUser, backslashEscapes));
			}

			if(tmp.modified) {
				X.add(Utility.textToSqlId(tmp.field)); Y.add("CURRENT_TIMESTAMP");
			}

			if(tmp.modifiedBy) {
				X.add(Utility.textToSqlId(tmp.field)); Y.add(Utility.textToSqlVal(AMIUser, backslashEscapes));
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new FieldsAndValues(X, Y);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
