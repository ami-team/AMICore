package net.hep.ami.task;

import java.util.*;

import org.eclipse.jetty.server.*;

import net.hep.ami.*;

public class MainServer2
{
	/*---------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		/*-----------------------------------------------------------------*/

		class MyHandler implements AMIMini.Handler
		{
			/*-------------------------------------------------------------*/

			@Override
			public void init(Map<String, String> config)
			{

			}

			/*-------------------------------------------------------------*/

			@Override
			public StringBuilder exec(String command, Map<String, String> arguments, Map<String, String> config, String ip)
			{
				StringBuilder result = new StringBuilder();

				return result;
			}

			/*-------------------------------------------------------------*/

			@Override
			public StringBuilder help(String command, Map<String, String> arguments, Map<String, String> config, String ip)
			{
				StringBuilder result = new StringBuilder();

				return result;
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		Server server = AMIMini.newInstance(9090, new MyHandler());

		try
		{
			server.start();
			server.join();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
