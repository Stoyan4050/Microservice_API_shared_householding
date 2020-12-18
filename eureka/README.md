## How does it work?
This is a load-balancer. We used the Netflix library [Eureka](https://github.com/Netflix/eureka).

We use it as a microservice discovery and registration, because we don't require a load balancer at the moment.
However, it would be easier to scale if we ever need the functionality in the future.

In order to register the microservices, each of them needs a name and a link to the Eureka microservice. Then, Eureka will discover them, and will give them links to each other.

[Here](https://spring.io/guides/gs/routing-and-filtering/) is a Spring tutorial about it.