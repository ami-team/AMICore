package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
//import net.hep.ami.jdbc.*;

public class AddUser extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

//	private String m_firstName = "";
//	private String m_lastName = "";
//	private String m_email = "";
//	private String m_amiLogin = "";
//	private String m_amiPassword = "";

	/*---------------------------------------------------------------------*/

	public AddUser(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);

//		m_firstName = arguments.get("firstName");
//		m_lastName = arguments.get("lastName");
//		m_email = arguments.get("email");
//		m_amiLogin = arguments.get("amiLogin");
//		m_amiPassword = arguments.get("amiPassword");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {
		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

//		QueryResult queryResult = getRouterLoader().executeQuery("SELECT COUNT(*) FROM `router_user` WHERE `AMIUser` = '" + m_amiLogin + "'");

//		if(queryResult.getNumberOfRows() > 0) {
//			throw new Exception("");
//		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		/* TODO */

		/*-----------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		/* TODO */

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {
		return "Add a new user.";
	}
}
