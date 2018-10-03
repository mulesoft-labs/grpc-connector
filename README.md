# Dynamic Logger Controller

Dynamic logger controller provides an interface to update logger level for given application dynamically without redeploying application or making any edits on the server in log4j.xml

This project contains two components:

* An API which utilizes connector to fetch and update logging level for all the apps in the JVM
* A connector for MuleESB, which connects with JMX and update logger level using MBeans

# Repository Structure
This repository consists on the following folders:

* `dynamic-logger-connector` Connector which connects to log4j MBeans
* `dynamic-logger-service-api` API specifications to fetch all apps on the JVM where this API is deployed

Please read the individual README files found on each folder to get more details on how to use each artifact.

To access web-admin, please visit http://localhost:8081/admin/


![](https://github.com/mulesoft-consulting/dynamic-logger-controller/blob/master/dynamic-logger-service-api/change-log-level.gif)
