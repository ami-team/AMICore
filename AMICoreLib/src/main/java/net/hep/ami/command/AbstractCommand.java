package net.hep.ami.command;

import java.math.*;
import java.util.*;
import java.security.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;

import org.jetbrains.annotations.*;

public abstract class AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final String GUEST_USER = ConfigSingleton.getProperty("guest_user");

	private final String TIME_ZONE = ConfigSingleton.getProperty("time_zone");

	/*----------------------------------------------------------------------------------------------------------------*/

	public final String m_AMIUser;
	public final String m_clientDN;
	public final String m_issuerDN;
	public final String m_notBefore;
	public final String m_notAfter;

	public final boolean m_isSecure;
	public final boolean m_isCached;

	public final String m_userAgent;
	public final String m_userSession;
	public final String m_userTimeZone;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final Set<String> m_userRoles;

	protected final Map<String, String> m_arguments;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final long m_transactionId;

	 private  final boolean m_transactionBooker;

	/*----------------------------------------------------------------------------------------------------------------*/

	public AbstractCommand(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* ARGUMENT PARAMETERS                                                                                        */
		/*------------------------------------------------------------------------------------------------------------*/

		m_AMIUser = arguments.containsKey("AMIUser") ? arguments.remove("AMIUser") : GUEST_USER;
		m_clientDN = arguments.containsKey("clientDN") ? arguments.remove("clientDN") : "";
		m_issuerDN = arguments.containsKey("issuerDN") ? arguments.remove("issuerDN") : "";
		m_notBefore = arguments.containsKey("notBefore") ? arguments.remove("notBefore") : "";
		m_notAfter  = arguments.containsKey("notAfter" ) ? arguments.remove("notAfter" ) : "";

		m_isSecure = arguments.containsKey("isSecure") && !"false".equalsIgnoreCase(arguments.remove("isSecure"));
		m_isCached = arguments.containsKey("cached") && !"false".equalsIgnoreCase(arguments.remove("cached"));

		m_userAgent = arguments.containsKey("userAgent") ? arguments.remove("userAgent") : "N/A";
		m_userSession = arguments.containsKey("userSession") ? arguments.remove("userSession") : "";
		m_userTimeZone = arguments.containsKey("userTimeZone") ? arguments.remove("userTimeZone") : TIME_ZONE;

		/*------------------------------------------------------------------------------------------------------------*/
		/* CONSTRUCTOR PARAMETERS                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		m_userRoles = userRoles;

		m_arguments = arguments;

		/*------------------------------------------------------------------------------------------------------------*/

		if(transactionId < 0)
		{
			m_transactionId = TransactionPoolSingleton.bookNewTransactionId();
			m_transactionBooker = true;
		}
		else
		{
			m_transactionId = /*------------*/ transactionId /*------------*/;
			m_transactionBooker = false;
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public Querier getAdminQuerier(@NotNull String catalog) throws Exception
	{
		TransactionalQuerier result = new TransactionalQuerier(catalog, m_AMIUser, m_userTimeZone, Querier.FLAG_ADMIN, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public Querier getAdminQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
	{
		TransactionalQuerier result = new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, m_AMIUser, m_userTimeZone, Querier.FLAG_ADMIN, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public Querier getQuerier(@NotNull String catalog) throws Exception
	{
		TransactionalQuerier result = new TransactionalQuerier(catalog, m_AMIUser, m_userTimeZone, m_userRoles.contains("AMI_ADMIN") ? Querier.FLAG_ADMIN : 0x00, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public Querier getQuerier(@NotNull String catalog, int flags) throws Exception
	{
		if(m_userRoles.contains("AMI_ADMIN"))
		{
			flags |= Querier.FLAG_ADMIN;
		}
		else
		{
			flags &= ~Querier.FLAG_ADMIN;
		}

		TransactionalQuerier result = new TransactionalQuerier(catalog, m_AMIUser, m_userTimeZone, flags, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public Querier getQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
	{
		TransactionalQuerier result = new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, m_AMIUser, m_userTimeZone, m_userRoles.contains("AMI_ADMIN") ? Querier.FLAG_ADMIN : 0x00, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public Querier getQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, int flags) throws Exception
	{
		if(m_userRoles.contains("AMI_ADMIN"))
		{
			flags |= Querier.FLAG_ADMIN;
		}
		else
		{
			flags &= ~Querier.FLAG_ADMIN;
		}

		TransactionalQuerier result = new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, m_AMIUser, m_userTimeZone, flags, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder executeCommand(@NotNull String command) throws Exception
	{
		return CommandSingleton.chainCommand(command, m_userRoles, m_arguments, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder executeCommand(@NotNull String command, @NotNull Map<String, String> arguments) throws Exception
	{
		return CommandSingleton.chainCommand(command, m_userRoles, arguments, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public final StringBuilder execute() throws Exception
	{
		StringBuilder result;

		/*------------------------------------------------------------------------------------------------------------*/

		if(m_isCached)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.update(getClass().getSimpleName().getBytes());

			messageDigest.update(m_arguments.toString().getBytes());

			/*--------------------------------------------------------------------------------------------------------*/

			String key = String.format("%032x", new BigInteger(1, messageDigest.digest()));

			/*--------------------------------------------------------------------------------------------------------*/

			String object = CacheSingleton.get(key);

			if(object != null)
			{
				result = new StringBuilder().append("<cacheOp><![CDATA[GET,").append(key).append("]]></cacheOp>")
				                            .append(/*--------------------*/ object /*--------------------*/)
				;
			}
			else
			{
				result = new StringBuilder().append("<cacheOp><![CDATA[SET,").append(key).append("]]></cacheOp>")
				                            .append(CacheSingleton.set(key, _execute().toString()))
				;
			}
		}
		else
		{
			result = new StringBuilder().append("<cacheOp><![CDATA[NONE]]></cacheOp>")
			                            .append(_execute())
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder _execute() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result;

		Exception e1;

		try
		{
			result = main(m_arguments);

			e1 = null;
		}
		catch(Exception e2)
		{
			result = new StringBuilder();

			e1 = e2;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(e1 == null)
		{
			if(m_transactionBooker)
			{
				TransactionPoolSingleton.commitAndRelease(m_transactionId);
			}
		}
		else
		{
			if(m_transactionBooker)
			{
				try
				{
					TransactionPoolSingleton.rollbackAndRelease(m_transactionId);
				}
				catch(Exception e2)
				{
					e2.initCause(e1);

					e1 = new Exception(e1.getMessage() + ", " + e2.getMessage(), e2);
				}
			}

			throw e1;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public abstract StringBuilder main(@NotNull Map<String, String> arguments) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
