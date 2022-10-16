# thesisWork
This is my master degree's thesis and it's a Data Stream Processing real-time application that computes market stream data in order to monitor stock prices to promptly find out whether selling or buying them. It is based on ACM DEBS Grand Challenge 2022 (https://2022.debs.org/call-for-grand-challenge-solutions/). Apache Flink and Kafka were used as Big Data tools to develop this solution.
## Technologies
The proposed solution is based on the following frameworks and tools:
* Java version: openjdk 11.0.14.1
* Gradle version: 7.4
* Docker: 20.10.12
* Docker-compose: 1.27.4
* Kafka: 5.3.0
* Zookeeper: 3.8.0
* Apache Flink 
	
## Setup
To run this project, you need to have previously installed Java and Gradle on your machine. 
Solution can be installed locally using:

```
$ git clone "this repo's url"
$ cd docker/
$ sudo docker-compose up
$ cd .. 
```
Make sure you set your Kafka container's IP address in the "config.properties" file you can find in the .\solution folder.
In order to execute this software, you now need to open two new separate terminal shells to run the two applications in parallel. Type the next command in the former, and the next one in the latter: 
```
$ gradle producer'
$ gradle run'
```
 

To stop the docker container, you can type:
```
$ cd docker/
$ sudo ./stop.sh
```
