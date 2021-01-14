package net.hep.ami.utility.shell;

import java.io.*;
import java.util.*;

import com.jcraft.jsch.*;

import net.hep.ami.utility.*;

public class SecureShell extends AbstractShell
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private JSch m_jsch;

	private Session m_session;

	/*----------------------------------------------------------------------------------------------------------------*/

	static final Properties s_properties = new Properties();

	static
	{
		s_properties.put("StrictHostKeyChecking", "no");

		s_properties.put("PreferredAuthentications", "publickey,password");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public SecureShell(String host, int port, String user, String passwordOrPrivateKey) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		m_jsch = new JSch();

		m_session = m_jsch.getSession(user, host, port);

		m_session.setConfig(s_properties);

		/*------------------------------------------------------------------------------------------------------------*/

		if(passwordOrPrivateKey.length() > 64)
		{
			m_jsch.addIdentity(passwordOrPrivateKey);
		}
		else
		{
			m_session.setPassword(passwordOrPrivateKey);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void connect() throws Exception
	{
		m_session.connect();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void disconnect() ////// Exception
	{
		m_session.disconnect();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public String getHomeDirectory() throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		channel.connect();

		String result;

		try
		{
			result = channel.getHome();
		}
		finally
		{
			channel.disconnect();
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String[] args) throws Exception
	{
		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		ChannelExec channel = (ChannelExec) m_session.openChannel("exec");

		channel.setCommand(argsToString(args));

		channel.connect();

		try
		{
			try(StreamReader inputThread = new StreamReader(inputStringBuilder, channel.getInputStream());
			    StreamReader errorThread = new StreamReader(errorStringBuilder, channel. getErrStream ()))
			{
				inputThread.start();
				errorThread.start();

				inputThread.join();
				errorThread.join();

				while(!channel.isClosed())
				{
					Thread.sleep(10);
				}
			}
		}
		finally
		{
			channel.disconnect();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new ShellTuple(channel.getExitStatus(), inputStringBuilder, errorStringBuilder);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void readTextFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		channel.connect();

		try
		{
			channel.cd(fpath);

			try(InputStream inputStream = channel.get(fname))
			{
				TextFile.read(stringBuilder, inputStream);
			}

			channel.exit();
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " (" + fpath + "/" + fname + ")", e);
		}
		finally
		{
			channel.disconnect();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		channel.connect();

		try
		{
			channel.cd(fpath);

			try(OutputStream outputStream = channel.put(fname))
			{
				TextFile.write(outputStream, stringBuilder);
			}

			channel.exit();
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " (" + fpath + "/" + fname + ")", e);
		}
		finally
		{
			channel.disconnect();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
