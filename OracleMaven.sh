#!/bin/bash

JAR=ojdbc8.jar

curl -o $JAR ami.in2p3.fr/B681507E_E232_5951_BBBB_0DAC2BAEC223

mvn install:install-file \
	-DgeneratePom=true \
	-DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 \
	-Dpackaging=jar -Dfile=$JAR -Dversion=$(unzip -p ojdbc8.jar META-INF/MANIFEST.MF | grep 'Implementation-Version:' | awk '{print $2}')
