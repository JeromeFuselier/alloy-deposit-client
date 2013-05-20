#!/bin/bash


java -jar bagger-2.1.3.jar http://localhost:8080 -Xms512m -classpath spring-beans-2.5.1.jar;bagger-2.1.3.jar 2>/dev/null
