package net.hep.ami.utility.shell;

import java.io.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

public abstract class AbstractShell
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(AbstractShell.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	public static class ShellTuple
	{
		public final Integer errorCode;

		public final StringBuilder inputStringBuilder;
		public final StringBuilder errorStringBuilder;

		public ShellTuple(int _errorCode, StringBuilder _inputStringBuilder, StringBuilder _errorStringBuilder)
		{
			errorCode = _errorCode;

			inputStringBuilder = _inputStringBuilder;
			errorStringBuilder = _errorStringBuilder;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected static class StreamReader extends Thread implements Closeable
	{
		private final StringBuilder m_stringBuilder;
		private final InputStream m_inputStream;

		public StreamReader(StringBuilder stringBuilder, InputStream inputStream)
		{
			m_stringBuilder = stringBuilder;
			m_inputStream = inputStream;
		}

		@Override
		public void run()
		{
			try
			{
				TextFile.read(m_stringBuilder, m_inputStream);
			}
			catch(Exception e)
			{
				LOG.error("could not read text file", e);
			}
		}

		@Override
		public void close() throws IOException
		{
			m_inputStream.close();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected String argsToString(String[] args)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		final int length = args.length;

		if(length == 0)
		{
			return "";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String result = args[0];

		/*-----------------------------------------------------------------------------------------*/
		/* BERKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK */
		/*-----------------------------------------------------------------------------------------*/

		for(int i = 1; i < length; i++)
		{
			result = result.concat(" \"" + args[i].replace("\"", "\\\"")  + "\"");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public abstract void connect() throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	public abstract void disconnect() throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	public abstract String getHomeDirectory() throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	public abstract ShellTuple exec(String[] args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	public abstract void readTextFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception;

	public abstract void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/
}
