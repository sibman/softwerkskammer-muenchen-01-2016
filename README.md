# Exercise for meeting of the Softwerkskammer München, January 2016

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
single item.

This repository defines the REST interfaces and configures an embedded
container. The task of the exercise is to implement the domain model and
business logic as described above.

