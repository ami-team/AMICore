package net.hep.ami.utility.shell;

import lombok.*;

import java.io.*;
import java.nio.charset.*;
import java.util.concurrent.atomic.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public abstract class AbstractShell
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected static final org.slf4j.Logger LOG = LogSingleton.getLogger(AbstractShell.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	public void set2FACode(String tfaCode)
	{
		if(!Empty.is(tfaCode, Empty.STRING_NULL_EMPTY_BLANK))
		{
			LOG.info("2AF not available");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@ToString
	@AllArgsConstructor
	public static class ShellTuple
	{
		private final Integer errorCode;

		private final StringBuilder inputStringBuilder;

		private final StringBuilder errorStringBuilder;
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
