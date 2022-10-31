package net.hep.ami.mqtt;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.*;

import org.jetbrains.annotations.*;

public class MQTT implements MqttCallbackExtended
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final int PING_PERIOD = ConfigSingleton.getProperty("ping_period", 30);

	private static final String MQTT_BROKER_URL = ConfigSingleton.getProperty("mqtt_broker_url", "");

	private static final String MQTT_JWT_ISSUER = ConfigSingleton.getProperty("mqtt_jwt_issuer", "");

	private static final String MQTT_JWT_SECRET = ConfigSingleton.getProperty("mqtt_jwt_secret", "");

	private static final String MQTT_USERNAME = "cronjob";

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(MQTT.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	private String m_serverName;

	private MqttAsyncClient m_asyncClient;

	private final Timer m_timer = new Timer();

	/*----------------------------------------------------------------------------------------------------------------*/

	public void init()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		boolean err1 = Empty.is(MQTT_BROKER_URL, Empty.STRING_NULL_EMPTY_BLANK);
		boolean err2 = Empty.is(MQTT_JWT_ISSUER, Empty.STRING_NULL_EMPTY_BLANK);
		boolean err3 = Empty.is(MQTT_JWT_SECRET, Empty.STRING_NULL_EMPTY_BLANK);

		if(err1 || err2 || err3)
		{
			LOG.warn("MQTT not configured, broker url: " + (err1 ? "error" : "okay") + ", jwt issuer: " + (err2 ? "error" : "okay") + ", jwt secret: " + (err3 ? "error" : "okay") );

			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			m_serverName = InetAddress.getLocalHost().getHostName();

			/*--------------------------------------------------------------------------------------------------------*/

			Algorithm algorithm = Algorithm.HMAC512(MQTT_JWT_SECRET);

			String mqttToken = JWT.create()
			                      .withIssuer(MQTT_JWT_ISSUER)
			                      .withSubject(MQTT_USERNAME)
			                      .sign(algorithm)
			;

			/*--------------------------------------------------------------------------------------------------------*/

			MqttConnectOptions connectOptions = new MqttConnectOptions();

			connectOptions.setServerURIs(new String[] { MQTT_BROKER_URL });

			connectOptions.setUserName(/**/ MQTT_USERNAME /**/);
			connectOptions.setPassword(mqttToken.toCharArray());

			connectOptions.setAutomaticReconnect(true);

			connectOptions.setCleanSession(true);

			/*--------------------------------------------------------------------------------------------------------*/

			m_asyncClient = new MqttAsyncClient(MQTT_BROKER_URL, m_serverName + "-" + UUID.randomUUID(), new MemoryPersistence());

			m_asyncClient.setCallback(this);

			m_asyncClient.connect(connectOptions)
			             .waitForCompletion(10000L)
			;

			/*--------------------------------------------------------------------------------------------------------*/

			startScheduler();

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private long getCurrentTime()
	{
		return System.currentTimeMillis() / 1000L;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void publish(@NotNull String topic, @NotNull String payload)
	{
		try
		{
			m_asyncClient.publish(topic, payload.getBytes(StandardCharsets.UTF_8), 0, false);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void startScheduler()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		this.notifyServer("STARTING");

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			TimerTask timerTask = new TimerTask() {

				/*----------------------------------------------------------------------------------------------------*/

				public void run()
				{
					notifyServer("ALIVE");
				}

				/*----------------------------------------------------------------------------------------------------*/
			};

			m_timer.schedule(timerTask, (long) PING_PERIOD * 1000, (long) PING_PERIOD * 1000);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void connectComplete(boolean reconnect, String serverURL)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(reconnect) {
			LOG.info("client `{}` reconnected to server URL `{}`", m_serverName, serverURL);
		}
		else {
			LOG.info("client `{}` connected to server URL `{}`", m_serverName, serverURL);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			m_asyncClient.subscribe("ami/" + m_serverName + "/command"              , 0).waitForCompletion(10000L);
			m_asyncClient.subscribe("ami/" + m_serverName + "/command/"              , 0).waitForCompletion(10000L);
			m_asyncClient.subscribe("ami/" + m_serverName + "/command/AMIXmlToCsv.xsl", 0).waitForCompletion(10000L);
			m_asyncClient.subscribe("ami/" + m_serverName + "/command/AMIXmlToJson.xsl", 0).waitForCompletion(10000L);
			m_asyncClient.subscribe("ami/" + m_serverName + "/command/AMIXmlToText.xsl", 0).waitForCompletion(10000L);
			m_asyncClient.subscribe("ami/" + m_serverName + "/command/AMIXmlToXml.xsl", 0).waitForCompletion(10000L);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void connectionLost(Throwable cause)
	{
		LOG.warn("client `{}` disconnected, cause: {}", m_serverName, cause.getMessage());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void messageArrived(String topic, MqttMessage message)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		/* TODO */

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		/* DO NOTHING  */
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	private void notifyServer(String state)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		long memFree = 0;
		long memTotal = 0;

		File memFile = new File("/proc/meminfo");

		if(memFile.exists())
		{
			try(BufferedReader bufferedReader = new BufferedReader(new FileReader(memFile)))
			{
				for(String line; (line = bufferedReader.readLine()) != null; )
				{
					String[] parts = line.split("[:\\s]+");

					if(parts.length == 3)
					{
						/**/ if("MemAvailable".equalsIgnoreCase(parts[0])) {
						memFree = Long.parseLong(parts[1]);
					}
					else if("MemTotal".equalsIgnoreCase(parts[0])) {
						memTotal = Long.parseLong(parts[1]);
					}
					}
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		File diskFile = new File(MQTT.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		long diskFree = diskFile.getFreeSpace() / 1024;
		long diskTotal = diskFile.getTotalSpace() / 1024;

		/*------------------------------------------------------------------------------------------------------------*/

		publish("ami/server/ping", String.format("{\"timestamp\": %d, \"server_name\": \"%s\", \"state\": \"%s\", \"free_mem\": %d, \"total_mem\": %d, \"free_disk\": %d, \"total_disk\": %d, \"nb_of_cpus\": %d}",
			getCurrentTime(),
			Utility.escapeJSONString(m_serverName, false),
			state,
			memFree,
			memTotal,
			diskFree,
			diskTotal,
			Runtime.getRuntime().availableProcessors()
		));

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
