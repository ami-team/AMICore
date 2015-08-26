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
	public void sendFile(String fpath, String fname, StringBuilder content) throws Exception
	{
		InputStream inputStream = new ByteArrayInputStream(content.toString().getBytes());

		try
		{
			ChannelSftp channel = (ChannelSftp) m_session.openChannel("sftp");

			try
			{
				channel.cd(fpath);
				channel.put(inputStream, fpath + File.separator + fname);
				channel.exit();
			}
			finally
			{
				channel.disconnect();
			}
		}
		finally
		{
			inputStream.close();
		}
	}

	/*---------------------------------------------------------------------*/
}
