
### Standalone JAR

    $ java -jar ApkInfoExtractor-1.0-standalone.jar

Standalone jar pack entire application files into one big **JAR**, included 3rd-party libraries (apps-core, db4o, poi),
aapt tool for three platform(`windows, linux, unix`).

It purpose is let you transfer application easier, only need send one file to other persons,
application could run with errorless, will not get crash because of omitting one dependency file or more.

### Pack tar.gz

    $ tar vfx ApkInfoExtractor-1.0.tar.gz
    $ java -jar ApkInfoExtractor-1.0-jfx.jar

Pack tar.gz contained app jar(`ApkInfoExtractor-1.0-jfx.jar`) and dependencies libraries directory(`lib`) next to it,
offered another choice relative to **Standalone JAR**, people don't want an integrated JAR should download that file.

## Tested Environments

ApkInfoExtractor required [JDK1.7+](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html),
below are tested environments detail :

> Operator System : Mac OS X-10.8.2 <br/> JDK : 1.7u25

> Operator System : Windows 7 32bit <br/> JDK : 1.7u60

> Operator System : Ubuntu-12.04 64bit <br/> JDK : 1.7u60
