FROM java:8
ADD target/demo-0.0.1-SNAPSHOT.jar /opt/demo/demo-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","/opt/demo/demo-0.0.1-SNAPSHOT.jar"]