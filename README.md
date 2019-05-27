# Zendesk Search Application

This simple console application is used to run search on the json data provided

## Getting Started

These instructions will get you a copy of the project built and ready to be launched in your local for validation.

### Prerequisites

You need the following software to be able to run the application successfully and how to install them
* Make sure java 8 is installed. If required, refer to the guide https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html
<br /> once java is installed, checking the version on the command line gives output similar to
```
zendesksearch $java -version
java version "1.8.0_91"
Java(TM) SE Runtime Environment (build 1.8.0_91-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.91-b14, mixed mode)
```
* Make sure maven 3 is installed. If required, refer to the guide https://maven.apache.org/install.html
<br /> once maven is installed, checking the version on the command line gives output similar to
```
zendesksearch $mvn --version
Apache Maven 3.6.1 (d66c9c0b3152b2e69ee9bac180bb8fcc8e6af555; 2019-04-05T00:30:29+05:30)
Maven home: /Users/phanindra/Downloads/apache-maven-3.6.1
Java version: 1.8.0_91, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.14.4", arch: "x86_64", family: "mac"

```

### Building

steps to get this project built.

* Download the project from github.
* Extract the downloaded zip file. Now you'll have a folder named **zendesksearch**
* Then compile, run tests and create package(build artifact) using the commands below
```
cd zendesksearch
mvn clean package
```
* That would have produced a executable jar named **zendesksearch/target/zendesksearch-1.0-SNAPSHOT.jar**

## Running the application

Simply run the below command to launch application to do some searching!
```
zendesksearch $java -jar target/zendesksearch-1.0-SNAPSHOT.jar

```
## (Optional)Testing with different data

By default, you dont need to supply any data. When the application is running, it gets data from the directory **zendesksearch/target/data**. 
In order to try testing the application with a different set of data ( i.e organizations.json, tickets.json, users.json)
You can edit those files under **zendesksearch/target/data** and simply run again.

Alternatively, If you have the files ready in a directory on your computer and you'd like the application to use json files from that
directory, then please follow below steps

* Edit *zendesksearch/src/main/resources/application.properties*. 
* Uncomment the line with key ```json.data.path``` and change the value of that key to the path of directory which contains json data
<br/> Note: The directory is assumed to contain files with names organizations.json, tickets.json, users.json
* Build the project again
```
mvn clean package
```
* Run again to test with new data


## Authors

* **Phaneendra Nakkala**


