# database-metadata-main
## build
````bash
$ mvn -Ddriver.groupId= -Ddrier.artifactId= -Ddriver.version= clean package
````
Some profiles are prepared for various jdbc drivers.
````bash
$ mvn help:all-profiles
$ mvn -Pmariadb-java-client clean package
$ mvn -Pmysql-connector-java -Ddriver.version=5.1.36 clean package
````
## run
Executes the final jar with following arguments.
  1. url
  2. username
  3. password
  4. filename
  5. suppression*
```bash
$ java -jar databse-metadata-main-....jar <url> <user> <pass> <outfile> suppression1 suppression2
```
## examples
### derby
```bash
$ mvn -Pderby clean package
$ java -jar target/...-derby-....jar "jdbc:derby:memory:test;create=true" "" "" \
> target/derby.metadata.xml
...
$ head target/derby.metadata.xml 
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<metadata xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns="http://github.com/jinahya/sql/database/metadata/bind">
    <allProceduresAreCallable>true</allProceduresAreCallable>
    <allTablesAreSelectable>true</allTablesAreSelectable>
    <autoCommitFailureClosesAllResultSets>true</autoCommitFailureClosesAllResultSets>
    <catalog>
        <tableCat></tableCat>
        <schema>
            <tableSchem>APP</tableSchem>
        </schema>
$
```
### h2
```bash
$ mvn -Ph2 clean package
$ java -jar target/...-h2-....jar jdbc:h2:mem:test "" "" target/h2.metadata.xml \
> schema/functions \
> column/isGeneratedcolumn \
> table/pseudoColumns \
> metadata/generatedKeyAlwaysReturned
...
$ head target/h2.metadata.xml 
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<metadata xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns="http://github.com/jinahya/sql/database/metadata/bind">
    <allProceduresAreCallable>true</allProceduresAreCallable>
    <allTablesAreSelectable>true</allTablesAreSelectable>
    <autoCommitFailureClosesAllResultSets>false</autoCommitFailureClosesAllResultSets>
    <catalog>
        <tableCat>TEST</tableCat>
        <schema tableCatalog="TEST">
            <tableSchem>INFORMATION_SCHEMA</tableSchem>
            <table tableCat="TEST" tableSchem="INFORMATION_SCHEMA">
$ 
```
### hsqldb
```bash
$ mvn -Phsqldb clean package
$ java -jar target/...-hsqldb-....jar jdbc:hsqldb:mem:test "" "" target/hsqldb.metadata.xml \
> table/pseudoColumns
...
$ head target/hsqldb.metadata.xml 
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<metadata xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://github.com/jinahya/sql/database/metadata/bind">
    <allProceduresAreCallable>true</allProceduresAreCallable>
    <allTablesAreSelectable>true</allTablesAreSelectable>
    <autoCommitFailureClosesAllResultSets>false</autoCommitFailureClosesAllResultSets>
    <catalog>
        <tableCat>PUBLIC</tableCat>
        <schema tableCatalog="PUBLIC">
            <tableSchem>INFORMATION_SCHEMA</tableSchem>
            <table tableCat="PUBLIC" tableSchem="INFORMATION_SCHEMA">
$ 
```
