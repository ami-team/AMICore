#!/bin/sh

#############################################################################

AMI_HOME=$(cd $(dirname ${BASH_SOURCE[0]}) && pwd)

echo $AMI_HOME

#############################################################################

export JAVA_HOME=/usr/local/java

#############################################################################

AMICLASSPATH=''

for jar in $AMI_HOME/lib/*.jar
do
    AMICLASSPATH=$jar${AMICLASSPATH:+:$AMICLASSPATH}
done

export CLASSPATH=$AMI_HOME/classes:$AMICLASSPATH${CLASSPATH:+:$CLASSPATH}

#############################################################################

if [[ -f $AMI_HOME/AMITaskServer.log ]]
then
    mv $AMI_HOME/AMITaskServer.log $AMI_HOME/AMITaskServer.log.`date +%Y-%m-%d_%Hh%Mm%Ss`
fi

#############################################################################

$JAVA_HOME/bin/java -Xms1G -Xmx1G -Djsse.enableSNIExtension=false -Dconfigfile=$AMI_HOME/AMI.conf net.hep.ami.task.MainServer > $AMI_HOME/AMITaskServer.log &

#############################################################################
