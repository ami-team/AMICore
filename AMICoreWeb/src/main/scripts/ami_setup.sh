#!/bin/bash

########################################################################################################################
########################################################################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink "${THIS_SCRIPT}") ]]
do
  THIS_SCRIPT=$(readlink "${THIS_SCRIPT}")
done

AMI_HOME=$(cd $(dirname "${THIS_SCRIPT}") && pwd)/..

########################################################################################################################
########################################################################################################################

AMI_U=$(id -u -n)
AMI_G=$(id -g -n)

########################################################################################################################

JAVA_HOME=/usr

########################################################################################################################

JAVA_MS=2G
JAVA_MX=4G
JAVA_SS=20m

########################################################################################################################

TOMCAT_MAX_THREADS=200
TOMCAT_HTTP_HEADER_SIZE=65536

########################################################################################################################

TOMCAT_SHUTDOWN_PORT=8005
TOMCAT_HTTPS_PORT=8443
TOMCAT_HTTP_PORT=8080

########################################################################################################################

TOMCAT_AJP_PORT=8009
TOMCAT_AJP_PACKET_SIZE=65536
TOMCAT_AJP_ADDRESS=$(curl -4 --silent icanhazip.com)
TOMCAT_AJP_SECRET=NOCgJx8ITYdHzdKF6asyrIFqq7dCcqmx3DCLRKUneEl91Xl2flRSnjeBmArS9Sbz

########################################################################################################################

TOMCAT_JVM_ROUTE=$(hostname -s)

########################################################################################################################
########################################################################################################################

while [[ $# -gt 0 ]]
do
  case $1 in
    -p|--base-path)
      BASE_PATH="$2"
      shift
      shift
      ;;
    -u|--user)
      AMI_U="$2"
      shift
      shift
      ;;
    -g|--group)
      AMI_G="$2"
      shift
      shift
      ;;
    --java-home)
      JAVA_HOME="$2"
      shift
      shift
      ;;
    --java-ms)
      JAVA_MS="$2"
      shift
      shift
      ;;
    --java-mx)
      JAVA_MX="$2"
      shift
      shift
      ;;
    --java-ss)
      JAVA_SS="$2"
      shift
      shift
      ;;
    --tomcat-max-threads)
      TOMCAT_MAX_THREADS="$2"
      shift
      shift
      ;;
    --tomcat-shutdown-port)
      TOMCAT_SHUTDOWN_PORT="$2"
      shift
      shift
      ;;
    --tomcat-https-port)
      TOMCAT_HTTPS_PORT="$2"
      shift
      shift
      ;;
    --tomcat-http-port)
      TOMCAT_HTTP_PORT="$2"
      shift
      shift
      ;;
    --tomcat-ajp-port)
      TOMCAT_AJP_PORT="$2"
      shift
      shift
      ;;
    --tomcat-ajp-address)
      TOMCAT_AJP_ADDRESS="$2"
      shift
      shift
      ;;
    --tomcat-ajp-secret)
      TOMCAT_AJP_SECRET="$2"
      shift
      shift
      ;;
    --tomcat-jvm-route)
      TOMCAT_JVM_ROUTE="$2"
      shift
      shift
      ;;
    --help)
      echo -e "Sets-up an AMI Web Server.\n\n$0 --base-path \"${BASE_PATH}\" --user \"${AMI_U}\" --group \"${AMI_G}\" --java-ms \"${JAVA_MS}\" --java-mx \"${JAVA_MX}\" --java-ss \"${JAVA_SS}\" --tomcat-max-threads \"${TOMCAT_MAX_THREADS}\" --tomcat-shutdown-port \"${TOMCAT_SHUTDOWN_PORT}\" --tomcat-https-port \"${TOMCAT_HTTPS_PORT}\" --tomcat-http-port \"${TOMCAT_HTTP_PORT}\" --tomcat-ajp-port \"${TOMCAT_AJP_PORT}\" --tomcat-ajp-address \"${TOMCAT_AJP_ADDRESS}\" --tomcat-ajp-secret \"${TOMCAT_AJP_SECRET}\" --tomcat-jvm-route \"${TOMCAT_JVM_ROUTE}\" --awf \"${AWF}\" --awf-title \"${AWF_TITLE}\" --awf-endpoint \"${AWF_ENDPOINT}\"\n"
      exit 0
      ;;
    -*)
      echo "Unknown option $1"
      exit 1
      ;;
  esac
done

########################################################################################################################
########################################################################################################################

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

########################################################################################################################

function _line()
{
  echo -e "${BLUE}-----------------------------------------------------------------------------${NC}"
}

########################################################################################################################

function _box()
{
  _line
  echo -e "${BLUE}- $1${NC}"
  _line
}

########################################################################################################################

function _ok()
{
  echo -e "                                                                       [${GREEN}OKAY${NC}]"
  #### #
}

########################################################################################################################

function _err()
{
  echo -e "                                                                       [${RED}ERR.${NC}]"
  exit 1
}

########################################################################################################################
########################################################################################################################

_box "Creating '\${AMI_HOME}/conf/AMI.xml'"

(
  cat > "${AMI_HOME}/conf/AMI.xml" << EOF
<?xml version="1.0" encoding="ISO-8859-1"?>

<properties>
  <property name="base_url"><![CDATA[]]></property>

  <property name="admin_user"><![CDATA[]]></property>
  <property name="admin_pass"><![CDATA[]]></property>
  <property name="admin_email"><![CDATA[]]></property>

  <property name="encryption_key"><![CDATA[]]></property>
  <property name="authorized_ips"><![CDATA[]]></property>

  <property name="router_catalog"><![CDATA[]]></property>
  <property name="router_schema"><![CDATA[]]></property>
  <property name="router_url"><![CDATA[]]></property>
  <property name="router_user"><![CDATA[]]></property>
  <property name="router_pass"><![CDATA[]]></property>

  <property name="time_zone"><![CDATA[]]></property>

  <property name="class_path"><![CDATA[]]></property>
</properties>
EOF

  _ok

) || _err

########################################################################################################################
########################################################################################################################

_box "Creating '\${AMI_HOME}/conf/server.xml'"

(
  cat > "${AMI_HOME}/conf/server.xml" << EOF
<?xml version="1.0" encoding="UTF-8"?>

<Server port="${TOMCAT_SHUTDOWN_PORT}" shutdown="SHUTDOWN">

  <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />

  <GlobalNamingResources>

  </GlobalNamingResources>

  <Service name="Catalina">

    <!--*************************************************************************************************************-->
    <!--
    <Connector port="${TOMCAT_HTTPS_PORT}"
               protocol="org.apache.coyote.http11.Http11Nio2Protocol"
               connectionTimeout="20000"
               maxThreads="${TOMCAT_MAX_THREADS}"
               maxHttpHeaderSize="${TOMCAT_HTTP_HEADER_SIZE}"
               compression="on"
               SSLEnabled="true"
               scheme="https"
               secure="true">

        <SSLHostConfig certificateVerification="optional"
                       truststoreFile="\${catalina.home}/conf/truststore.jks"
                       truststorePassword="changeit">

            <Certificate certificateKeystoreFile="\${catalina.home}/conf/certificate.jks"
                         certificateKeystorePassword="changeit" />

        </SSLHostConfig>

    </Connector>
    -->
    <!--*************************************************************************************************************-->

    <Connector port="${TOMCAT_HTTP_PORT}"
               redirectPort="${TOMCAT_HTTPS_PORT}"
               protocol="org.apache.coyote.http11.Http11Nio2Protocol"
               connectionTimeout="20000"
               maxThreads="${TOMCAT_MAX_THREADS}"
               maxHttpHeaderSize="${TOMCAT_HTTP_HEADER_SIZE}" />

    <!--*************************************************************************************************************-->

    <Connector port="${TOMCAT_AJP_PORT}"
               redirectPort="${TOMCAT_HTTPS_PORT}"
               protocol="org.apache.coyote.ajp.AjpNio2Protocol"
               connectionTimeout="20000"
               maxThreads="${TOMCAT_MAX_THREADS}"
               packetSize="${TOMCAT_AJP_PACKET_SIZE}"
               address="${TOMCAT_AJP_ADDRESS}"
               secret="${TOMCAT_AJP_SECRET}"
               secretRequired="true"
               allowedRequestAttributesPattern=".*" />

    <!--*************************************************************************************************************-->

    <Engine name="Catalina"
            defaultHost="localhost"
            jvmRoute="${TOMCAT_JVM_ROUTE}">

        <Host name="localhost"
              appBase="webapps"
              unpackWARs="true"
              autoDeploy="true">

            <Valve className="org.apache.catalina.valves.AccessLogValve"
                   directory="logs"
                   prefix="localhost_access_log"
                   suffix=".txt"
                   pattern="%h %l %u %t &quot;%r&quot; %s %b" />

        </Host>

    </Engine>

    <!--*************************************************************************************************************-->

  </Service>

</Server>
EOF

  _ok

) || _err

########################################################################################################################
########################################################################################################################

_box "Creating '\${AMI_HOME}/bin/setenv.sh'"

(
  cat > "${AMI_HOME}/bin/setenv.sh" << EOF
JAVA_HOME=$JAVA_HOME

JAVA_OPTS='-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'

CATALINA_OPTS='-Xms${JAVA_MS} -Xmx${JAVA_MX} -Xss${JAVA_SS} -server -XX:+UseParallelGC'
EOF

  _ok

) || _err

########################################################################################################################
########################################################################################################################

_line

########################################################################################################################
########################################################################################################################
