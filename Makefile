all: stop install deploy start
	echo "done"

compile:
	mvn compile

install:
	mvn install -U

assembly:
	mvn -f AMITaskServer/pom.xml assembly:assembly

update-versions:
	mvn release:update-versions

deploy:
	rm -fr tomcat/logs/*
	rm -fr tomcat/webapps/*

	cp AMIWebCore/target/AMIWebCore-*.war tomcat/webapps/AMI.war
#	cp ../QR/target/QRCode.war tomcat/webapps/QRCode.war

stop:
	./tomcat/bin/shutdown.sh &> /dev/null

start:
	./tomcat/bin/startup.sh

restart:
	./tomcat/bin/shutdown.sh &> /dev/null
	sleep 2
	./tomcat/bin/startup.sh

clean:
	mvn clean
