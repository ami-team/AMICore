#! /bin/sh

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
  kill $PID &> /dev/null
fi

#############################################################################

touch $AMI_HOME/stopFile

#############################################################################

n=0

while [[ -n $(ps -ef | grep "net\.hep\.ami\.task\.MainServer") && $n -lt 30 ]]
do
  n=$((n+1))
  printf '.'
  sleep 1
done

#############################################################################

if [[ -n $(ps -ef | grep "net\.hep\.ami\.task\.MainServer") ]]
then
  ps -ef | grep "AMI\.Task\.MainServer" | awk '{print $2}' | xargs kill

  echo 'Task server killed'
else
  echo 'Task server stopped'
fi

#############################################################################

rm -f $AMI_HOME/wdogFile
rm -f $AMI_HOME/stopFile

#############################################################################
