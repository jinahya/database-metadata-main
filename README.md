# database-metadata-main
## build
````
mvn -Ddriver.groupId= -Ddrier.artifactId= -Ddriver.version= clean package
````
Some profiles are prepared for various jdbc drivers.
````
mvn help:all-profiles
mvn -Pmariadb-java-client clean package
mvn -Pmysql-connector-java -Ddriver.version=5.1.36 clean package
````
## run
Executes the final jar with following arguments.
  1. url
  2. username
  3. password
  4. filename
  5. suppression*
```
java -jar databse-metadata-main-....jar jdbc/mysql://... user pass out.xml table/tablePrivileges
```
