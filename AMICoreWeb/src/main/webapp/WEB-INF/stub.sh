#!/bin/bash

#JAVA_HOME=/opt/java/jdk-12.x.x

########################################################################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink $THIS_SCRIPT) ]]
do
  THIS_SCRIPT=$(readlink $THIS_SCRIPT)
done

AMI_HOME=$(cd $(dirname $THIS_SCRIPT) && pwd)

########################################################################################################################

AMICLASSPATH=$AMI_HOME/classes

for jar in $AMI_HOME/lib/*.jar
do
  AMICLASSPATH=$AMICLASSPATH:$jar
done

########################################################################################################################
(

  cd $AMI_HOME

  $JAVA_HOME/bin/java -Dfile.encoding=UTF-8 -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -classpath $AMICLASSPATH net.hep.ami.AMICoreTest $@

  if [[ $? == "0" ]]
  then
    echo '[Okay]'
  else
    echo '[Error]'
  fi

)

########################################################################################################################
