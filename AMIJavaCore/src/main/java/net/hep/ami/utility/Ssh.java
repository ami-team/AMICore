package net.hep.ami.utility;

import com.jcraft.jsch.*;

public class Ssh extends ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	JSch m_jsch;

	Session m_session;

	/*---------------------------------------------------------------------*/

	public Ssh(String host, int port, String user, String password) throws Exception
	{
		m_jsch = new JSch();

		m_session = m_jsch.getSession(user, host, port);

		m_session.setPassword(password);
	}

	/*---------------------------------------------------------------------*/

	public void connect() throws Exception
	{
		m_session.connect();
	}

	/*---------------------------------------------------------------------*/

	public void disconnect()
	{
		m_session.disconnect();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String command) throws Exception
	{
		ChannelExec channel = (ChannelExec) m_session.openChannel("exec");

		channel.setCommand(command);

		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		new Thread(new StreamReader(inputStringBuilder, channel.getInputStream())).start();
		new Thread(new StreamReader(errorStringBuilder, channel.getErrStream())).start();

		return new ShellTuple(channel.getExitStatus(), inputStringBuilder, errorStringBuilder);
	}

	/*---------------------------------------------------------------------*/
}
