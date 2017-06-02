package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;

public abstract class AbstractCommand
{
	/*---------------------------------------------------------------------*/

	protected String m_AMIUser;
	protected String m_AMIPass;
	protected String m_clientDN;
	protected String m_issuerDN;
	protected String m_isSecure;
	protected String m_AMIAgent;

	/*---------------------------------------------------------------------*/

	protected static final String m_guestUser = ConfigSingleton.getProperty("guest_user");
	protected static final String m_guestPass = ConfigSingleton.getProperty("guest_pass");

	/*---------------------------------------------------------------------*/

	private Map<String, String> m_arguments;

	/*---------------------------------------------------------------------*/

	protected long m_transactionId;

	 private  boolean m_transactionBooker;

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

		m_isSecure = arguments.containsKey("isSecure") ? arguments.remove("isSecure") : "";

		m_AMIAgent = arguments.containsKey("AMIAgent") ? arguments.remove("AMIAgent") : "";

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
			m_transactionId = (((((((((((((((((transactionId)))))))))))))))));
			m_transactionBooker = false;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	protected Querier getQuerier(String catalog) throws Exception
	{
		return new TransactionalQuerier(catalog, m_transactionId);
	}

	/*---------------------------------------------------------------------*/

	protected Querier getQuerier(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		return new TransactionalQuerier(externalCatalog, internalCatalog, jdbcUrl, user, pass, m_transactionId);
	}

	/*---------------------------------------------------------------------*/

	protected String executeCommand(String command, Map<String, String> arguments, boolean checkRoles) throws Exception
	{
		return CommandSingleton.executeCommand(command, arguments, checkRoles, m_transactionId);
	}

	/*---------------------------------------------------------------------*/

	public final StringBuilder execute() throws Exception
	{
		StringBuilder result = null;

		Exception e = null;

		/*-----------------------------------------------------------------*/

		try
		{
			result = main(m_arguments);
		}
		catch(Exception f)
		{
			e = f;
		}

		/*-----------------------------------------------------------------*/

		if(m_transactionBooker)
		{
			if(e == null)
			{
				TransactionPoolSingleton.commitAndRelease(m_transactionId, e);
			}
			else
			{
				try
				{
					TransactionPoolSingleton.rollbackAndRelease(m_transactionId, e);
				}
				catch(Exception f)
				{
					throw new Exception(e.getMessage() + ", " + f.getMessage(), f);
				}
			}
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
