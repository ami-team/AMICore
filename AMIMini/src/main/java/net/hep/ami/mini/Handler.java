package net.hep.ami.mini;

import java.util.*;

public interface Handler
{
	/*---------------------------------------------------------------------*/

	public void init(Server server, Map<String, String> config) throws Exception;

	/*---------------------------------------------------------------------*/

	public StringBuilder exec(Server server, Map<String, String> config, String command, Map<String, String> arguments, String ip) throws Exception;

	public StringBuilder help(Server server, Map<String, String> config, String command, Map<String, String> arguments, String ip) throws Exception;

	/*---------------------------------------------------------------------*/
}
