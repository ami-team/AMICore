package net.hep.ami;

import java.util.logging.*;

public class LogSingleton {
	/*---------------------------------------------------------------------*/

	public static enum LogLevel {
		INFO,
		WARN,
		ERROR,
		CRITICAL,
	}

	/*---------------------------------------------------------------------*/

	private static final int m_logLevel = ConfigSingleton.getProperty("log_level", 0);

	/*---------------------------------------------------------------------*/

	private static final String m_from = ConfigSingleton.getProperty("log_from");

	private static final String m_to = ConfigSingleton.getProperty("log_to");

	private static final String m_cc = ConfigSingleton.getProperty("log_cc");

	/*---------------------------------------------------------------------*/

	private static Logger m_logger = Logger.getLogger(LogSingleton.class.getName());

	/*---------------------------------------------------------------------*/

	public static void log(LogLevel logLevel, String message) {

		if(m_logLevel <= logLevel.ordinal()) {
			/*-------------------------------------------------------------*/
			/* GET CLASS NAME AND METHOD NAME                              */
			/*-------------------------------------------------------------*/

			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

			String className = stackTraceElements[2].getClassName();
			String methodName = stackTraceElements[2].getMethodName();

			/*-------------------------------------------------------------*/
			/* WRITE MESSAGE                                               */
			/*-------------------------------------------------------------*/

			switch(logLevel) {

				case INFO:
					m_logger.logp(Level.INFO, className, methodName, message);
					break;

				case WARN:
					m_logger.logp(Level.WARNING, className, methodName, message);
					break;

				case ERROR:
					m_logger.logp(Level.SEVERE, className, methodName, message);
					break;

				case CRITICAL:
					m_logger.logp(Level.SEVERE, className, methodName, message);

					if(!m_to.isEmpty()
					   ||
					   !m_cc.isEmpty()
					 ) {

						try {
							MailSingleton.sendMessage(m_from, m_to, m_cc, String.format("CRITICAL ERROR (%s.%s)", className, methodName), message);

						} catch(Exception e) {
							/* IGNORE */
						}
					}

					break;
			}

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/
}
