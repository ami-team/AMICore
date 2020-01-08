package net.hep.ami.utility;

import java.sql.*;
import java.math.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.query.sql.*;

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

			Tuple2<String, List<Object>> tuple = prepare(sql, args.length == 1 && args[0] != null && args[0].getClass().isArray() ? (Object[]) args[0] : args);

			/*--------------------------------------------------------------------------------------------------------*/

			result = statementMap.get(tuple.x);

			if(result == null || result.isClosed())
			{
				statementMap.put(tuple.x, result = !returnGeneratedKeys ? (
						connection.prepareStatement(tuple.x)
					) : (
						(columnNames == null) ? (
							connection.prepareStatement(tuple.x, Statement.RETURN_GENERATED_KEYS)
						) : (
							connection.prepareStatement(tuple.x, /*-----*/ columnNames /*-----*/)
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

	@NotNull
	@Contract("_, _ -> new")
	private static Tuple2<String, List<Object>> prepare(@NotNull String sql, @Nullable Object[] args) throws Exception
	{
		List<Object> list = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		if(args == null)
		{
			return new Tuple2<>(sql, list);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		int i = 0;
		int cnt = 0;
		int mode = 0;

		String value = null;

		final int l = args.length;

		for(String token: Tokenizer.tokenize(sql))
		{
			/**/ if("?#".equals(token))
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}
				/*----------------------------------------------------------------------------------------------------*/

				list.add(args[i] != null ? SecuritySingleton.encrypt(args[i].toString()) : null);

				stringBuilder.append("?");

				i++;

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("?".equals(token))
			{
				/*----------------------------------------------------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*----------------------------------------------------------------------------------------------------*/

				list.add(/*---------------------------*/ args[i] /*---------------------------*/);

				stringBuilder.append("?");

				i++;

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
									list.add(value != null ? m_amiDateTime.parseTimestamp(value) : null);
									break;

								case 2:
									list.add(value != null ? m_amiDateTime.parseDate(value) : null);
									break;

								case 3:
									list.add(value != null ? m_amiDateTime.parseTime(value) : null);
									break;
							}

							stringBuilder.append("?");

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

		return new Tuple2<>(stringBuilder.toString(), list);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void inject(@NotNull PreparedStatement statement, @NotNull Tuple2<String, List<Object>> tuple) throws Exception
	{
		for(int i = 0; i < tuple.y.size(); i++)
		{
			Object value = tuple.y.get(i);

			switch(value.getClass().getName())
			{
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
					statement.setTimestamp(i + 1, (java.sql.Timestamp) value);
					break;

				case "java.sql.Date":
					statement.setDate(i + 1, (java.sql.Date) value);
					break;

				case "java.sql.Time":
					statement.setTime(i + 1, (java.sql.Time) value);
					break;

				default:

					try
					{
						switch(statement.getParameterMetaData().getParameterClassName(i + 1))
						{
							case "java.lang.Integer":
								statement.setInt(i + 1, Integer.parseInt(value.toString()));
								break;

							case "java.lang.Long":
								statement.setLong(i + 1, Long.parseLong(value.toString()));
								break;

							case "java.lang.Float":
								statement.setFloat(i + 1, Float.parseFloat(value.toString()));
								break;

							case "java.lang.Double":
								statement.setDouble(i + 1, Double.parseDouble(value.toString()));
								break;

							case "java.math.BigDecimal":
								statement.setBigDecimal(i + 1, new BigDecimal(value.toString()));
								break;

							case "java.sql.Timestamp":
								statement.setTimestamp(i + 1, m_amiDateTime.parseTimestamp(value.toString()));
								break;

							case "java.sql.Date":
								statement.setDate(i + 1, m_amiDateTime.parseDate(value.toString()));
								break;

							case "java.sql.Time":
								statement.setTime(i + 1, m_amiDateTime.parseTime(value.toString()));
								break;

							default:
								statement.setString(i + 1, value.toString());
								break;
						}
					}
					catch(SQLException e)
					{
						statement.setString(i + 1, value.toString());
					}

					break;
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
