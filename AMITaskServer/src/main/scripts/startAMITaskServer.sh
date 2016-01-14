#!/bin/sh

export JAVA_HOME=/usr/local/java
JAVA_MIN_RAM=512M
JAVA_MAX_RAM=2G

#############################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink $THIS_SCRIPT) ]]
do
  THIS_SCRIPT=$(readlink $THIS_SCRIPT)
done

AMI_HOME=$(cd $(dirname $THIS_SCRIPT) && pwd)

#############################################################################

if [[ -z $(ps -ef | grep "net\.hep\.ami\.task\.MainServer") ]]
then
  ###########################################################################

  AMICLASSPATH=''

  for jar in $AMI_HOME/lib/*.jar
  do
    AMICLASSPATH=$jar${AMICLASSPATH:+:$AMICLASSPATH}
  done

  export CLASSPATH=$AMI_HOME/classes:$AMICLASSPATH${CLASSPATH:+:$CLASSPATH}

  ###########################################################################

  if [[ -f $AMI_HOME/log/AMITaskServer.out ]]
  then
    mv $AMI_HOME/log/AMITaskServer.out $AMI_HOME/log/AMITaskServer.$(date +%Y-%m-%d_%Hh%Mm%Ss).out
  fi

  ###########################################################################

  $JAVA_HOME/bin/java -Xms$JAVA_MIN_RAM -Xmx$JAVA_MAX_RAM -Djsse.enableSNIExtension=false -Dami.home=$AMI_HOME -Dami.conffile=$AMI_HOME/AMI.xml net.hep.ami.task.MainServer &> $AMI_HOME/log/AMITaskServer.out &

  ###########################################################################

  $AMI_HOME/watchDog.sh &

  ###########################################################################
fi

#############################################################################
