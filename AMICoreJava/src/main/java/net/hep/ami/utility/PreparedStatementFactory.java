package net.hep.ami.utility;

import java.sql.*;
import java.math.*;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hep.ami.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class PreparedStatementFactory
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final static DateTime m_amiDateTime = new DateTime();

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private PreparedStatementFactory() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static PreparedStatement createStatement(@NotNull Map<String, PreparedStatement> statementMap, @NotNull Connection connection, @NotNull String sql, boolean returnGeneratedKeys, String[] columnNames, boolean injectArgs, @Nullable Object[] args) throws Exception
	{
		PreparedStatement result;

		if(injectArgs && args != null)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			Tuple tuple = prepare(sql, connection, args.length == 1 && args[0] != null && args[0].getClass().isArray() ? (Object[]) args[0] : args);

			/*--------------------------------------------------------------------------------------------------------*/

			result = statementMap.get(tuple.getSql());

			if(result == null || result.isClosed())
			{
				statementMap.put(tuple.getSql(), result = !returnGeneratedKeys ? (
						connection.prepareStatement(tuple.getSql())
					) : (
						(columnNames == null) ? (
							connection.prepareStatement(tuple.getSql(), Statement.RETURN_GENERATED_KEYS)
						) : (
							connection.prepareStatement(tuple.getSql(), /*-----*/ columnNames /*-----*/)
						)
					)
				);
			}

			/*--------------------------------------------------------------------------------------------------------*/

			inject(result, tuple);

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/

			result = statementMap.get(sql);

			if(result == null || result.isClosed())
			{
				statementMap.put(sql, result = !returnGeneratedKeys ? (
						connection.prepareStatement(sql)
					) : (
						(columnNames == null) ? (
							connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
						) : (
							connection.prepareStatement(sql, /*-----*/ columnNames /*-----*/)
						)
					)
				);
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	private static final class Tuple
	{
		@NotNull private final String sql;
		@NotNull private final List<String> typeList;
		@NotNull private final List<Object> valueList;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_, _, _ -> new")
	private static Tuple prepare(@NotNull String sql, Connection connexion, @Nullable Object[] args) throws Exception
	{
		List<String> typeList = new ArrayList<>();
		List<Object> valueList = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		if(args == null)
		{
			return new Tuple(sql, typeList, valueList);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		int i;
		int idx;

		int cnt = 0;
		int mode = 0;

		String value = null;

		final int l = args.length;

		for(String token: Tokenizer.tokenize(sql))
		{
			/**/ if(token.startsWith("?#<"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				idx = token.indexOf('>');

				/*----------------------------------------------------------------------------------------------------*/

				if(idx > 3)
				{
					i = Integer.parseInt(token.substring(idx + 1));

					if(i >= l)
					{
						throw new Exception("not enough arguments");
					}
				}
				else
				{
					throw new Exception("invalid parameter");
				}

				/*----------------------------------------------------------------------------------------------------*/

				if(!Empty.is(args[i], Empty.STRING_AMI_NULL))
				{
					typeList.add("parse::" + token.substring(3, idx).toLowerCase());

					valueList.add(SecuritySingleton.encrypt(args[i].toString()));

					stringBuilder.append("?");
				}
				else
				{
					stringBuilder.append("NULL");
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if(token.startsWith("?#"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				i = Integer.parseInt(token.substring(2));

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				if(!Empty.is(args[i], Empty.STRING_AMI_NULL))
				{
					typeList.add("java.lang.String");

					valueList.add(SecuritySingleton.encrypt(args[i].toString()));

					stringBuilder.append("?");
				}
				else
				{
					stringBuilder.append("NULL");
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if(token.startsWith("?<"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				idx = token.indexOf('>');

				/*----------------------------------------------------------------------------------------------------*/

				if(idx > 2)
				{
					i = Integer.parseInt(token.substring(idx + 1));

					if(i >= l)
					{
						throw new Exception("not enough arguments");
					}
				}
				else
				{
					throw new Exception("invalid parameter");
				}

				/*----------------------------------------------------------------------------------------------------*/

				if(!Empty.is(args[i], Empty.STRING_AMI_NULL))
				{
					typeList.add("parse::" + token.substring(2, idx).toLowerCase());

					valueList.add(/*--------------*/ args[i] /*--------------*/);

					stringBuilder.append("?");
				}
				else
				{
					stringBuilder.append("NULL");
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if(token.startsWith("?"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				i = Integer.parseInt(token.substring(1));

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				if(!Empty.is(args[i], Empty.STRING_AMI_NULL))
				{
					typeList.add(args[i].getClass().getName());

					valueList.add(/*--------------*/ args[i] /*--------------*/);

					stringBuilder.append("?");
				}
				else
				{
					stringBuilder.append("NULL");
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("AMI_TIMESTAMP".equalsIgnoreCase(token))
			{
				mode = 1;
			}
			else if("AMI_DATE".equalsIgnoreCase(token))
			{
				mode = 2;
			}
			else if("AMI_TIME".equalsIgnoreCase(token))
			{
				mode = 3;
			}
			else
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(mode != 0)
				{
					/**/ if("(".equals(token))
					{
						cnt++;
					}
					else if(")".equals(token))
					{
						cnt--;

						if(cnt == 0)
						{
							switch(mode)
							{
								case 1:
									if(!Empty.is(value, Empty.STRING_AMI_NULL))
									{
										if(connexion.getMetaData().getDriverName().contains("SQLite"))
										{
											typeList.add("java.lang.String");
											String tmpValue = m_amiDateTime.parseTimestamp(value).toString();
											if(tmpValue.endsWith(".0"))
											{
												tmpValue = tmpValue.substring(0,tmpValue.length()-2);
											}
											valueList.add(tmpValue);
										}
										else
										{
											typeList.add("java.sql.Timestamp");
											valueList.add(m_amiDateTime.parseTimestamp(value));

										}

										stringBuilder.append("?");
									}
									else
									{
										stringBuilder.append("NULL");
									}
									break;

								case 2:
									if(!Empty.is(value, Empty.STRING_AMI_NULL))
									{
										typeList.add("java.sql.Date");
										valueList.add(m_amiDateTime.parseDate(value));

										stringBuilder.append("?");
									}
									else
									{
										stringBuilder.append("NULL");
									}
									break;

								case 3:
									if(!Empty.is(value, Empty.STRING_AMI_NULL))
									{
										typeList.add("java.sql.Time");
										valueList.add(m_amiDateTime.parseTime(value));

										stringBuilder.append("?");
									}
									else
									{
										stringBuilder.append("NULL");
									}
									break;
							}


							mode = 0;
						}
					}
					else
					{
						final int m = token.length();

						if(m >= 2
						   &&
						   token.charAt(0 + 0) == '\''
						   &&
						   token.charAt(m - 1) == '\''
						 ) {
							value = token.substring(0 + 1, m - 1).trim();
						}
					}
				}
				else
				{
					stringBuilder.append(token);
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new Tuple(stringBuilder.toString(), typeList, valueList);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void inject(@NotNull PreparedStatement statement, @NotNull Tuple tuple) throws Exception
	{
		for(int i = 0; i < tuple.getTypeList().size(); i++)
		{
			String type = tuple.getTypeList().get(i);
			Object value = tuple.getValueList().get(i);

			if(type.startsWith("parse::"))
			{
				/*----------------------------------------------------------------------------------------------------*/

				switch(Integer.parseInt(type.substring(7)))
				{
					case Types.BIT:
						statement.setBoolean(i + 1, Bool.parseBool(value.toString()));
						break;

					case Types.TINYINT:
					case Types.SMALLINT:
					case Types.INTEGER:
						statement.setInt(i + 1, Integer.parseInt(value.toString()));
						break;

					case Types.FLOAT:
						statement.setFloat(i + 1, Float.parseFloat(value.toString()));
						break;

					case Types.DOUBLE:
						statement.setDouble(i + 1, Double.parseDouble(value.toString()));
						break;

					case Types.NUMERIC:
					case Types.DECIMAL:
						statement.setBigDecimal(i + 1, new BigDecimal(value.toString()));
						break;

					case Types.TIMESTAMP:
						statement.setTimestamp(i + 1, m_amiDateTime.parseTimestamp(value.toString()));
						break;

					case Types.DATE:
						statement.setDate(i + 1, m_amiDateTime.parseDate(value.toString()));
						break;

					case Types.TIME:
						statement.setTime(i + 1, m_amiDateTime.parseTime(value.toString()));
						break;

					default:
						statement.setString(i + 1, value.toString());
						break;
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				/*----------------------------------------------------------------------------------------------------*/

				switch(type)
				{
					case "java.lang.Boolean":
						statement.setBoolean(i + 1, (java.lang.Boolean) value);
						break;

					case "java.lang.Integer":
						statement.setInt(i + 1, (java.lang.Integer) value);
						break;

					case "java.lang.Long":
						statement.setLong(i + 1, (java.lang.Long) value);
						break;

					case "java.lang.Float":
						statement.setFloat(i + 1, (java.lang.Float) value);
						break;

					case "java.lang.Double":
						statement.setDouble(i + 1, (java.lang.Double) value);
						break;

					case "java.math.BigDecimal":
						statement.setBigDecimal(i + 1, (java.math.BigDecimal) value);
						break;

					case "java.sql.Timestamp":
						statement.setTimestamp(i + 1, (java.sql.Timestamp) value) ;
						break;

					case "java.sql.Date":
						statement.setDate(i + 1, (java.sql.Date) value);
						break;

					case "java.sql.Time":
						statement.setTime(i + 1, (java.sql.Time) value);
						break;

					default:
						statement.setString(i + 1, value.toString());
						break;
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
