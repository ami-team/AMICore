#!/bin/bash

JAR=ojdbc7.jar
VERSION=12.1.0.1.0

mvn install:install-file \
	-DgeneratePom=true \
	-DgroupId=com.oracle -DartifactId=ojdbc7 \
	-Dpackaging=jar -Dfile=$JAR -Dversion=$VERSION
