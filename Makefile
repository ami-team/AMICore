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

	unzip AMIWebCore/target/AMIWebCore-*.war -d tomcat/webapps/AMI/

	cp ../AMIFinance/target/AMIFinance-*.jar tomcat/webapps/AMI/WEB-INF/lib/
	cp ~/.m2/repository/com/yahoofinance-api/YahooFinanceAPI/2.3.0/YahooFinanceAPI-2.3.0.jar tomcat/webapps/AMI/WEB-INF/lib/

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
