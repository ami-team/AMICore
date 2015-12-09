package net.hep.ami.command;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;

public abstract class CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	protected String m_AMIUser;
	protected String m_AMIPass;
	protected String m_clientDN;
	protected String m_issuerDN;
	protected String m_isSecure;

	/*---------------------------------------------------------------------*/

	protected static final String m_guestUser = ConfigSingleton.getProperty("guest_user");
	protected static final String m_guestPass = ConfigSingleton.getProperty("guest_pass");

	/*---------------------------------------------------------------------*/

	protected Map<String, String> m_arguments;

	/*---------------------------------------------------------------------*/

	protected long m_transactionId;

	 private  boolean m_transactionIdBooker;

	/*---------------------------------------------------------------------*/

	public CommandAbstractClass(Map<String, String> arguments, long transactionId)
	{
		/*-----------------------------------------------------------------*/
		/* ARGUMENT PARAMETERS                                             */
		/*-----------------------------------------------------------------*/

		if(arguments.containsKey("AMIUser"))
		{
			m_AMIUser = arguments.get("AMIUser");
			arguments.remove("AMIUser");
		}
		else
		{
			m_AMIUser = "";
		}

		/*-----------------------------------------------------------------*/

		if(arguments.containsKey("AMIPass"))
		{
			m_AMIPass = arguments.get("AMIPass");
			arguments.remove("AMIPass");
		}
		else
		{
			m_AMIPass = "";
		}

		if(arguments.containsKey("clientDN"))
		{
			m_clientDN = arguments.get("clientDN");
			arguments.remove("clientDN");
		}
		else
		{
			m_clientDN = "";
		}

		/*-----------------------------------------------------------------*/

		if(arguments.containsKey("issuerDN"))
		{
			m_issuerDN = arguments.get("issuerDN");
			arguments.remove("issuerDN");
		}
		else
		{
			m_issuerDN = "";
		}

		/*-----------------------------------------------------------------*/

		if(arguments.containsKey("isSecure"))
		{
			m_isSecure = arguments.get("isSecure");
			arguments.remove("isSecure");
		}
		else
		{
			m_isSecure = "";
		}

		/*-----------------------------------------------------------------*/
		/* CONSTRUCTOR PARAMETERS                                          */
		/*-----------------------------------------------------------------*/

		m_arguments = arguments;

		/*-----------------------------------------------------------------*/

		if(transactionId < 0)
		{
			m_transactionId = TransactionPoolSingleton.bookNewTransactionId();
			m_transactionIdBooker = true;
		}
		else
		{
			m_transactionId = (((((((((((((((((transactionId)))))))))))))))));
			m_transactionIdBooker = false;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	protected TransactionalQuerier getQuerier(String catalog) throws Exception
	{
		return new TransactionalQuerier(catalog, m_transactionId);
	}

	/*---------------------------------------------------------------------*/

	protected TransactionalQuerier getQuerier(String jdbcUrl, String user, String pass) throws Exception
	{
		return new TransactionalQuerier(jdbcUrl, user, pass, m_transactionId);
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

		try
		{
			result = main();
		}
		finally
		{
			if(m_transactionIdBooker)
			{
				if(result != null)
				{
					TransactionPoolSingleton.commitAndRelease(m_transactionId);
				}
				else
				{
					TransactionPoolSingleton.rollbackAndRelease(m_transactionId);
				}
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public abstract StringBuilder main() throws Exception;

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
