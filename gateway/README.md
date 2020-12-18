## How does it work?
The gateway microservice is the entry point of the application. Users make requests to it, and it decides where to forward these requests.

We used the [Netflix Zuul](https://github.com/Netflix/zuul) library to implement the gateway.

You can read more about it from [this](https://spring.io/guides/gs/routing-and-filtering/) tutorial by Spring. 

You can check the `gateway/src/main/resources/application.properties` file to see the URL paths for each microservice. For example the requests microservice has `/requests/something` path:
```
https://example.com/requests/getHouse/4
```
Check the Controller classes of each microservice for all HTTP method URL mappings.

In the `application.properties` you can also see and change information like the port and name of each microsevice (they all run on localhost).

## What still needs to be done?
We should probably change the port of the gateway to something like 8080, as it is more conventional for a REST service.