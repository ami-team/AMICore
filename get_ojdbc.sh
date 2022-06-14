#!/bin/bash

DRIVER=ojdbc10

curl -L -o $DRIVER.jar https://download.oracle.com/otn-pub/otn_software/jdbc/1914/ojdbc10.jar

mvn install:install-file \
	-DgeneratePom=true \
	-DgroupId=com.oracle.jdbc -DartifactId=$DRIVER \
	-Dpackaging=jar -Dfile=$DRIVER.jar -Dversion=$(unzip -p $DRIVER.jar META-INF/MANIFEST.MF | awk -F ':' '/Implementation-Version/ { gsub(/[^0-9\.]/, "", $2); print $2; }')

rm -f $DRIVER.jar
