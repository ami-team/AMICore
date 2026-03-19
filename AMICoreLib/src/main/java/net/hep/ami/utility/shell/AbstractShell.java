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

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(AbstractShell.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	private final AtomicBoolean m_tfaDone = new AtomicBoolean();

	/*----------------------------------------------------------------------------------------------------------------*/

	private final @Nullable String m_tfaPrompt;

	private volatile @Nullable String m_tfaCode;

	/*----------------------------------------------------------------------------------------------------------------*/

	public AbstractShell(@Nullable String tfaPrompt)
	{
		m_tfaDone.set(false);

		m_tfaPrompt = tfaPrompt;

		m_tfaCode = null;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@ToString
	@AllArgsConstructor
	public class ShellTuple
	{
		private final Integer errorCode;

		private final StringBuilder inputStringBuilder;

		private final StringBuilder errorStringBuilder;
	}
	/*----------------------------------------------------------------------------------------------------------------*/

	public void set2FACode(String tfaCode)
	{
		m_tfaDone.set(
			Empty.is(m_tfaPrompt, Empty.STRING_NULL_EMPTY_BLANK)
			||
			Empty.is(tfaCode, Empty.STRING_NULL_EMPTY_BLANK)
		);

		m_tfaCode = tfaCode;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected class StreamReader extends Thread implements Closeable
	{
		private final StringBuilder m_tfaWindow = new StringBuilder();

		private final StringBuilder m_stringBuilder;
		private final InputStream m_inputStream;
		private final OutputStream m_outputStream;

		public StreamReader(StringBuilder stringBuilder, InputStream inputStream, OutputStream outputStream)
		{
			m_stringBuilder = stringBuilder;
			m_inputStream = inputStream;
			m_outputStream = outputStream;
		}

		@Override
		public void run()
		{
			try
			{
				InputStreamReader reader = new InputStreamReader(m_inputStream, StandardCharsets.UTF_8);

				for(int c; (c = reader.read()) != -1; )
				{
					char ch = (char) c;

					m_stringBuilder.append(ch);

					/*------------------------------------------------------------------------------------------------*/
					/* 2FA PROMPT DETECTION                                                                           */
					/*------------------------------------------------------------------------------------------------*/

					if(!m_tfaDone.get() && m_tfaPrompt != null)
					{
						m_tfaWindow.append(ch);

						if(m_tfaWindow.length() > m_tfaPrompt.length())
						{
							m_tfaWindow.delete(0, m_tfaWindow.length() - m_tfaPrompt.length());
						}

						if(m_tfaWindow.toString().equals(m_tfaPrompt) && m_tfaDone.compareAndSet(false, true))
						{
							m_outputStream.write((m_tfaCode + "\n").getBytes(StandardCharsets.UTF_8));

							m_outputStream.flush();
						}
					}

					/*------------------------------------------------------------------------------------------------*/
				}
			}
			catch(Exception e)
			{
				LOG.error("could not read stream", e);
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
