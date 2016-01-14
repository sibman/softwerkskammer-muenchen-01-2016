# Exercise for meeting of the Softwerkskammer München, January 2016

Slide deck is at [https://docs.google.com/presentation/d/1kUnjD8UEq9w4ade5wWxoHqfBnv1VxVwZhvQ5MDRn9H4]

This is an exercise on the use of CQRS and Event Sourcing for the meeting of
the Softwerkskammer München in January 2016.

The exercise is to implement the backend of a shopping list for personal
use. Clients communicate with the backend via a RESTful interface. The backend
manages and persists the shopping list.

The backend exposes the following commands:
 * Add an item to the list.
 * Mark an item on the list as complete.
 * Clear the list.
 * Undo the previous action.

Furthermore, the backend exposes the following queries:
 * Read the current state of the list.
 * Subscribe to the list, so that a client is informed as soon as any change
   to the list occurs.

Each item consists of an article, a unit, and an amount. If two items with the
same article and unit are added to the list, they should be aggregated to a
single item. Units include 'gram', 'millilitre', 'package', and so on.

This repository defines the REST interfaces and configures an embedded
container. The endpoints are configured in the subproject `services` and their
implementations are currently stubs. The task of the exercise is to implement
the domain model and business logic as described above.

## Starting points:

 * `ShoppingListService` in
   `services/src/shoppinglist/services/ShoppingListService.java`
 * `ShoppingListClientNotifier` in
   `services/src/shoppinglist/services/ShoppingListClientNotifier.java`

## Some things to consider

 * What domain objects do you need?
 * How could you imagine a client, such as a web frontend or an android app,
   processing the event notifications sent by the server?
 * How will persistence work? For now, don't worry about setting up a real
   persistence mechanism. Think about what the business logic needs from the
   persistence layer and create one or more appropriate Repository interfaces.
 * How will you implement undo? In particular, what happens if undo is invoked
   multiple times in sequence?

Don't worry about implementing a real persistence layer with a database and
whatnot -- that goes beyond the scope of this exercise. An in-memory solution
will do just fine for now.

## Technical notes

To compile and run this program, run

* `./gradlew build` on Linux or Mac OS, or
* `gradlew build` on Windows.

If you do this out of the box, you'll see several tests fail. This is because
there is no implementation to make them pass. If you wish to run the
application before completing the implementation, you can annotate the test
classes with @Ignore.

Once you have built the program successfully, run

* `java -jar build/shopping-list.jar` on Linux or Mac OS, or
* `java -jar build\shopping-list.jar` on Windows.

The server should start and listen to HTTP port 8080.

This program is built to run against Java 8.
