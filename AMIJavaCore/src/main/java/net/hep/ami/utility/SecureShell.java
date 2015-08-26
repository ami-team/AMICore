package net.hep.ami.utility;

import java.io.*;

import com.jcraft.jsch.*;

public class SecureShell extends ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	JSch m_jsch;

	Session m_session;

	/*---------------------------------------------------------------------*/

	public SecureShell(String host, int port, String user, String password) throws Exception
	{
		m_jsch = new JSch();

		m_session = m_jsch.getSession(user, host, port);

		m_session.setPassword(password);
		m_session.setConfig("StrictHostKeyChecking", "no");
	}

	/*---------------------------------------------------------------------*/

	public void connect() throws Exception
	{
		m_session.connect();
	}

	/*---------------------------------------------------------------------*/

	public void disconnect() throws Exception
	{
		m_session.disconnect();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String command) throws Exception
	{
		int exitStatus;

		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		ChannelExec channel = (ChannelExec) m_session.openChannel("exec");

		try
		{
			channel.setCommand(command);

			new Thread(new StreamReader(inputStringBuilder, channel.getInputStream())).start();
			new Thread(new StreamReader(errorStringBuilder, channel.getErrStream())).start();

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
	public void readFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		try
		{
			channel.cd(fpath);

			InputStream inputStream = channel.get(fpath + File.separator + fname);

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
	public void writeFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

		try
		{
			channel.cd(fpath);

			OutputStream outputStream = channel.put(fpath + File.separator + fname);

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
