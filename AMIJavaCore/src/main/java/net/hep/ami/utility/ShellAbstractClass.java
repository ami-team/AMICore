package net.hep.ami.utility;

import java.io.*;

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

	protected static class StreamReader implements Runnable
	{
		private StringBuilder m_stringBuilder;
		private InputStream   m_inputStream  ;

		public StreamReader(StringBuilder stringBuilder, InputStream inputStream)
		{
			m_stringBuilder = stringBuilder;
			m_inputStream   = inputStream  ;
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
				/* IGNORE */
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public abstract ShellTuple exec(String command) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract void sendFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception;

	/*---------------------------------------------------------------------*/
}
