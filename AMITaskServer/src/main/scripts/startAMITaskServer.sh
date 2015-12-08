#!/bin/sh

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
    #########################################################################

    export JAVA_HOME=/usr/local/java

    #########################################################################

    AMICLASSPATH=''

    for jar in $AMI_HOME/lib/*.jar
    do
        AMICLASSPATH=$jar${AMICLASSPATH:+:$AMICLASSPATH}
    done

    export CLASSPATH=$AMI_HOME/classes:$AMICLASSPATH${CLASSPATH:+:$CLASSPATH}

    #########################################################################

    if [[ -f $AMI_HOME/log/AMITaskServer.out ]]
    then
      mv $AMI_HOME/log/AMITaskServer.out $AMI_HOME/log/AMITaskServer.$(date +%Y-%m-%d_%Hh%Mm%Ss).out
    fi

    #########################################################################

    $JAVA_HOME/bin/java -Xms2G -Xmx2G -Djsse.enableSNIExtension=false -DAMI_HOME=$AMI_HOME -Dami.conffile=$AMI_HOME/AMI.xml net.hep.ami.task.MainServer > $AMI_HOME/log/AMITaskServer.out &

    #########################################################################
fi

#############################################################################
