all: stop clean build deploy start
	echo "done"

build:
	mvn --update-snapshots install

deploy:
	rm -fr tomcat/logs/*
	rm -fr tomcat/webapps/*

	cp target/AMICore.war tomcat/webapps/AMI.war

start:
	./tomcat/bin/startup.sh

stop:
	./tomcat/bin/shutdown.sh

clean:
	mvn clean
