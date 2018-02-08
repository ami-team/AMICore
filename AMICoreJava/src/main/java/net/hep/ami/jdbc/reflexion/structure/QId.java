package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;
import java.util.stream.*;

import org.antlr.v4.runtime.*;

import net.hep.ami.utility.parser.*;

public class QId
{
	/*---------------------------------------------------------------------*/

	public static final int FLAG_CATALOG = 0b100;
	public static final int FLAG_ENTITY = 0b010;
	public static final int FLAG_FIELD = 0b001;

	public static final int MASK_CATALOG_ENTITY = 0b110;
	public static final int MASK_ENTITY_FIELD = 0b011;

	public static final int MASK_CATALOG_ENTITY_FIELD = 0b111;

	/*---------------------------------------------------------------------*/

	private boolean m_exclusion = false;

	/*---------------------------------------------------------------------*/

	private String m_catalog = null;
	private String m_entity = null;
	private String m_field = null;

	/*---------------------------------------------------------------------*/

	private final List<QId> m_path = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public static String unquote(String s)
	{
		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		if(s.isEmpty() == false)
		{
			final int l = s.length() - 1;

			if(s.charAt(0) == '`'
			   &&
			   s.charAt(l) == '`'
			 ) {
				s = s.substring(1, l).replace("``", "`");
			}
		}

		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public static String quote(String s)
	{
		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		if(s.isEmpty() == false)
		{
			final int l = s.length() - 1;

			if(s.charAt(0) != '`'
			   ||
			   s.charAt(l) != '`'
			 ) {
				s = '`' + s.replace("`", "``") + '`';
			}
		}

		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public QId()
	{
		/* DO NOTHING */
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId) throws Exception
	{
		this(qId, FLAG_FIELD, FLAG_FIELD);
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId, int type) throws Exception
	{
		this(qId, type, FLAG_FIELD);
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId, int type, int typeForPath) throws Exception
	{
		/*-----------------------------------------------------------------*/

		QIdLexer lexer = new QIdLexer(CharStreams.fromString(qId));

		QIdParser parser = new QIdParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*-----------------------------------------------------------------*/

		visitQId(this, parser.qId(), type, typeForPath);

		/*-----------------------------------------------------------------*/

		if(listener.isSuccess() == false)
		{
			throw new Exception(listener.toString());
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private QId visitQId(QId result, QIdParser.QIdContext context, int type, int typeForPath) throws Exception
	{
		visitBasicQId(result, context.m_basicQId, type);

		for(QIdParser.PathQIdContext pathQIdContext: context.m_pathQIds)
		{
			result.m_path.add(visitQId(new QId().setExclusion(pathQIdContext.m_op != null), pathQIdContext.m_qId, typeForPath, typeForPath));
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private QId visitBasicQId(QId result, QIdParser.BasicQIdContext context, int type) throws Exception
	{
		final int size = context.m_ids.size();

		switch(type)
		{
			/*-------------------------------------------------------------*/

			case FLAG_CATALOG:
				/**/ if(size == 1)
				{
					result.m_catalog = unquote(context.m_ids.get(0).getText());
				}
				else
				{
					throw new Exception("syntax error for catalog");
				}
				break;

			/*-------------------------------------------------------------*/

			case FLAG_ENTITY:
				/**/ if(size == 2)
				{
					result.m_catalog = unquote(context.m_ids.get(0).getText());
					result.m_entity = unquote(context.m_ids.get(1).getText());
				}
				else if(size == 1)
				{
					result.m_entity = unquote(context.m_ids.get(0).getText());
				}
				else
				{
					throw new Exception("syntax error for entity");
				}
				break;

			/*-------------------------------------------------------------*/

			case FLAG_FIELD:
				/**/ if(size == 3)
				{
					result.m_catalog = unquote(context.m_ids.get(0).getText());
					result.m_entity = unquote(context.m_ids.get(1).getText());
					result.m_field = unquote(context.m_ids.get(2).getText());
				}
				else if(size == 2)
				{
					result.m_entity = unquote(context.m_ids.get(0).getText());
					result.m_field = unquote(context.m_ids.get(1).getText());
				}
				else if(size == 1)
				{
					result.m_field = unquote(context.m_ids.get(0).getText());
				}
				else
				{
					throw new Exception("syntax error for field");
				}
				break;

			/*-------------------------------------------------------------*/

			default:
				throw new Exception("invalid QId type");

			/*-------------------------------------------------------------*/
		}

		if("*".equals(result.m_catalog)) {
			throw new Exception("`*` not allowed in `catalog` part");
		}

		if("*".equals(result.m_entity)) {
			throw new Exception("`*` not allowed in `entity` part");
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field)
	{
		this(catalog, entity, field, null);
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String entity, @Nullable String field, @Nullable List<QId> path)
	{
		setCatalog(catalog);
		setEntity(entity);
		setField(field);

		if(path != null)
		{
			m_path.addAll(path);
		}
	}

	/*---------------------------------------------------------------------*/

	public QId setExclusion(boolean exclusion)
	{
		m_exclusion = exclusion;

		return this;
	}

	/*---------------------------------------------------------------------*/

	public boolean getExclusion()
	{
		return m_exclusion;
	}

	/*---------------------------------------------------------------------*/

	public QId setCatalog(@Nullable String catalog)
	{
		m_catalog = catalog != null ? unquote(catalog) : null;

		return this;
	}

	/*---------------------------------------------------------------------*/

	public String getCatalog()
	{
		return m_catalog;
	}

	/*---------------------------------------------------------------------*/

	public QId setEntity(@Nullable String entity)
	{
		m_entity = entity != null ? unquote(entity) : null;

		return this;
	}

	/*---------------------------------------------------------------------*/

	public String getEntity()
	{
		return m_entity;
	}

	/*---------------------------------------------------------------------*/

	public QId setField(@Nullable String field)
	{
		m_field = field != null ? unquote(field) : null;

		return this;
	}

	/*---------------------------------------------------------------------*/

	public String getField()
	{
		return m_field;
	}

	/*---------------------------------------------------------------------*/

	public List<QId> getPath()
	{
		return m_path;
	}

	/*---------------------------------------------------------------------*/

	public boolean equals(Object anObject)
	{
		return equals(anObject, MASK_CATALOG_ENTITY_FIELD, MASK_CATALOG_ENTITY_FIELD);
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

			return this == that || this.toString(mask, maskForPath).equals(that.toString(mask, maskForPath));
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public boolean matches(QId qId)
	{
		return (this.m_catalog == null || qId.m_catalog == null || "#".equals(this.m_catalog) || "#".equals(qId.m_catalog) || this.m_catalog.equalsIgnoreCase(qId.m_catalog))
		       &&
		       (this.m_entity == null || qId.m_entity == null || "#".equals(this.m_entity) || "#".equals(qId.m_entity) || this.m_entity.equalsIgnoreCase(qId.m_entity))
		       &&
		       (this.m_field == null || qId.m_field == null || "#".equals(this.m_field) || "#".equals(qId.m_field) || this.m_field.equalsIgnoreCase(qId.m_field))
		;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return toStringBuilder(MASK_CATALOG_ENTITY_FIELD, MASK_CATALOG_ENTITY_FIELD).toString();
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
		return toStringBuilder(MASK_CATALOG_ENTITY_FIELD, MASK_CATALOG_ENTITY_FIELD);
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

		if((mask & FLAG_CATALOG) != 0 && m_catalog != null) {
			parts.add(quote(m_catalog));
		}

		if((mask & FLAG_ENTITY) != 0 && m_entity != null) {
			parts.add(quote(m_entity));
		}

		if((mask & FLAG_FIELD) != 0 && m_field != null) {
			parts.add(quote(m_field));
		}

		/*-----------------------------------------------------------------*/

		if(m_exclusion)
		{
			result.append("!");
		}

		result.append(String.join(".", parts));

		/*-----------------------------------------------------------------*/

		if(m_path.isEmpty() == false)
		{
			result.append("{")
			      .append(m_path.stream().map(qId -> qId.toString(maskForPath, maskForPath)).collect(Collectors.joining(",")))
			      .append("}")
			;
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
