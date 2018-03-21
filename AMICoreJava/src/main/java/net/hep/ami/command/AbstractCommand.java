package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;

public abstract class AbstractCommand
{
	/*---------------------------------------------------------------------*/

	protected final String m_AMIUser;
	protected final String m_AMIPass;

	protected final String m_clientDN;
	protected final String m_issuerDN;

	protected final String m_notBefore;
	protected final String m_notAfter;

	protected final boolean m_isSecure;
	protected final boolean m_isCached;

	protected final String m_userAgent;

	/*---------------------------------------------------------------------*/

	private final Map<String, String> m_arguments;

	/*---------------------------------------------------------------------*/

	protected final long m_transactionId;

	 private  final boolean m_transactionBooker;

	/*---------------------------------------------------------------------*/

	public AbstractCommand(Map<String, String> arguments, long transactionId)
	{
		/*-----------------------------------------------------------------*/
		/* ARGUMENT PARAMETERS                                             */
		/*-----------------------------------------------------------------*/

		m_AMIUser = arguments.containsKey("AMIUser") ? arguments.remove("AMIUser") : "";
		m_AMIPass = arguments.containsKey("AMIPass") ? arguments.remove("AMIPass") : "";

		m_clientDN = arguments.containsKey("clientDN") ? arguments.remove("clientDN") : "";
		m_issuerDN = arguments.containsKey("issuerDN") ? arguments.remove("issuerDN") : "";

		m_notBefore = arguments.containsKey("notBefore") ? arguments.remove("notBefore") : "";
		m_notAfter = arguments.containsKey("notAfter") ? arguments.remove("notAfter") : "";

		m_isSecure = arguments.containsKey("isSecure") ? "false".equalsIgnoreCase(arguments.remove("isSecure")) == false : false;
		m_isCached = arguments.containsKey( "cached" ) ? "false".equalsIgnoreCase(arguments.remove( "cached" )) == false : false;

		m_userAgent = arguments.containsKey("userAgent") ? arguments.remove("userAgent") : "N/A";

		/*-----------------------------------------------------------------*/
		/* CONSTRUCTOR PARAMETERS                                          */
		/*-----------------------------------------------------------------*/

		m_arguments = arguments;

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	protected Querier getQuerier(String catalog) throws Exception
	{
		TransactionalQuerier result = new TransactionalQuerier(catalog, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	protected Querier getQuerier(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		TransactionalQuerier result = new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, m_transactionId);

		if(m_isCached)
		{
			result.setReadOnly(true);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	protected String executeCommand(String command, Map<String, String> arguments, boolean checkRoles) throws Exception
	{
		return CommandSingleton.executeCommand(command, arguments, checkRoles, m_transactionId);
	}

	/*---------------------------------------------------------------------*/

	public final StringBuilder execute() throws Exception
	{
		StringBuilder result;

		/*-----------------------------------------------------------------*/

		String key = new StringBuilder().append(getClass().getSimpleName().toString())
		                                .append(m_arguments.toString())
		                                .toString()
		;

		/*-----------------------------------------------------------------*/

		if(m_isCached)
		{
			Object value = CacheSingleton.get(key);

			result = (StringBuilder) ((value instanceof StringBuilder == false) ? CacheSingleton.put(key, _execute()) : value);
		}
		else
		{
			result = _execute();
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	private StringBuilder _execute() throws Exception
	{
		StringBuilder result = null;

		Exception e1 = null;

		/*-----------------------------------------------------------------*/

		try
		{
			result = main(m_arguments);
		}
		catch(Exception e2)
		{
			e1 = e2;
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public abstract StringBuilder main(Map<String, String> arguments) throws Exception;

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "";
	}

	/*---------------------------------------------------------------------*/
}
