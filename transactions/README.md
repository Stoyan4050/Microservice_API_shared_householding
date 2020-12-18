## Description

This is the home folder for the Transactions service of our application.

## How to run it?

You can just run the transaction microservice separately from the other ones. However, this microservice relies a lot on
the Request microservice, in order to perform some basic operations.

## How does it work?

The main idea of the Transactions microservice is to control the flow of the products, credits and portions consumed by
different users. The Transactions microservice has established documentation with the Requests microservice. This way
when a user adds or gets a product from the fridge, his credits will be automatically increased or decreased. Another
feature related to the communication between these two microservices is the splitting of the cost of a transaction (
getting food from the fridge), among different users in a house, when eating together. This way every user will have
even amount of credits subtracted about the food they share. Also, when a user reports product as expired in price for
the left portions will be automatically subtracted from all of the members of the house. Finally, there is a method that
returns the list of products per house, so that a user can see the products only for his house. All of these methods
rely solely on the communication between the two microservices. For more information about all the methods you can check
the two controller classes.

## Notes

The input to all the methods should be correct. This means that you should not leave any field in the entities blank and
to pass to the methods proper objects. The inputs of the users should always be checked. Before making any transaction
or adding/deleting/editing products it should be checked if the user and the product belong to the same house. A user
should not be able to get products from different houses. A product is only associated with a user, so when a user
leaves a house his products will not be anymore in the fridge of the house. The method for deleting transaction and
product will not update the credits of the users when the transaction is deleted. It is just there to delete old
transactions. However, the method for editing products will update the portions left of the product connected to the
transactions and will change the credits to the user. In the project in many places we have used the @Username
annotation. Its purpose is to get directly the username of the user that is currently logged in.

## What still needs to be done?

Because of shortage of time we were not able to implement some functionalities. The main things that still need to be
implemented are some checks in the methods that will allow a user to get products only available in his house and that a
user without a house should not be able to get or add any products. Also checks for other corner cases will be developed
with future optimizations. The last thing is to always get automatically the username of the user making request using
the @Username annotation.