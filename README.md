AMI
===

The [ATLAS Metadata Interface (AMI)](http://www.cern.ch/ami/) is a generic high level framework for metadata cataloguing. It was originally developed for the [A Toroidal LHC ApparatuS (ATLAS)](http://home.web.cern.ch/about/experiments/atlas) experiment, one of the two general-purpose detectors at the [Large Hadron Collider (LHC)](http://home.web.cern.ch/about/accelerators/large-hadron-collider).

Compiling AMI
=============

1. Requierments

  Make sure that [Apache Maven 3](http://maven.apache.org/) is installed:
	```bash
mvn --version
```

2. Installing Orache JDBC driver 12c

  Download [Orache JDBC driver 12c](http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html). Edit OracleMaven.sh and set the proper JAR version:
	```bash
#!/bin/bash

JAR=ojdbc7.jar
JAR_VERSION=12.1.0.1.0

mvn install:install-file \
        -DgeneratePom=true \
        -DgroupId=com.oracle -DartifactId=ojdbc7 \
        -Dpackaging=jar -Dfile=$JAR -Dversion=$JAR_VERSION
```

  Run OracheMaven.sh:
	```bash
./OracheMaven.sh
```

3. Compiling AMI
	```bash
mvn install
```
