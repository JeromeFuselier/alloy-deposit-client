#!/bin/sh

mvn install:install-file -DgroupId=net.sf.nachocalendar -DartifactId=nachocalendar -Dversion=0.23 -Dpackaging=jar -Dfile=nachocalendar-0.23.jar
mvn install:install-file -DgroupId=com.vlsolutions.swing -DartifactId=docking -Dversion=2.1.4 -Dpackaging=jar -Dfile=vldocking-2.1.4.jar
mvn install:install-file -DgroupId=org.springframework.richclient -DartifactId=spring-richclient-vldocking -Dversion=1.1.0 -Dpackaging=jar -Dfile=spring-richclient-vldocking-1.1.0.jar
