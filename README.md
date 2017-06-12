# TransformX-Service-UCD-ConvertTemplate

This project defines the code for generating UCD XML from different Templates

This service runs on port: 9016

To run the server, enter into project folder and run

mvn spring-boot:run (or) java -jar *location of the jar file*

The above line will start the server at port 9016

If you want to change the port .Please start the server as mentioned below 

syntax : java -jar *location of the jar file* --server.port= *server port number*
 
example: java -jar target/ConvertFromTemplate.jar --server.port=9090

API to generate MISMO XML and ERRORS(actualize/transformx/transforms/{version}/templatetoucd) with input as text format

syntax : *server address with port*/actualize/transformx/transforms/v1/templatetoucd; method :POST; Header: Content-Type:text/plain

API to generate MISMO XML and ERRORS(actualize/transformx/transforms/{version}/csvtoxml) with input as csv 

syntax : *server address with port*/actualize/transformx/transforms/v1/csvtoxml; method: POST; Header: Content-Type:text/plain

API to check the status of service(actualize/transformx/transforms/{version}/ping); method: GET;  

example: http://localhost:9016/actualize/transformx/transforms/templatetoucd ; method: POST; Header: Content-Type:text/plain
