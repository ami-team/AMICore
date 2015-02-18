package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;

public abstract class CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	protected String m_AMIUser = "";
	protected String m_AMIPass = "";
	protected String m_clientDN = "";
	protected String m_issuerDN = "";
	protected String m_isSecure = "";

	/*---------------------------------------------------------------------*/

	protected static final String m_guestUser = ConfigSingleton.getProperty("guest_user");
	protected static final String m_guestPass = ConfigSingleton.getProperty("guest_pass");
	protected static final String  m_useVOMS  = ConfigSingleton.getProperty( "use_voms" );

	/*---------------------------------------------------------------------*/

	protected HashMap<String, String> m_arguments = null;

	protected int m_transactionID = -1;

	/*---------------------------------------------------------------------*/

	private BasicLoader m_routerLoader = null;

	/*---------------------------------------------------------------------*/

	public CommandAbstractClass(HashMap<String, String> arguments, int transactionID) {
		/*-----------------------------------------------------------------*/
		/* ARGUMENT PARAMETERS                                             */
		/*-----------------------------------------------------------------*/

		if(arguments.containsKey("AMIUser")) {
			m_AMIUser = arguments.get("AMIUser");
			arguments.remove("AMIUser");
		}

		if(arguments.containsKey("AMIPass")) {
			m_AMIPass = arguments.get("AMIPass");
			arguments.remove("AMIPass");
		}

		if(arguments.containsKey("clientDN")) {
			m_clientDN = arguments.get("clientDN");
			arguments.remove("clientDN");
		}

		if(arguments.containsKey("issuerDN")) {
			m_issuerDN = arguments.get("issuerDN");
			arguments.remove("issuerDN");
		}

		if(arguments.containsKey("isSecure")) {
			m_isSecure = arguments.get("isSecure");
			arguments.remove("isSecure");
		}

		/*-----------------------------------------------------------------*/
		/* CONSTRUCTOR PARAMETERS                                          */
		/*-----------------------------------------------------------------*/

		m_arguments = arguments;

		m_transactionID = transactionID;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder execute() throws Exception {

		StringBuilder result = main();

		if(m_routerLoader != null) {
			m_routerLoader.commitAndRelease();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public abstract StringBuilder main() throws Exception;

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "";
	}

	/*---------------------------------------------------------------------*/

	public static String usage() {

		return "";
	}

	/*---------------------------------------------------------------------*/

	public JdbcInterface getRouterLoader() throws Exception {

		if(m_routerLoader == null) {

			m_routerLoader = new BasicLoader(
				ConfigSingleton.getProperty("jdbc_url"),
				ConfigSingleton.getProperty("router_user"),
				ConfigSingleton.getProperty("router_pass")
			);

			m_routerLoader.useDB(ConfigSingleton.getProperty("router_name"));
		}

		return m_routerLoader;
	}

	/*---------------------------------------------------------------------*/
}
