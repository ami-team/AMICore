#!/bin/sh

#############################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink $THIS_SCRIPT) ]]
do
  THIS_SCRIPT=$(readlink $THIS_SCRIPT)
done

AMI_HOME=$(cd $(dirname $THIS_SCRIPT) && pwd)

#############################################################################

PID=$(cat $AMI_HOME/wdogFile 2> /dev/null)

if [[ $? -eq 0 ]]
then
  XXXXXXXXXXXXXXX=$(ps -p $PID 2> /dev/null)

  if [[ $? -eq 0 ]]
  then
    echo $XXXXXXXXXXXXXXX

    exit 0
  fi
fi

#############################################################################

echo $$ > $AMI_HOME/wdogFile

if [[ $? -ne 0 ]]
then
  exit 1
fi

#############################################################################

while true
do
  sleep 10

  if [[ -z $(find $AMI_HOME/wdogFile -cmin -10 2> /dev/null) ]]
  then
    echo 'Restart task server'

    $AMI_HOME/restartAMITaskServer.sh
  fi
done

#############################################################################
