all: stop clean build deploy start
	echo "done"

build:
	mvn --update-snapshots install

deploy:
	rm -fr tomcat/logs/*
	rm -fr tomcat/webapps/*

	cp target/AMICore.war tomcat/webapps/AMI.war

stop:
	./tomcat/bin/shutdown.sh

start:
	./tomcat/bin/startup.sh

restart:
	./tomcat/bin/shutdown.sh
	sleep 2
	./tomcat/bin/startup.sh

clean:
	mvn clean
