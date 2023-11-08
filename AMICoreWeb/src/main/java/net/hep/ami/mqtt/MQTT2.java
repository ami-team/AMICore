package net.hep.ami.mqtt;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.*;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;

import com.hivemq.client.mqtt.*;
import com.hivemq.client.mqtt.mqtt3.*;
import com.hivemq.client.mqtt.mqtt3.message.publish.*;
import com.hivemq.client.mqtt.datatypes.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class MQTT2
{
    /*----------------------------------------------------------------------------------------------------------------*/

    private static final int PING_PERIOD = ConfigSingleton.getProperty("ping_period", 30);

    private static final String MQTT_BROKER_ENDPOINT = ConfigSingleton.getProperty("mqtt_broker_endpoint", "");

    private static final String MQTT_JWT_ISSUER = ConfigSingleton.getProperty("mqtt_jwt_issuer", "");

    private static final String MQTT_JWT_SECRET = ConfigSingleton.getProperty("mqtt_jwt_secret", "");

    private static final String MQTT_USERNAME = "cronjob";

    /*----------------------------------------------------------------------------------------------------------------*/

    private static final org.slf4j.Logger LOG = LogSingleton.getLogger(MQTT.class.getSimpleName());

    /*----------------------------------------------------------------------------------------------------------------*/

    private String m_serverName;

    private Mqtt3AsyncClient m_asyncClient;

    private final Timer m_timer = new Timer();

    /*----------------------------------------------------------------------------------------------------------------*/

    public void init()
    {
        /*------------------------------------------------------------------------------------------------------------*/

        boolean err1 = Empty.is(MQTT_BROKER_ENDPOINT, Empty.STRING_NULL_EMPTY_BLANK);
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

            m_serverName = InetAddress.getLocalHost().getCanonicalHostName();

            /*--------------------------------------------------------------------------------------------------------*/

            Algorithm algorithm = Algorithm.HMAC512(MQTT_JWT_SECRET);

            String mqttToken = JWT.create()
                    .withIssuer(MQTT_JWT_ISSUER)
                    .withSubject(MQTT_USERNAME)
                    .sign(algorithm)
                    ;

            /*--------------------------------------------------------------------------------------------------------*/

            URI uri = new URI(MQTT_BROKER_ENDPOINT);

            String host = uri.getHost();
            int port = uri.getPort();

            if(port < 0)
            {
                String protocol = uri.getScheme();

                /**/ if("ws".equalsIgnoreCase(protocol)) {
                    port = 80;
                }
                else if("wss".equalsIgnoreCase(protocol)) {
                    port = 443;
                }
                else {
                    port = 1883;
                }
            }

            /*--------------------------------------------------------------------------------------------------------*/

            m_asyncClient = MqttClient.builder()
                    .useMqttVersion3()
                    /* Authentication */
                    .identifier(m_serverName + "-" + UUID.randomUUID())
                    .serverHost(host)
                    .serverPort(port)
                    .sslWithDefaultConfig()
                    .webSocketWithDefaultConfig()
                    /* High availability */
                    .automaticReconnectWithDefaultConfig()
                    .addDisconnectedListener((ctx) -> LOG.info("client `{}` disconnected from server URL `{}`", m_serverName, MQTT_BROKER_ENDPOINT))
                    /* Builder */
                    .buildAsync()
            ;

            /*--------------------------------------------------------------------------------------------------------*/

            m_asyncClient.publishes(MqttGlobalPublishFilter.SUBSCRIBED, this::onMessageReceived);

            /*--------------------------------------------------------------------------------------------------------*/

            m_asyncClient.connectWith()
                    .simpleAuth().username(MQTT_USERNAME).password(mqttToken.getBytes()).applySimpleAuth()
                    .cleanSession(true)
                    .keepAlive(10)
                    .send()
                    .whenComplete((connAck, throwable) -> {

                        if(throwable != null)
                        {
                            LOG.error("client `{}` failed to connect to server URL `{}`: `{}`", m_serverName, MQTT_BROKER_ENDPOINT, throwable.getMessage());
                        }
                        else
                        {
                            LOG.info("client `{}` connect successfully to server URL `{}`", m_serverName, MQTT_BROKER_ENDPOINT);

                            subscribe("ami/" + m_serverName + "/command");
                            subscribe("ami/" + m_serverName + "/command/");
                            subscribe("ami/" + m_serverName + "/command/AMIXmlToCsv.xsl");
                            subscribe("ami/" + m_serverName + "/command/AMIXmlToJson.xsl");
                            subscribe("ami/" + m_serverName + "/command/AMIXmlToText.xsl");
                            subscribe("ami/" + m_serverName + "/command/AMIXmlToXml.xsl");
                        }
                    })
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

    @SuppressWarnings("SameParameterValue")
    private void publish(@NotNull String topic, @NotNull String payload)
    {
        try
        {
            m_asyncClient.publishWith()
                    .topic(topic)
                    .payload(payload.getBytes(StandardCharsets.UTF_8))
                    .qos(MqttQos.AT_MOST_ONCE)
                    .retain(false)
                    .send()
            ;
        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private void subscribe(@NotNull String topic)
    {
        try
        {
            m_asyncClient.subscribeWith()
                    .addSubscription().topicFilter(topic).qos(MqttQos.AT_MOST_ONCE).applySubscription()
                    .send()
                    .whenComplete((subAck, subThrowable) -> {

                        if(subThrowable != null)
                        {
                            LOG.error("client `{}` failed to suscribe to topic: `{}`: `{}`", m_serverName, topic, subThrowable.getMessage());
                        }
                        else
                        {
                            LOG.info("client `{}` suscribed to topic `{}`.", m_serverName, topic);
                        }
                    });
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private void onMessageReceived(Mqtt3Publish mqtt3Publish)
    {
        LOG.debug("message received from topic `{}`: `{}`.", mqtt3Publish.getTopic(), mqtt3Publish.getPayload());
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

        publish("ami/webserver/ping", String.format("{\"timestamp\": %d, \"server_name\": \"%s\", \"state\": \"%s\", \"free_mem\": %d, \"total_mem\": %d, \"free_disk\": %d, \"total_disk\": %d, \"nb_of_cpus\": %d}",
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
