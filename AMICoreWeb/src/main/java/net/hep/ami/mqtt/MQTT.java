package net.hep.ami.mqtt;

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

	private static final int PING_DELAY = ConfigSingleton.getProperty("ping_delay", 10);

	private static final String MQTT_BROKER_URL = ConfigSingleton.getProperty("mqtt_broker_url", "");

	private static final String MQTT_JWT_ISSUER = ConfigSingleton.getProperty("mqtt_jwt_issuer", "");

	private static final String MQTT_JWT_SECRET = ConfigSingleton.getProperty("mqtt_jwt_secret", "");

	private static final String MQTT_USERNAME = "cronjob";

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
		 	LogSingleton.root.warn("MQTT not configured, broker url: " + (err1 ? "error" : "okay") + ", jwt issuer: " + (err2 ? "error" : "okay") + ", jwt secret: " + (err3 ? "error" : "okay") );
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
			LogSingleton.root.error(e.getMessage(), e);
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
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void startScheduler()
	{
		try
		{
			TimerTask timerTask = new TimerTask() {

				/*----------------------------------------------------------------------------------------------------*/

				public void run()
				{
					publish("ami/server/ping", String.format("{\"timestamp\": %d, \"server_name\": \"%s\"}",
						getCurrentTime(),
						Utility.escapeJSONString(m_serverName, false)
					));
				}

				/*----------------------------------------------------------------------------------------------------*/
			};

			m_timer.schedule(timerTask, 0, (long) PING_DELAY * 1000);
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void connectComplete(boolean reconnect, String serverURL)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(reconnect) {
			LogSingleton.root.info("client `{}` reconnected to server URL `{}`", m_serverName, serverURL);
		}
		else {
			LogSingleton.root.info("client `{}` connected to server URL `{}`", m_serverName, serverURL);
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
			LogSingleton.root.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void connectionLost(Throwable cause)
	{
		LogSingleton.root.warn("client `{}` disconnected, cause: {}", m_serverName, cause.getMessage());
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
		/* do nothing */
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
