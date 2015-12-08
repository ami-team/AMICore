#!/bin/sh

#############################################################################

THIS_SCRIPT=${BASH_SOURCE[0]:-$0}

while [[ -n $(readlink $THIS_SCRIPT) ]]
do
  THIS_SCRIPT=$(readlink $THIS_SCRIPT)
done

AMI_HOME=$(cd $(dirname $THIS_SCRIPT) && pwd)

#############################################################################

$AMI_HOME/stopAMITaskServer.sh
sleep 2
$AMI_HOME/startAMITaskServer.sh

#############################################################################
