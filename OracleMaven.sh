#!/bin/bash

JAR=ojdbc8.jar
JAR_VERSION=12.2.0.1

mvn install:install-file \
	-DgeneratePom=true \
	-DgroupId=com.oracle.jdbc -DartifactId=ojdbc8 \
	-Dpackaging=jar -Dfile=$JAR -Dversion=$JAR_VERSION
