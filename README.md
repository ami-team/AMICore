AMI
===

The [ATLAS Metadata Interface (AMI)](http://www.cern.ch/ami/) is a generic high level framework for metadata cataloguing. It was originally developed for the [ATLAS experiment](http://atlas.ch/), one of the two general-purpose detectors at the Large Hadron Collider (LHC).

Compiling AMI
=============

1. Requierments

  Make sure that [Apache Maven 3](http://maven.apache.org/) is installed:
	```
mvn --version
```

2. Installing Orache JDBC driver 12c

  Download [Orache JDBC driver 12c](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html). Edit OracleMaven.sh and set the proper JAR version:
	```
#!/bin/bash

JAR=ojdbc7.jar
JAR_VERSION=12.1.0.1.0

mvn install:install-file \
        -DgeneratePom=true \
        -DgroupId=com.oracle -DartifactId=ojdbc7 \
        -Dpackaging=jar -Dfile=$JAR -Dversion=$JAR_VERSION
```

  Run OracheMaven.sh:
	```
./OracheMaven.sh
```

3. Compiling AMI
	```
mvn install
```
