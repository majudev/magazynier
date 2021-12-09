#!/bin/bash
EXECUTABLE=Magazynier.jar
HOST="jdbc:mariadb://localhost:3306/magazynier"
USER="magazynier"
PASSWORD="magazynier"
MAIL_SERVER="localhost"
MAIL_PORT="25"
MAIL_USER=""
MAIL_PASSWORD=""
MAIL_TRANSPORT="SMTP"
MAIL_BASEURL="http://localhost/magazynier"
java -jar $EXECUTABLE \
	--spring.datasource.url=$HOST \
	--spring.datasource.username=$USER \
	--spring.datasource.password=$PASSWORD \
	--smtp.host=$MAIL_SERVER \
	--smtp.port=$MAIL_PORT \
	--smtp.user=$MAIL_USER \
	--smtp.password=$MAIL_PASSWORD \
	--smtp.transport=$MAIL_TRANSPORT \
	--smtp.baseurl="$MAIL_BASEURL"
