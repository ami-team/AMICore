#!/bin/sh

#############################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink $THIS_SCRIPT) ]]
do
    THIS_SCRIPT=$(readlink $THIS_SCRIPT)
done

AMI_HOME=$(cd $(dirname $THIS_SCRIPT) && pwd)

#############################################################################

if [[ -z $(pgrep watchDog.sh) ]]
then
  while true
  do
    sleep 10

    Q1=$(find $AMI_HOME/log/AMITaskServer.out -cmin -60)
    sleep 1
    Q2=$(find $AMI_HOME/log/AMITaskServer.out -cmin -60)

    if [[ -z $Q1 && -z $Q2 ]]
    then
      echo "$(date) - restart AMI Task Server" >> /tmp/WD.out

      $AMI_HOME/restartAMITaskServer.sh &
    fi
  done
fi

#############################################################################
