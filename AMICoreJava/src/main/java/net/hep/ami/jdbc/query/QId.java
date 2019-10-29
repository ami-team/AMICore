package net.hep.ami.jdbc.query;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;

import net.hep.ami.jdbc.query.mql.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public final class QId
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public enum Type
	{
		NONE,
		CATALOG,
		ENTITY,
		FIELD,
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final int MASK_NONE = 0b0000;

	public static final int MASK_CATALOG = 0b0100;
	public static final int MASK_ENTITY = 0b0010;
	public static final int MASK_FIELD = 0b0001;

	public static final int MASK_CATALOG_ENTITY = 0b0110;
	public static final int MASK_ENTITY_FIELD = 0b0011;

	public static final int MASK_CATALOG_ENTITY_FIELD = 0b0111;

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final String WILDCARD = "$";

	/*----------------------------------------------------------------------------------------------------------------*/

	private boolean m_exclusion = false;

	/*----------------------------------------------------------------------------------------------------------------*/

	private String m_catalog = null;
	private String m_entity = null;
	private String m_field = null;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final List<QId> m_constraints = new ArrayList<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	public QId()
	{
		/* DO NOTHING */
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public QId(@NotNull SchemaSingleton.Column column, boolean isInternal)
	{
		setCatalog(isInternal ? column.internalCatalog : column.externalCatalog);
		setEntity(column.entity);
		setField(column.field);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public QId(@NotNull SchemaSingleton.Column column, boolean isInternal, @Nullable Collection<QId> constraints)
	{
		setCatalog(isInternal ? column.internalCatalog : column.externalCatalog);
		setEntity(column.entity);
		setField(column.field);

		if(constraints != null)
		{
			m_constraints.addAll(constraints);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field)
	{
		setCatalog(catalog);
		setEntity(entity);
		setField(field);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId parseQId_RuntimeException(@NotNull String qId)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId parseQId_RuntimeException(@NotNull String qId, @NotNull Type typeForQId)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId parseQId_RuntimeException(@NotNull String qId, @NotNull Type typeForQId, @NotNull Type typeForConstraints)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId parseQId(@NotNull String qId) throws Exception
	{
		return parseQId(qId, Type.FIELD, Type.FIELD);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId parseQId(@NotNull String qId, @NotNull Type typeForQId) throws Exception
	{
		return parseQId(qId, typeForQId, Type.FIELD);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId parseQId(@NotNull String qId, @NotNull Type typeForQId, @NotNull Type typeForConstraints) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		QIdLexer lexer = new QIdLexer(CharStreams.fromString(qId));

		QIdParser parser = new QIdParser(new CommonTokenStream(lexer));

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		QId result = visitQId(parser.qId(), typeForQId, typeForConstraints);

		/*------------------------------------------------------------------------------------------------------------*/

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static QId visitQId(@NotNull QIdParser.QIdContext context, @NotNull Type typeForQId, @NotNull Type typeForConstraints) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		QId result = buildBasicQId(context.getText(), context.m_basicQId.m_ids, typeForQId);

		/*------------------------------------------------------------------------------------------------------------*/

		if(typeForConstraints != Type.NONE)
		{
			for(QIdParser.ConstraintQIdContext constraintQIdContext: context.m_constraintQIds)
			{
				result.m_constraints.add(buildBasicQId(constraintQIdContext.m_qId.m_basicQId.getText(), constraintQIdContext.m_qId.m_basicQId.m_ids, typeForConstraints).setExclusion(constraintQIdContext.m_op != null));
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static QId buildBasicQId(@NotNull String text, @NotNull List<Token> m_ids, @NotNull Type typeForQId) throws Exception
	{
		QId result;

		final int size = m_ids.size();

		/*------------------------------------------------------------------------------------------------------------*/

		switch(typeForQId)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			case FIELD:

				/**/ if(size == 3)
				{
					result = new QId(
						m_ids.get(0).getText(),
						m_ids.get(1).getText(),
						m_ids.get(2).getText()
					);
				}
				else if(size == 2)
				{
					result = new QId(
						null,
						m_ids.get(0).getText(),
						m_ids.get(1).getText()
					);
				}
				else if(size == 1)
				{
					result = new QId(
						null,
						null,
						m_ids.get(0).getText()
					);
				}
				else
				{
					throw new Exception("syntax error for field for " + text);
				}

				break;

			/*--------------------------------------------------------------------------------------------------------*/

			case ENTITY:

				/**/ if(size == 2)
				{
					result = new QId(
						m_ids.get(0).getText(),
						m_ids.get(1).getText(),
						null
					);
				}
				else if(size == 1)
				{
					result = new QId(
						null,
						m_ids.get(0).getText(),
						null
					);
				}
				else
				{
					throw new Exception("syntax error for entity for " + text);
				}

				break;

			/*--------------------------------------------------------------------------------------------------------*/

			case CATALOG:

				/**/ if(size == 1)
				{
					result = new QId(
						m_ids.get(0).getText(),
						null,
						null
					);
				}
				else
				{
					throw new Exception("syntax error for catalog for " + text);
				}

				break;

			/*--------------------------------------------------------------------------------------------------------*/

			default:

				throw new Exception("invalid QId type for " + text);

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if("*".equals(result.m_catalog)) {
			throw new Exception("`*` not allowed in `catalog` part for " + text);
		}

		if("*".equals(result.m_entity)) {
			throw new Exception("`*` not allowed in `entity` part for " + text);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> this")
	public QId setExclusion(boolean exclusion)
	{
		m_exclusion = exclusion;

		return this;
	}

	/////////
	@Contract(pure = true)
	public boolean getExclusion()
	{
		return m_exclusion;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> this")
	public QId setCatalog(@Nullable String catalog)
	{
		if(catalog != null)
		{
			catalog = Utility.sqlIdToText(catalog);

			if(catalog.isEmpty())
			{
				catalog = null;
			}
		}

		m_catalog = catalog;

		return this;
	}

	@Nullable
	@Contract(pure = true)
	public String getCatalog()
	{
		return m_catalog;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> this")
	public QId setEntity(@Nullable String entity)
	{
		if(entity != null)
		{
			entity = Utility.sqlIdToText(entity);

			if(entity.isEmpty())
			{
				entity = null;
			}
		}

		m_entity = entity;

		return this;
	}

	@Nullable
	@Contract(pure = true)
	public String getEntity()
	{
		return m_entity;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> this")
	public QId setField(@Nullable String field)
	{
		if(field != null)
		{
			field = Utility.sqlIdToText(field);

			if(field.isEmpty())
			{
				field = null;
			}
		}

		m_field = field;

		return this;
	}

	@Nullable
	@Contract(pure = true)
	public String getField()
	{
		return m_field;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public List<QId> getConstraints()
	{
		return m_constraints;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public QId as(int mask)
	{
		return new QId(
			(mask & MASK_CATALOG) != 0 ? m_catalog : null,
			(mask & MASK_ENTITY) != 0 ? m_entity : null,
			(mask & MASK_FIELD) != 0 ? m_field : null,
			m_constraints
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public boolean is(int mask)
	{
		return (((mask & MASK_CATALOG) != 0) == (m_catalog != null))
		       &&
		       (((mask & MASK_ENTITY) != 0) == (m_entity != null))
		       &&
		       (((mask & MASK_FIELD) != 0) == (m_field != null))
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract("null -> false")
	public boolean matches(@Nullable Object object)
	{
		if(!(object instanceof QId))
		{
			return false;
		}

		return (this.m_catalog == null || ((QId) object).m_catalog == null || WILDCARD.equals(this.m_catalog) || WILDCARD.equals(((QId) object).m_catalog) || this.m_catalog.equalsIgnoreCase(((QId) object).m_catalog))
		       &&
		       (this.m_entity == null || ((QId) object).m_entity == null || WILDCARD.equals(this.m_entity) || WILDCARD.equals(((QId) object).m_entity) || this.m_entity.equalsIgnoreCase(((QId) object).m_entity))
		       &&
		       (this.m_field == null || ((QId) object).m_field == null || WILDCARD.equals(this.m_field) || WILDCARD.equals(((QId) object).m_field) || this. m_field.equalsIgnoreCase(((QId) object).m_field))
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Contract(value = "null -> false", pure = true)
	public boolean equals(@Nullable Object object)
	{
		return equals(object, MASK_CATALOG_ENTITY_FIELD, MASK_NONE);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract("null, _ -> false")
	public boolean equals(@Nullable Object object, int mask)
	{
		return equals(object, mask, MASK_NONE);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract("null, _, _ -> false")
	public boolean equals(@Nullable Object object, int mask, int maskForPath)
	{
		if(!(object instanceof QId))
		{
			return false;
		}

		return this.toString(mask, maskForPath).equals(((QId) object).toString(mask, maskForPath));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public int hashCode()
	{
		return toString(MASK_CATALOG_ENTITY_FIELD, MASK_NONE).hashCode();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/////////
	public int hashCode(int mask)
	{
		return toString(mask, MASK_NONE).hashCode();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/////////
	public int hashCode(int mask, int maskForPath)
	{
		return toString(mask, maskForPath).hashCode();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String toString()
	{
		return toStringBuilder(MASK_CATALOG_ENTITY_FIELD, MASK_NONE).toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	/////////
	public String toString(int mask)
	{
		return toStringBuilder(mask, MASK_NONE).toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	/////////
	public String toString(int mask, int maskForPath)
	{
		return toStringBuilder(mask, maskForPath).toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(MASK_CATALOG_ENTITY_FIELD, MASK_NONE);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder(int mask)
	{
		return toStringBuilder(mask, MASK_NONE);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder(int mask, int maskForPath)
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		if(m_exclusion)
		{
			result.append("!");
		}

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		if(maskForPath != MASK_NONE && !m_constraints.isEmpty())
		{
			result.append("{")
			      .append(m_constraints.stream().map(qId -> qId.toString(maskForPath, maskForPath)).collect(Collectors.joining(", ")))
			      .append("}")
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
