#############################################################################

all: stop install deploy start
	echo "done"

#############################################################################

compile:
	mvn compile

#############################################################################

test:
	mvn test

#############################################################################

install:
	mvn install -U

#############################################################################

update-versions:
	mvn release:update-versions

#############################################################################

deploy:
	rm -fr ~/workspace/tomcat/webapps/AMI/

	unzip AMICoreWeb/target/AMICoreWeb-*.war -d ~/workspace/tomcat/webapps/AMI/

#############################################################################

stop:
	~/workspace/tomcat/bin/catalina.sh stop &> /dev/null

	rm -fr ~/workspace/tomcat/logs/*

#############################################################################

start:
	rm -fr ~/workspace/tomcat/logs/*

	~/workspace/tomcat/bin/catalina.sh start

#############################################################################

restart:
	~/workspace/tomcat/bin/catalina.sh stop &> /dev/null

	rm -fr ~/workspace/tomcat/logs/* ; sleep 2

	~/workspace/tomcat/bin/catalina.sh start

#############################################################################

clean:
	mvn clean

#############################################################################
