# scala-gRPC-test
Sample of gRPC using ScalaPB
Configuration file is present in src/main/resources/application.conf
Instance of typesafe Config class injected using scala-guice

### Server
Run following command and select option for AppServer 
```bash
> sbt run

Multiple main classes detected, select one to run:

 [1] AppServer
 [2] Client
[info] Packaging ...

Enter number: [info] Done packaging.
1
```

### Client
Run following command and select option for Client  
```bash
> sbt run

Multiple main classes detected, select one to run:

 [1] AppServer
 [2] Client
[info] Packaging ...

Enter number: [info] Done packaging.
2
```
Enter a name when prompted, which will be used to make call to the server
Entering "exit" as name will close the client
