sample-jpa-osgi
===============
1. Install Apache Karaf

2. Start it ($KARAF_HOME/bin/karaf)

3. Copy the following jars into $KARAF_HOME/deploy
    log4j-1.2.17.jar
    jdo-api-3.0.1.jar
    geronimo-jpa_2.0_spec-1.1.jar
    com.springsource.com.mysql.jdbc-5.1.6.jar

    datanucleus-core-{version}.jar
    datanucleus-api-jpa-{version}.jar
    datanucleus-rdbms-{version}.jar
    datanucleus-samples-osgi-jpa-1.0.jar

    These will then be automatically installed/started

4. If you do a "list" in the karaf console you will now get something like
karaf@root> list
START LEVEL 100 , List Threshold: 50
   ID   State         Blueprint      Level  Name
[  50] [Active     ] [            ] [   80] Java Data Objects (3.0.1)
[  51] [Active     ] [            ] [   80] Apache Log4j (1.2.17)
[  52] [Active     ] [            ] [   80] Apache Geronimo JSR-317 JPA 2.0 Spec API (1.1)
[  53] [Active     ] [            ] [   80] MySQL AB's JDBC Driver for MySQL (5.1.6)
[  54] [Active     ] [            ] [   80] DataNucleus Core ({version})
[  55] [Active     ] [Created     ] [   80] DataNucleus JPA API ({version})
[  56] [Active     ] [            ] [   80] DataNucleus RDBMS ({version})
[  57] [Active     ] [            ] [   80] datanucleus-samples-osgi-jpa (1.0)

5. Make sure you have "spring-dm" enabled in Karaf. For example
   karaf> features:install spring-dm

   This will then start up the module in the DN sample


