package net.hep.ami.utility.shell;

import java.io.*;
import java.util.*;
import java.nio.charset.*;
import java.util.concurrent.*;

import org.apache.sshd.client.*;
import org.apache.sshd.client.channel.*;
import org.apache.sshd.client.session.*;
import org.apache.sshd.client.keyverifier.*;
import org.apache.sshd.sftp.client.*;

import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class SecureShell extends AbstractShell
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final SshClient m_client;

	private ClientSession m_session;

	private final TwoFactorUserInfo m_userInfo;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final String m_host;
	private final int    m_port;
	private final String m_user;

	/*----------------------------------------------------------------------------------------------------------------*/

	static final long s_timeout = 10L;

	/*----------------------------------------------------------------------------------------------------------------*/

	public SecureShell(String host, int port, @Nullable String user, @Nullable String passwordOrPrivateKey) throws Exception
	{
		this(host, port, user, passwordOrPrivateKey, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public SecureShell(String host, int port, @Nullable String user, @Nullable String pass, @Nullable String tfaPrompt) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		m_host = host;
		m_port = port;
		m_user = user;

		/*------------------------------------------------------------------------------------------------------------*/

		m_userInfo = new TwoFactorUserInfo(pass, tfaPrompt);

		/*------------------------------------------------------------------------------------------------------------*/

		m_client = SshClient.setUpDefaultClient();

		m_client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);

		m_client.setUserInteraction(m_userInfo);

		m_client.start();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void set2FACode(@Nullable String tfaCode)
	{
		m_userInfo.set2FACode(tfaCode);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void connect() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		m_session = m_client.connect(m_user, m_host, m_port)
		                    .verify(s_timeout, TimeUnit.SECONDS)
		                    .getSession()
		;

		/*------------------------------------------------------------------------------------------------------------*/

		//m_session.addPasswordIdentity(m_pass);

		/*------------------------------------------------------------------------------------------------------------*/

		m_session.auth().verify(s_timeout, TimeUnit.SECONDS);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void disconnect() ////// Exception
	{
		if(m_session != null)
		{
			m_session.close(false);
		}

		if(m_client != null)
		{
			m_client.stop();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public String getHomeDirectory() throws Exception
	{
		try(SftpClient channel = SftpClientFactory.instance().createSftpClient(m_session))
		{
			return channel.canonicalPath(".");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String[] args) throws Exception
	{
		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		try(ClientChannel channel = m_session.createExecChannel(argsToString(args));
			ByteArrayOutputStream inputStream = new ByteArrayOutputStream();
			ByteArrayOutputStream errorStream = new ByteArrayOutputStream())
		{
			channel.setOut(inputStream);
			channel.setErr(errorStream);

			channel.open().verify(s_timeout, TimeUnit.SECONDS);

			channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);

			inputStringBuilder.append(inputStream.toString(StandardCharsets.UTF_8));
			errorStringBuilder.append(errorStream.toString(StandardCharsets.UTF_8));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new ShellTuple(0, inputStringBuilder, errorStringBuilder);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void readTextFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception
	{
		try(SftpClient channel = SftpClientFactory.instance().createSftpClient(m_session))
		{
			try(InputStream inputStream = channel.read(fpath + "/" + fname))
			{
				TextFile.read(stringBuilder, inputStream);
			}
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " (" + fpath + "/" + fname + ")", e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		try(SftpClient channel = SftpClientFactory.instance().createSftpClient(m_session))
		{
			try(OutputStream outputStream = channel.write(fpath + "/" + fname))
			{
				TextFile.write(outputStream, stringBuilder);
			}
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " (" + fpath + "/" + fname + ")", e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}