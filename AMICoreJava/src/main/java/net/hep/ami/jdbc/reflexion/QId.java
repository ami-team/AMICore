package net.hep.ami.jdbc.reflexion;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;

import net.hep.ami.jdbc.query.mql.*;
import net.hep.ami.utility.parser.*;
import net.hep.ami.utility.*;

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

	public QId(String qId) throws Exception
	{
		this(qId, Type.FIELD, Type.FIELD);
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId, Type typeForQId) throws Exception
	{
		this(qId, typeForQId, Type.FIELD);
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId, Type typeForQId, Type typeForConstraints) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(qId));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		visitQId(this, parser.qId(), typeForQId, typeForConstraints);

		/*-----------------------------------------------------------------*/

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public QId(MQLParser.QIdContext context, Type typeForQId, Type typeForConstraints) throws Exception
	{
		visitQId(this, context, typeForQId, typeForConstraints);
	}

	/*---------------------------------------------------------------------*/

	private QId visitQId(QId result, MQLParser.QIdContext context, Type typeForQId, Type typeForConstraints) throws Exception
	{
		/*-----------------------------------------------------------------*/

		visitBasicQId(result, context.m_basicQId, typeForQId);

		/*-----------------------------------------------------------------*/

		if(typeForConstraints != Type.NONE)
		{
			for(MQLParser.ConstraintQIdContext constraintQIdContext: context.m_constraintQIds)
			{
				result.m_constraints.add(visitQId(new QId().setExclusion(constraintQIdContext.m_op != null), constraintQIdContext.m_qId, typeForConstraints, typeForConstraints));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private QId visitBasicQId(QId result, MQLParser.BasicQIdContext context, Type typeForQId) throws Exception
	{
		final int size = context.m_ids.size();

		/*-----------------------------------------------------------------*/

		switch(typeForQId)
		{
			/*-------------------------------------------------------------*/

			case FIELD:

				/**/ if(size == 3)
				{
					result.m_catalog = Utility.sqlIdToText(context.m_ids.get(0).getText());
					result.m_entity = Utility.sqlIdToText(context.m_ids.get(1).getText());
					result.m_field = Utility.sqlIdToText(context.m_ids.get(2).getText());
				}
				else if(size == 2)
				{
					result.m_entity = Utility.sqlIdToText(context.m_ids.get(0).getText());
					result.m_field = Utility.sqlIdToText(context.m_ids.get(1).getText());
				}
				else if(size == 1)
				{
					result.m_field = Utility.sqlIdToText(context.m_ids.get(0).getText());
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
					result.m_catalog = Utility.sqlIdToText(context.m_ids.get(0).getText());
					result.m_entity = Utility.sqlIdToText(context.m_ids.get(1).getText());
				}
				else if(size == 1)
				{
					result.m_entity = Utility.sqlIdToText(context.m_ids.get(0).getText());
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
					result.m_catalog = Utility.sqlIdToText(context.m_ids.get(0).getText());
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

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field)
	{
		this(catalog, entity, field, null);
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field, @Nullable List<QId> constraints)
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

	public boolean matches(QId qId)
	{
		return (m_catalog == null || qId.m_catalog == null || "$".equals(m_catalog) || "$".equals(qId.m_catalog) || m_catalog.equalsIgnoreCase(qId.m_catalog))
		       &&
		       (m_entity == null || qId.m_entity == null || "$".equals(m_entity) || "$".equals(qId.m_entity) || m_entity.equalsIgnoreCase(qId.m_entity))
		       &&
		       (m_field == null || qId.m_field == null || "$".equals(m_field) || "$".equals(qId.m_field) || m_field.equalsIgnoreCase(qId.m_field))
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int hashCode()
	{
		return hashCode(MASK_CATALOG_ENTITY_FIELD, MASK_NONE);
	}

	/*---------------------------------------------------------------------*/

	public int hashCode(int mask)
	{
		return hashCode(mask, MASK_CATALOG_ENTITY_FIELD);
	}

	/*---------------------------------------------------------------------*/

	public int hashCode(int mask, int maskForPath)
	{
		return toString(mask, maskForPath).hashCode();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean equals(Object anObject)
	{
		return equals(anObject, MASK_CATALOG_ENTITY_FIELD, MASK_NONE);
	}

	/*---------------------------------------------------------------------*/


	public boolean equals(Object anObject, int mask)
	{
		return equals(anObject, mask, MASK_CATALOG_ENTITY_FIELD);
	}

	/*---------------------------------------------------------------------*/

	public boolean equals(Object anObject, int mask, int maskForPath)
	{
		if(anObject instanceof QId)
		{
			QId that = (QId) anObject;

			return this == that || toString(mask, maskForPath).equals(that.toString(mask, maskForPath));
		}

		return false;
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
		return toStringBuilder(mask, MASK_CATALOG_ENTITY_FIELD).toString();
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
		return toStringBuilder(mask, MASK_CATALOG_ENTITY_FIELD);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(int mask, int maskForPath)
	{
		StringBuilder result = new StringBuilder();

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

		/*-----------------------------------------------------------------*/

		if(m_exclusion)
		{
			result.append("!");
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
