# gRPC Connector

Repository that contains gRPC connector and demo app for client and server implementation using gRPC connector

# Repository Structure
This repository consists on the following folders:

* `grpc-proto` project that includes all required plugins and gRPC dependencies. It also includes .proto file which defines
client-server communication contract. This project has packaging as jar, and required in connector
* `grpc-connector` gRPC connector that uses stubs created from .proto file to communicate as server and client
* `grpc-client-demoapp` A demo MuleApp which acts as client to send messages and attributes to the gRpc server (Another MuleApp)
*`grpc-server-demoapp` A MuleApp which acts as server using gRPC connector.

Please read the individual README files found on each folder to get more details on how to use each artifact.

# Mule supported versions
Mule 3.8.2, 3.8.3, 3.8.4 

Currently this connector is not supported in any version higher than 3.8.4 due to conflict of `protobuf` 
dependencies

```ruby
To use this connector in mule-apps, we need to override certain dependencies.
Please add "loader.override=com.google.protobuf" in mule-deploy.properties in all mule 
applications
```


![](https://github.com/mulesoft-labs/grpc-connector)
