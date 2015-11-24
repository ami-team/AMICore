#!/bin/sh

#############################################################################

ROOTDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

#############################################################################

start() {
    sleep 2
    $ROOTDIR/startAMITaskServer.sh
}

stop() {
    sleep 2
    $ROOTDIR/stopAMITaskServer.sh
}

restart() {
    stop
    start
}

#############################################################################

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  reload|restart|condrestart)
    restart
    ;;
  *)
    echo "Usage: $0 {start|stop|reload|restart|condrestart}"
    exit 1
esac

#############################################################################

exit $?

#############################################################################
