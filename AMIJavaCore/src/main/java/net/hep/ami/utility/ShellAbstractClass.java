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
			String line;

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_inputStream));

			try
			{
				while((line = bufferedReader.readLine()) != null)
				{
					m_stringBuilder.append(line);
					m_stringBuilder.append('\n');
				}
			}
			catch(IOException e)
			{
				/* IGNORE */
			}
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch(IOException e)
				{
					/* IGNORE */

				}
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public abstract ShellTuple exec(String command) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract void sendFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception;

	/*---------------------------------------------------------------------*/
}
