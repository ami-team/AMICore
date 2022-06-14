[![][Build Status img]][Build Status]
[![][License img]][License]

<a href="http://lpsc.in2p3.fr/" target="_blank">
	<img src="http://ami.in2p3.fr/docs/images/logo_lpsc.png" alt="LPSC" height="72" />
</a>
&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://www.in2p3.fr/" target="_blank">
	<img src="http://ami.in2p3.fr/docs/images/logo_in2p3.png" alt="IN2P3" height="72" />
</a>
&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://www.univ-grenoble-alpes.fr/" target="_blank">
	<img src="http://ami.in2p3.fr/docs/images/logo_uga.png" alt="UGA" height="72" />
</a>
&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://home.cern/" target="_blank">
	<img src="http://ami.in2p3.fr/docs/images/logo_atlas.png" alt="CERN" height="72" />
</a>
&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://atlas.cern/" target="_blank">
	<img src="http://ami.in2p3.fr/docs/images/logo_cern.png" alt="CERN" height="72" />
</a>

AMI
===

The [ATLAS Metadata Interface (AMI)](http://www.cern.ch/ami/) is a generic high level framework for metadata cataloguing. It was originally developed for the [A Toroidal LHC ApparatuS (ATLAS)](http://home.web.cern.ch/about/experiments/atlas) experiment, one of the two general-purpose detectors at the [Large Hadron Collider (LHC)](http://home.web.cern.ch/about/accelerators/large-hadron-collider).

Compiling AMI
=============

 * Requirements

Make sure that [Java 11](http://www.oracle.com/technetwork/java/javase/) and [Maven 3](http://maven.apache.org/) are installed:
```bash
java -version
mvn -version
```

 * Installing Oracle JDBC driver 12c

Download [Oracle JDBC driver 12c](http://www.oracle.com/technetwork/database/features/jdbc/). Edit `get_ojdbc.sh` and set the proper JAR version:
```bash
JAR=ojdbc8.jar
JAR_VERSION=12.2.0.1

mvn install:install-file \
        -DgeneratePom=true \
        -DgroupId=com.oracle -DartifactId=ojdbc8 \
        -Dpackaging=jar -Dfile=$JAR -Dversion=$JAR_VERSION
```

Run `get_ojdbc.sh`:
```bash
./get_ojdbc.sh
```

 * Compiling sources

```bash
mvn package
```

Developers
==========

* [Jérôme ODIER](https://annuaire.in2p3.fr/4121-4467/jerome-odier) ([CNRS/LPSC](http://lpsc.in2p3.fr/))
* [Jérôme FULACHIER](https://annuaire.in2p3.fr/2061-2240/jerome-fulachier) ([CNRS/LPSC](http://lpsc.in2p3.fr/))
* [Fabian LAMBERT](https://annuaire.in2p3.fr/3087-3350/fabian-lambert) ([CNRS/LPSC](http://lpsc.in2p3.fr/))

[Build Status]:https://gitlab.in2p3.fr/ami-team/AMICore/-/commits/master
[Build Status img]:https://gitlab.in2p3.fr/ami-team/AMICore/badges/master/pipeline.svg

[License]:http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.txt
[License img]:https://img.shields.io/badge/license-CeCILL-blue.svg
