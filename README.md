
install db4o to local repository (just need first time)

    $ mvn clean validate

compile with all dependencies

    $ mvn compile assembly:single

the final jar generated within target, names `ApkInfoExtractor-1.0-jar-with-dependencies.jar`.

execute that jar and enjoy this Application :

    java -jar ApkInfoExtractor-1.0-jar-with-dependencies.jar
