package net.hep.ami.utility.shell;

import java.io.*;
import java.util.*;

import com.jcraft.jsch.*;

import net.hep.ami.utility.*;

public class SecureShell extends ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	JSch m_jsch;

	Session m_session;

	/*---------------------------------------------------------------------*/

	static final Properties s_properties = new Properties();

	static
	{
		s_properties.put("StrictHostKeyChecking", "no");

		s_properties.put("PreferredAuthentications", "publickey,password");
	}

	/*---------------------------------------------------------------------*/

	public SecureShell(String host, int port, String user, String passwordOrPrivateKey) throws Exception
	{
		m_jsch = new JSch();

		m_session = m_jsch.getSession(user, host, port);

		m_session.setConfig(s_properties);

		if(passwordOrPrivateKey.length() > 64)
		{
			m_jsch.addIdentity(passwordOrPrivateKey);
		}
		else
		{
			m_session.setPassword(passwordOrPrivateKey);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void connect() throws Exception
	{
		m_session.connect();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void disconnect() throws Exception
	{
		m_session.disconnect();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String[] args) throws Exception
	{
		int exitStatus;

		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ChannelExec channel = (ChannelExec) m_session.openChannel("exec");

		channel.setCommand(argsToString(args));

		channel.connect();

		try
		{
			Thread inputThread = new StreamReader(inputStringBuilder, channel.getInputStream());
			Thread errorThread = new StreamReader(errorStringBuilder, channel.getErrStream());

			inputThread.start();
			errorThread.start();

			inputThread.join();
			errorThread.join();

			while(channel.isClosed() == false)
			{
				Thread.sleep(1);
			}

			exitStatus = channel.getExitStatus();
		}
		finally
		{
			channel.disconnect();
		}

		/*-----------------------------------------------------------------*/

		return new ShellTuple(exitStatus, inputStringBuilder, errorStringBuilder);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void readTextFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		channel.connect();

		try
		{
			channel.cd(fpath);

			InputStream inputStream = channel.get(fname);

			try
			{
				TextFile.read(stringBuilder, inputStream);
			}
			finally
			{
				inputStream.close();
			}

			channel.exit();
		}
		finally
		{
			channel.disconnect();
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		channel.connect();

		try
		{
			channel.cd(fpath);

			OutputStream outputStream = channel.put(fname);

			try
			{
				TextFile.write(outputStream, stringBuilder);
			}
			finally
			{
				outputStream.close();
			}

			channel.exit();
		}
		finally
		{
			channel.disconnect();
		}
	}

	/*---------------------------------------------------------------------*/
}
