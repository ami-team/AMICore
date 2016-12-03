package net.hep.ami.utility.shell;

import java.io.*;

import net.hep.ami.utility.*;

public abstract class ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	public static class ShellTuple
	{
		public Integer errorCode;

		public StringBuilder inputStringBuilder;
		public StringBuilder errorStringBuilder;

		public ShellTuple(int _errorCode, StringBuilder _inputStringBuilder, StringBuilder _errorStringBuilder)
		{
			errorCode = _errorCode;

			inputStringBuilder = _inputStringBuilder;
			errorStringBuilder = _errorStringBuilder;
		}
	}

	/*---------------------------------------------------------------------*/

	protected static class StreamReader extends Thread
	{
		private StringBuilder m_stringBuilder;
		private InputStream m_inputStream;

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
				e.printStackTrace();
			}
		}
	}

	/*---------------------------------------------------------------------*/

	protected String argsToString(String[] args)
	{
		/*-----------------------------------------------------------------*/

		final int length = args.length;

		if(length == 0)
		{
			return "";
		}

		/*-----------------------------------------------------------------*/

		String result = args[0];

		for(int i = 1; i < length; i++)
		{
			result = result.concat(" \"" + args[i].replace("\"", "\\\"")  + "\"");
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public abstract void connect() throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract void disconnect() throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract ShellTuple exec(String[] args) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract void readTextFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception;
	public abstract void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception;

	/*---------------------------------------------------------------------*/
}
