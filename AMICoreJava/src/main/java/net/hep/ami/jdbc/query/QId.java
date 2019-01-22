package net.hep.ami.jdbc.query;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;
import net.hep.ami.jdbc.query.mql.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class QId
{
	/*---------------------------------------------------------------------*/

	public enum Type
	{
		NONE,
		CATALOG,
		ENTITY,
		FIELD,
	}

	/*---------------------------------------------------------------------*/

	public static final int MASK_NONE = 0b0000;

	public static final int MASK_CATALOG = 0b0100;
	public static final int MASK_ENTITY = 0b0010;
	public static final int MASK_FIELD = 0b0001;

	public static final int MASK_CATALOG_ENTITY = 0b0110;
	public static final int MASK_ENTITY_FIELD = 0b0011;

	public static final int MASK_CATALOG_ENTITY_FIELD = 0b0111;

	/*---------------------------------------------------------------------*/

	public static final String WILDCARD = "$";

	/*---------------------------------------------------------------------*/

	private boolean m_exclusion = false;

	/*---------------------------------------------------------------------*/

	private String m_catalog = null;
	private String m_entity = null;
	private String m_field = null;

	/*---------------------------------------------------------------------*/

	private final List<QId> m_constraints = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public QId()
	{
		/* DO NOTHING */
	}

	/*---------------------------------------------------------------------*/

	public QId(SchemaSingleton.Column column, boolean isInternal)
	{
		setCatalog(isInternal ? column.internalCatalog : column.externalCatalog);
		setEntity(column.table);
		setField(column.name);
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field)
	{
		setCatalog(catalog);
		setEntity(entity);
		setField(field);
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field, @Nullable Collection<QId> constraints)
	{
		setCatalog(catalog);
		setEntity(entity);
		setField(field);

		if(constraints != null)
		{
			m_constraints.addAll(constraints);
		}
	}

	/*---------------------------------------------------------------------*/

	public static QId parseQId_RuntimeException(String qId)
	{
		try
		{
			return parseQId(qId, Type.FIELD, Type.FIELD);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static QId parseQId_RuntimeException(String qId, Type typeForQId)
	{
		try
		{
			return parseQId(qId, typeForQId, Type.FIELD);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static QId parseQId_RuntimeException(String qId, Type typeForQId, Type typeForConstraints)
	{
		try
		{
			return parseQId(qId, typeForQId, typeForConstraints);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/*---------------------------------------------------------------------*/

	public static QId parseQId(String qId) throws Exception
	{
		return parseQId(qId, Type.FIELD, Type.FIELD);
	}

	/*---------------------------------------------------------------------*/

	public static QId parseQId(String qId, Type typeForQId) throws Exception
	{
		return parseQId(qId, typeForQId, Type.FIELD);
	}

	/*---------------------------------------------------------------------*/

	public static QId parseQId(String qId, Type typeForQId, Type typeForConstraints) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(qId));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		QId result = visitQId(parser.qId(), typeForQId, typeForConstraints);

		/*-----------------------------------------------------------------*/

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static QId visitQId(MQLParser.QIdContext context, Type typeForQId, Type typeForConstraints) throws Exception
	{
		/*-----------------------------------------------------------------*/

		QId result = visitBasicQId(context.m_basicQId, typeForQId);

		/*-----------------------------------------------------------------*/

		if(typeForConstraints != Type.NONE)
		{
			for(MQLParser.ConstraintQIdContext constraintQIdContext: context.m_constraintQIds)
			{
				result.m_constraints.add(visitQId(constraintQIdContext.m_qId, typeForConstraints, typeForConstraints).setExclusion(constraintQIdContext.m_op != null));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static QId visitBasicQId(MQLParser.BasicQIdContext context, Type typeForQId) throws Exception
	{
		QId result;

		final int size = context.m_ids.size();

		/*-----------------------------------------------------------------*/

		switch(typeForQId)
		{
			/*-------------------------------------------------------------*/

			case FIELD:

				/**/ if(size == 3)
				{
					result = new QId(
						context.m_ids.get(0).getText(),
						context.m_ids.get(1).getText(),
						context.m_ids.get(2).getText()
					);
				}
				else if(size == 2)
				{
					result = new QId(
						null,
						context.m_ids.get(0).getText(),
						context.m_ids.get(1).getText()
					);
				}
				else if(size == 1)
				{
					result = new QId(
						null,
						null,
						context.m_ids.get(0).getText()
					);
				}
				else
				{
					throw new Exception("syntax error for field");
				}

				break;

			/*-------------------------------------------------------------*/

			case ENTITY:

				/**/ if(size == 2)
				{
					result = new QId(
						context.m_ids.get(0).getText(),
						context.m_ids.get(1).getText(),
						null
					);
				}
				else if(size == 1)
				{
					result = new QId(
						null,
						context.m_ids.get(0).getText(),
						null
					);
				}
				else
				{
					throw new Exception("syntax error for entity");
				}

				break;

			/*-------------------------------------------------------------*/

			case CATALOG:

				/**/ if(size == 1)
				{
					result = new QId(
						context.m_ids.get(0).getText(),
						null,
						null
					);
				}
				else
				{
					throw new Exception("syntax error for catalog");
				}

				break;

			/*-------------------------------------------------------------*/

			default:

				throw new Exception("invalid type");

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if("*".equals(result.m_catalog)) {
			throw new Exception("`*` not allowed in `catalog` part");
		}

		if("*".equals(result.m_entity)) {
			throw new Exception("`*` not allowed in `entity` part");
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public QId setExclusion(boolean exclusion)
	{
		m_exclusion = exclusion;

		return this;
	}

	public boolean getExclusion()
	{
		return m_exclusion;
	}

	/*---------------------------------------------------------------------*/

	public QId setCatalog(@Nullable String catalog)
	{
		m_catalog = Utility.sqlIdToText(catalog);

		return this;
	}

	public String getCatalog()
	{
		return m_catalog;
	}

	/*---------------------------------------------------------------------*/

	public QId setEntity(@Nullable String entity)
	{
		m_entity = Utility.sqlIdToText(entity);

		return this;
	}

	public String getEntity()
	{
		return m_entity;
	}

	/*---------------------------------------------------------------------*/

	public QId setField(@Nullable String field)
	{
		m_field = Utility.sqlIdToText(field);

		return this;
	}

	public String getField()
	{
		return m_field;
	}

	/*---------------------------------------------------------------------*/

	public List<QId> getConstraints()
	{
		return m_constraints;
	}

	/*---------------------------------------------------------------------*/

	public QId as(int mask)
	{
		return new QId(
			(mask & MASK_CATALOG) != 0 ? m_catalog : null,
			(mask & MASK_ENTITY) != 0 ? m_entity : null,
			(mask & MASK_FIELD) != 0 ? m_field : null,
			m_constraints
		);
	}

	/*---------------------------------------------------------------------*/

	public boolean is(int mask)
	{
		return (((mask & MASK_CATALOG) != 0) == (m_catalog != null))
		       &&
		       (((mask & MASK_ENTITY) != 0) == (m_entity != null))
		       &&
		       (((mask & MASK_FIELD) != 0) == (m_field != null))
		;
	}

	/*---------------------------------------------------------------------*/

	public boolean matches(Object object)
	{
		if(object instanceof QId == false)
		{
			return false;
		}

		return (((QId) this).m_catalog == null || ((QId) object).m_catalog == null || WILDCARD.equals(((QId) this).m_catalog) || WILDCARD.equals(((QId) object).m_catalog) || ((QId) this).m_catalog.equalsIgnoreCase(((QId) object).m_catalog))
		       &&
		       (((QId) this).m_entity == null || ((QId) object).m_entity == null || WILDCARD.equals(((QId) this).m_entity) || WILDCARD.equals(((QId) object).m_entity) || ((QId) this).m_entity.equalsIgnoreCase(((QId) object).m_entity))
		       &&
		       (((QId) this).m_field == null || ((QId) object).m_field == null || WILDCARD.equals(((QId) this).m_field) || WILDCARD.equals(((QId) object).m_field) ||((QId) this). m_field.equalsIgnoreCase(((QId) object).m_field))
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean equals(Object object)
	{
		return equals(object, MASK_CATALOG_ENTITY_FIELD, MASK_NONE);
	}

	/*---------------------------------------------------------------------*/

	public boolean equals(Object object, int mask)
	{
		return equals(object, mask, MASK_NONE);
	}

	/*---------------------------------------------------------------------*/

	public boolean equals(Object object, int mask, int maskForPath)
	{
		if(object instanceof QId == false)
		{
			return false;
		}

		return ((QId) this).toString(mask, maskForPath).equals(((QId) object).toString(mask, maskForPath));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int hashCode()
	{
		return toString(MASK_CATALOG_ENTITY_FIELD, MASK_NONE).hashCode();
	}

	/*---------------------------------------------------------------------*/

	public int hashCode(int mask)
	{
		return toString(mask, MASK_NONE).hashCode();
	}

	/*---------------------------------------------------------------------*/

	public int hashCode(int mask, int maskForPath)
	{
		return toString(mask, maskForPath).hashCode();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return toStringBuilder(MASK_CATALOG_ENTITY_FIELD, MASK_NONE).toString();
	}

	/*---------------------------------------------------------------------*/

	public String toString(int mask)
	{
		return toStringBuilder(mask, MASK_NONE).toString();
	}

	/*---------------------------------------------------------------------*/

	public String toString(int mask, int maskForPath)
	{
		return toStringBuilder(mask, maskForPath).toString();
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(MASK_CATALOG_ENTITY_FIELD, MASK_NONE);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(int mask)
	{
		return toStringBuilder(mask, MASK_NONE);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(int mask, int maskForPath)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(m_exclusion)
		{
			result.append("!");
		}

		/*-----------------------------------------------------------------*/

		List<String> parts = new ArrayList<>();

		if((mask & MASK_CATALOG) != 0 && m_catalog != null) {
			parts.add(Utility.textToSqlId(m_catalog));
		}

		if((mask & MASK_ENTITY) != 0 && m_entity != null) {
			parts.add(Utility.textToSqlId(m_entity));
		}

		if((mask & MASK_FIELD) != 0 && m_field != null) {
			parts.add(Utility.textToSqlId(m_field));
		}

		result.append(String.join(".", parts));

		/*-----------------------------------------------------------------*/

		if(maskForPath != MASK_NONE && m_constraints.isEmpty() == false)
		{
			result.append("{")
			      .append(m_constraints.stream().map(qId -> qId.toString(maskForPath, maskForPath)).collect(Collectors.joining(", ")))
			      .append("}")
			;
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
