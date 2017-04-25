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

	public CommandAbstractClass(Map<String, String> arguments, long transactionId)
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

	protected TransactionalQuerier getQuerier(String catalog) throws Exception
	{
		return new TransactionalQuerier(catalog, m_transactionId);
	}

	/*---------------------------------------------------------------------*/

	protected TransactionalQuerier getQuerier(@Nullable String catalog, String jdbcUrl, String user, String pass) throws Exception
	{
		return new TransactionalQuerier(catalog, jdbcUrl, user, pass, m_transactionId);
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

		boolean success = false;

		try
		{
			result = main(m_arguments);

			success = true;
		}
		finally
		{
			if(m_transactionBooker)
			{
				if(success)
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
