#!/bin/bash
#
# ApkInfoExtractor Packager execution script for Linux/Solaris/OS X.
# From : https://github.com/vince-styling/ApkInfoExtractor
# Copyright (C) 2014 Vince Styling
#
# Requiremt :
# Maven 3.0.5+
# JDK 1.7 u10+
#

#
# Preparing all things for packager :
# > determine all required command exists.
# > install dependencies jars into the local maven repository.
#
preparing () {
    if !(command_exists java) ; then
        echo "Could't find java(jdk) command."
        exit
    elif !(command_exists mvn) ; then
        echo "Could't find maven(mvn) command."
        exit
    elif !(command_exists tar) ; then
        echo "Could't find tar(bsdtar) command."
        exit
    fi

    # Install the db4o into maven repository
    mvn install:install-file -Dfile=libs/db4o-8.0.249.16098-core-java5.jar -DgroupId=com.db4o -DartifactId=db4o-core -Dversion=8.0.249 -Dpackaging=jar -DgeneratePom=true

    # Install the apache-poi into maven repository
    mvn install:install-file -Dfile=libs/poi-3.10-FINAL-20140208.jar -DgroupId=org.apache.poi -DartifactId=poi -Dversion=3.10-FINAL-20140208 -Dpackaging=jar -DgeneratePom=true
}

#
# Check if command exists.
#
command_exists () {
    type "$1" &> /dev/null
}


################ Main Program ###################

scriptName="packager.sh"

appName="ApkInfoExtractor"
appVersionName="1.0"

if [[ "$1" == "prepare" ]]; then
    preparing

elif [[ "$1" == "run" ]]; then
    preparing

    mvn clean jfx:run

elif [[ "$1" == "pack" ]]; then
    preparing

    mvn clean jfx:jar
    
    cd target/jfx/app
    
    tarName="$appName-$appVersionName.tar.gz"
    tar zcf $tarName *
    mv $tarName ../../../
    
    echo "

    build successful, the final release file \"$tarName\" that packed dependencies libraries and app jar has been here,
    you can untar and enter it, to be run the program, simply run \"java -jar $appName-$appVersionName.jar\" in the terminal
    or direct double-click the main jar file."

elif [[ "$1" == "standalone" ]]; then
    preparing
    
    mvn clean jfx:jar

    # Install the app jar into the local repository
    mvn install:install-file -Dfile=target/jfx/app/$appName-$appVersionName-jfx.jar -DgroupId=com.vincestyling.apps -DartifactId=$appName -Dversion=$appVersionName -Dpackaging=jar -DgeneratePom=true
    
    cd standalone
    mvn clean compile assembly:single
    
    cp target/$appName-$appVersionName-standalone-jar-with-dependencies.jar ../$appName-$appVersionName-standalone.jar
    
    echo "

    build successful, the final jar \"$appName-$appVersionName-standalone.jar\" has been here,
    you could distribute it in any platform and anywhere, just simply execute the $appName-$appVersionName-standalone.jar,
    all the other thing will be away from you, enjoy this^_^."


elif [[ "$1" == "native" ]]; then
    preparing

    mvn clean jfx:native
    
    echo "

    build successful, the final native bundle in the \"project.basedir/target/jfx/native/bundles/\" directory."

else

echo "Usage: $scriptName options{prepare|run|pack|standalone|native}
for example :
------------
  $scriptName prepare
    install the db4o and apache-poi into the local maven repository so the project able to compile with right dependencies.

  $scriptName run
    build the project and run it to experience immediately.

  $scriptName pack
    build a app jar and copy all dependencies into lib directory next to it, finally release a tar.gz file that contained the app jar and the lib directory.

  $scriptName standalone
    build a standalone jar that packed entire application files(included dependencies libraries and apps jar) into one big JAR.

  $scriptName native
    generate a native bundle for the platform we are building on, when you execute this in MacOS you will get a .dmg native bundle, other like msi, exe, rpg, etc. which depended what os you upon."

fi






















