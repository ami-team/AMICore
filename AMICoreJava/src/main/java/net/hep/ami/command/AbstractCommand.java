package net.hep.ami.command;

import java.security.*;
import java.math.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;

import org.jetbrains.annotations.*;

public abstract class AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected final String m_AMIUser;
	protected final String m_AMIPass;

	protected final String m_clientDN;
	protected final String m_issuerDN;

	protected final String m_notBefore;
	protected final String m_notAfter;

	protected final boolean m_isSecure;
	protected final boolean m_isCached;

	protected final String m_userAgent;
	protected final String m_userSession;
	protected final String m_userTimeZone;

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

		m_AMIUser = arguments.containsKey("AMIUser") ? arguments.remove("AMIUser") : ConfigSingleton.getProperty("guest_user");
		m_AMIPass = arguments.containsKey("AMIPass") ? arguments.remove("AMIPass") : ConfigSingleton.getProperty("guest_pass");

		m_clientDN = arguments.containsKey("clientDN") ? arguments.remove("clientDN") : "";
		m_issuerDN = arguments.containsKey("issuerDN") ? arguments.remove("issuerDN") : "";

		m_notBefore = arguments.containsKey("notBefore") ? arguments.remove("notBefore") : "";
		m_notAfter  = arguments.containsKey("notAfter" ) ? arguments.remove("notAfter" ) : "";

		m_isSecure = arguments.containsKey("isSecure") && !"false".equalsIgnoreCase(arguments.remove("isSecure"));
		m_isCached = arguments.containsKey("cached") && !"false".equalsIgnoreCase(arguments.remove("cached"));

		m_userAgent = arguments.containsKey("userAgent") ? arguments.remove("userAgent") : "N/A";
		m_userSession = arguments.containsKey("userSession") ? arguments.remove("userSession") : "";
		m_userTimeZone = arguments.containsKey("userTimeZone") ? arguments.remove("userTimeZone") : ConfigSingleton.getProperty("time_zone");

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
	protected Querier getAdminQuerier(@NotNull String catalog) throws Exception
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
	protected Querier getAdminQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
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
	protected Querier getQuerier(@NotNull String catalog) throws Exception
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
	protected Querier getQuerier(@NotNull String catalog, int flags) throws Exception
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
	protected Querier getQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
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
	protected Querier getQuerier(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, int flags) throws Exception
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
	protected String executeCommand(@NotNull String command, boolean checkRoles) throws Exception
	{
		return CommandSingleton.executeCommand(command, checkRoles, m_transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	protected String executeCommand(@NotNull String command, @NotNull Map<String, String> arguments, boolean checkRoles) throws Exception
	{
		return CommandSingleton.executeCommand(command, arguments, checkRoles, m_transactionId);
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

			Object object = CacheSingleton.get(key);

			if(object instanceof StringBuilder)
			{
				result = new StringBuilder().append("<cacheOp><![CDATA[GET," + key + "]]></cacheOp>")
				                            .append(/*----------*/ object /*----------*/)
				;
			}
			else
			{
				result = new StringBuilder().append("<cacheOp><![CDATA[SET," + key + "]]></cacheOp>")
				                            .append(CacheSingleton.set(key, _execute()))
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

	/*---------------------------------------------------------------------*/

	@NotNull
	public abstract StringBuilder main(@NotNull Map<String, String> arguments) throws Exception;

	/*---------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "";
	}

	/*---------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "";
	}

	/*---------------------------------------------------------------------*/
}
