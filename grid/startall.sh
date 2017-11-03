#!/bin/bash

LOCATOR_PORT=10334
SERVER2_PORT=40405
HOSTNAME=localhost
PROJECT_JARS=../DomainService/target/DomainService-1.jar

gfsh <<!

start locator --name=locator --properties-file=config/locator.properties --port=$LOCATOR_PORT --J=-Xms256m --J=-Xmx256m

start server --name=server1 --locators=$HOSTNAME[$LOCATOR_PORT] --J=-Xms512m --J=-Xmx512m --cache-xml-file=config/cache.xml --properties-file=config/gemfire.properties --classpath=$PROJECT_JARS

undeploy --jar=DomainService-1.0.0.BUILD-SNAPSHOT.jar
deploy   --jar=../DomainService/target/DomainService-1.0.0.BUILD-SNAPSHOT.jar


//start server --name=server2 --locators=$HOSTNAME[$LOCATOR_PORT] --J=-Xms512m --J=-Xmx512m --cache-xml-file=config/cache.xml --properties-file=config/gemfire.properties --server-port=$SERVER2_PORT --classpath=$PROJECT_JARS

run --file=data/dept-data
run --file=data/emp-data

list members;

list regions;

exit;
!
