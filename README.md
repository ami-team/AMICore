AMI
===

The [ATLAS Metadata Interface (AMI)](http://www.cern.ch/ami/) is a generic high level framework for metadata cataloguing. It was originally developed for the [A Toroidal LHC ApparatuS (ATLAS)](http://home.web.cern.ch/about/experiments/atlas) experiment, one of the two general-purpose detectors at the [Large Hadron Collider (LHC)](http://home.web.cern.ch/about/accelerators/large-hadron-collider).

Compiling AMI
=============

 * Requirements

Make sure that [Java 8](http://www.oracle.com/technetwork/java/javase/) and [Maven 3](http://maven.apache.org/) are installed:
```bash
java -version
mvn -version
```

 * Installing Orache JDBC driver 12c

Download [Orache JDBC driver 12c](http://www.oracle.com/technetwork/database/features/jdbc/). Edit OracleMaven.sh and set the proper JAR version:
```bash
JAR=ojdbc8.jar
JAR_VERSION=12.2.0.1

mvn install:install-file \
        -DgeneratePom=true \
        -DgroupId=com.oracle -DartifactId=ojdbc8 \
        -Dpackaging=jar -Dfile=$JAR -Dversion=$JAR_VERSION
```

Run OracheMaven.sh:
```bash
./OracheMaven.sh
```

 * Compiling sources

```bash
mvn install
```
