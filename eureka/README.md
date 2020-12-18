## How does it work?
This is a load-balancer. We used the Netflix library [Eureka](https://github.com/Netflix/eureka).

We use it as a microservice discovery and registration, because we don't require a load balancer at the moment.
However, it would be easier to scale if we ever need the functionality in the future.