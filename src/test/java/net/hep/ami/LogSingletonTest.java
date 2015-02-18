package net.hep.ami;

import net.hep.ami.LogSingleton.*;

public class LogSingletonTest {
	/*---------------------------------------------------------------------*/

	public static void main(String[] args) {

		LogSingleton.log(LogLevel.INFO, "this is an example of INFO!");
		LogSingleton.log(LogLevel.WARN, "this is an example of WARN!");
		LogSingleton.log(LogLevel.ERROR, "this is an example of ERROR!");

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
