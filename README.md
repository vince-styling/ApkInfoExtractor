
ApkInfoExtractor
================

ApkInfoExtractor is an aapt(Android Asset Packaging Tool) wrapper, developed by JavaFX2.2,
aim to export android application file's information(e.g label, icon, package, versionCode, versionName etc.)
via straightforward and friendly graphic user interface, help you batch extracting apks information,
finally export the result as excel or xml or sql,
download and usage in this site [apkinfoextractor.vincestyling.com](http://apkinfoextractor.vincestyling.com/).

![application screenshot](http://apkinfoextractor.vincestyling.com/images/main_window.png)


Development
===========

#### Precondition

You should had below components install before you begin to compile and packaging this project :

```text
JDK 1.7+ (use to compile source code)
Maven 3.0.5+ (use to packaging application)
```

#### Import to IDE

If you are using `IntelliJ IDEA`, just import this project as maven project, in the root **pom.xml** file,
you'll see "dependency not found" errors, because we used two jars which didn't release to maven
centre repository(db4o, poi), it could be solve by install them into your local repository,
use my shell script file `${project.basedir}/packager.sh` with proper argument :

```bash
$ ./packager.sh prepare
```

#### Compile

Now, you can use maven to compile a jar :

```bash
mvn clean jfx:jar
```

If you been the first time do compile JavaFX project, above execute may report lots of error like this :

```bash
[ERROR] BaseTableController.java:[20,28] package javafx.scene.control does not exist;
and so many "symbol not find" errors;
```

that means you haven't config the JavaFX runtime environment yet,
you should copy the `jfxrt.jar` from **jre/lib** to **jre/lib/ext/**
(actually, this job could done by simply execute "mvn jfx:fix-classpath") :

```bash
$ cd ${JAVA_HOME}/jre/lib
$ cp jfxrt.jar ext/
```

#### Packaging

The packaging work was boring because you might be always run few commands over and over, I'm quite lazy for this,
therefore we put which commands into a shell script file calls `packager.sh`,
offer several options to execute different goals.

```bash
$ ./packager.sh prepare
```

Checking your compile environment(java, mvn, tar) correctness,
install the db4o and apache-poi into the maven local repository.
rest of options also execute this option first to prepare next processes.

```bash
$ ./packager.sh run
```

build the project and run it immediately.

```bash
$ ./packager.sh pack
```

build a app jar and copy all dependencies libraries into lib directory next to it,
finally release a `tar.gz` file that contained the app jar and the lib directory.

```bash
$ ./packager.sh standalone
```

Assembling the entire application into one `uber-jar` that contained all the 3rd-party libraries in one big JAR.

It **purpose** is make you transfer application easier, only need send one file to other persons,
application could run with errorless, will not get crash because of omitting one dependency file or more.

I know that **consequences** about assemble all in one JAR, jar duplicates in your classpath might be cause a lot of
problems with name clashes after use this JAR in other contexts like webapp or drop in a folder where other jars
are sitting. of course we should avoid this problem, but in case of a standalone application such as we do this
should always be single executing, will share not same classpath with other jars, so i believe this problem won't appear.

Another consequence is **Class-Loader** too slow because jar was big, we have calculated, the final standalone jar is 5.1MB,
from JVM start to the main() method spent 4s, from main() method to appearance of application windows on the screen spent 2s.
we compare to non-standalone jar, it improve only 1s, i think maybe the problem was happen in JavaFX,
but I'm not sure about this, so we offered `./packager.sh pack` as another choice.


```bash
$ ./packager.sh native
```

The `javafx-maven-plugin` can generate a native bundle for the platform we are building on,
when you execute this goal in MacOS you will take a .dmg bundle,
similarly in Ubuntu you will take a .deb bundle,
there is particularly of Windows 7, we doesn't take the .exe bundle,
we haven't solve this problem, might we should first download and install
the 3rd-party tools that the JavaFX packaging tools require (e.g. Wix, Inno, etc),
see [here](http://zenjava.com/javafx/maven/native-mojo.html) for details.

Compatibility
=============

Below are what platform we tested in :

> Operator System : Mac OS X-10.8.2 <br/> JDK : 1.7u25

> Operator System : Windows 7 32bit <br/> JDK : 1.7u60

> Operator System : Ubuntu-12.04 64bit <br/> JDK : 1.7u60

in the `${project.basedir}/src/main/resources/aapts/` directory, we packed three platforms (windows, linux, unix) aapt tool,
so you needn't worry about runtime problem, we'll choose one aapt tool in runtime that according what operator system you are executing on.


License
=======

```text
Copyright 2014 Vince Styling

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```