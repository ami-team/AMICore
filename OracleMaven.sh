#!/bin/bash

DRIVER=ojdbc8

curl -o $DRIVER.jar http://ami.in2p3.fr/B681507E_E232_5951_BBBB_0DAC2BAEC223

mvn install:install-file \
	-DgeneratePom=true \
	-DgroupId=com.oracle.jdbc -DartifactId=$DRIVER \
	-Dpackaging=jar -Dfile=$DRIVER.jar -Dversion=$(unzip -p $DRIVER.jar META-INF/MANIFEST.MF | awk -F ':' '/Implementation-Version/ { gsub(/[^0-9\.]/, "", $2); print $2; }')
