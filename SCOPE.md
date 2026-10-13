# TableNext — Scope

CS 2114 Lab 2, Deliverable 2. TableNext is a console app for running a restaurant: a waitlist, tables with capacity, seating, per-party food orders, and separate menus for hosts, servers, and customers. All storage goes through the provided generic `LinkedChain<T>`.

## Contents

- [Class Design](#class-design)
- [System Diagram](#system-diagram)
- [The Generic Contract](#the-generic-contract)
- [Data & State](#data--state)
- [Method Signatures](#method-signatures)
- [Where Validation Lives](#where-validation-lives)
- [Test Plan](#test-plan)
- [Revised Scope](#revised-scope)
- [Work Attribution](#work-attribution)
- [GenAI Reflection](#genai-reflection)

---

## Class Design

**Given:**

- `LinkedChain<T>`
- `Node<E>` (private static nested class inside `LinkedChain`)

**Making** (all public, no `extends` or `implements`):

| Class | Role |
|---|---|
| `TableNextApp` | Main class. Owns the input loop and validates all user input |
| `Restaurant` | Holds the waitlist and tables, and coordinates seating and orders |
| `Waitlist` | Wraps `LinkedChain<Party>` with position-based waitlist operations |
| `Table` | One table with a number, capacity, and optional seated party |
| `Party` | A waiting or seated group, with its own order of `MenuItem`s |
| `MenuItem` | A menu item with a name and price |
| `Output` | All console printing |
| `UserType` (enum) | `HOST`, `SERVER`, `CUSTOMER` |

---

## System Diagram

Each arrow names the field, data, or method call that connects the classes.

```mermaid
classDiagram
    direction TB

    class UserType {
        <<enumeration>>
        HOST
        SERVER
        CUSTOMER
    }
    class TableNextApp {
        Restaurant restaurant
        Output output
        Scanner input
        run()
        selectUserType()
        readInt()
        readName()
        readYesNo()
    }
    class Output {
        printMenu()
        printWaitlist()
        printTables()
        printOrder()
        printError()
    }
    class Restaurant {
        Waitlist waitlist
        LinkedChain~Table~ tables
        int nextPartyId
        addParty()
        seatNext()
        clearTable()
        addItemToTable()
        removeItemFromTable()
    }
    class Waitlist {
        LinkedChain~Party~ line
        addParty()
        insertAt()
        removeAt()
        peekNext()
        removeFirstThatFits()
        getInOrder()
    }
    class Table {
        int tableNumber
        int capacity
        Party seatedParty
        fits()
        seat()
        clear()
        isOpen()
    }
    class Party {
        int id
        String name
        int size
        boolean hasReservation
        int position
        LinkedChain~MenuItem~ order
        addItem()
        removeItem()
        getOrderTotal()
    }
    class MenuItem {
        String name
        double price
        getName()
        getPrice()
        equals()
        toString()
    }
    class LinkedChain~T~ {
        Node~T~ firstNode
        int numberOfEntries
        add()
        addToEnd()
        remove()
        get()
        contains()
        count()
        size()
        toArray()
    }
    class Node~E~ {
        <<private>>
        E data
        Node~E~ next
    }

    UserType <.. TableNextApp : selectUserType() returns UserType
    TableNextApp --> Output : field output; calls print methods
    TableNextApp --> Restaurant : field restaurant; calls restaurant methods
    Restaurant --> Waitlist : field waitlist; addParty(), removeAt()
    Restaurant --> Table : field tables; seatNext(), clearTable()
    Restaurant ..> Party : creates Party; manages its order
    Table --> Party : field seatedParty; seat() / clear()
    Waitlist --> LinkedChain : field line: LinkedChain~Party~
    Party --> MenuItem : field order: LinkedChain~MenuItem~
    LinkedChain --> Node : firstNode points to Node~T~
```

---

## The Generic Contract

**Type parameter and bound.** `public class LinkedChain<T>` has one type parameter, `T`, with no bound. `T` can be any class (`Party`, `MenuItem`, `Table`), and the chain needs nothing from it beyond what every object already has.

**What `LinkedChain<T>` promises about `T`:**

- **One type in, same type out.** Whatever type the chain is created with is the only type it accepts and returns. A `LinkedChain<Party>` takes `Party` objects and hands back `Party` objects with no casting. Adding a `MenuItem` to it is a compile error.
- **Never stores `null`.** `add` and `addToEnd` throw `IllegalArgumentException` for `null`, so every entry the chain gives back is a real `T`, and a `null` from `remove()` can only mean the chain was empty.
- **Doesn't look inside `T`**, except for `equals` (used by `remove(T)`, `contains`, and `count`) and `toString` (used by the chain's own `toString`). It never compares entries, so it does not need `Comparable`.
- **No sorting, duplicates allowed.** Entries stay in the order `add` (front) and `addToEnd` (end) put them in.

**Which operations are type-safe?**

| Operations | Type-safe? | What they rely on from `T` |
|---|---|---|
| `add(T)`, `addToEnd(T)`, `remove()`, `get(int)` | Yes. The compiler only allows `T` in and gives `T` out | Nothing, except that the entry added is not `null` |
| `remove(T)`, `contains(T)`, `count(T)` | Yes. The parameter is `T` | `T`'s `equals(Object)`. These match entries with `equals`, and the compiler cannot check that `T` overrides it. A class that does not override it only matches the exact same object |
| `toArray(T[])` | Yes, as long as the caller passes an array of the right type, such as `new Party[chain.size()]`. Java cannot create a `T[]` inside a generic class, so the caller supplies it | Nothing |
| `toString()` | Not about `T`'s type | Each entry's own `toString()` |
| `clear()`, `size()`, `isEmpty()` | Do not touch `T` at all | Nothing |

---

## Data & State

`LinkedChain<T>` is backed by a chain of nodes. Each node is an instance of the private static nested class `Node<E>`, which holds a `data` entry and a `next` reference to the following node. The chain keeps a `firstNode` reference to the head and has **no tail reference**, so `addToEnd` has to walk the chain (O(n)). Size is tracked by a `numberOfEntries` counter that every add and remove updates, so `size()` is O(1).

| Class | Field | Type |
|---|---|---|
| `LinkedChain<T>` | `firstNode` | `Node<T>` |
| `LinkedChain<T>` | `numberOfEntries` | `int` |
| `Node<E>` (private, inside `LinkedChain`) | `data` | `E` |
| `Node<E>` (private, inside `LinkedChain`) | `next` | `Node<E>` |
| `Party` | `id` | `int` |
| `Party` | `name` | `String` |
| `Party` | `size` | `int` |
| `Party` | `hasReservation` | `boolean` |
| `Party` | `position` | `int` |
| `Party` | `order` | `LinkedChain<MenuItem>` |
| `MenuItem` | `name` | `String` |
| `MenuItem` | `price` | `double` |
| `Table` | `tableNumber` | `int` |
| `Table` | `capacity` | `int` |
| `Table` | `seatedParty` | `Party` |
| `Waitlist` | `line` | `LinkedChain<Party>` |
| `Restaurant` | `waitlist` | `Waitlist` |
| `Restaurant` | `tables` | `LinkedChain<Table>` |
| `Restaurant` | `nextPartyId` | `int` |
| `TableNextApp` | `restaurant` | `Restaurant` |
| `TableNextApp` | `output` | `Output` |
| `TableNextApp` | `input` | `Scanner` |
| `UserType` (enum) | `HOST`, `SERVER`, `CUSTOMER` | None (enum constants) |
| `Output` | none | none |

---

## Method Signatures

### `LinkedChain.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `add(T newEntry)` | `boolean` | Adds a new entry to the front; returns true if successful | O(1) |
| `addToEnd(T newEntry)` | `boolean` | Adds a new entry to the end; returns true if successful | O(n) |
| `remove()` | `T` | Removes the first entry and returns it | O(1) |
| `remove(T anEntry)` | `boolean` | Removes the first occurrence of the entry; returns true if successful | O(n) |
| `clear()` | `void` | Removes all entries | O(1) |
| `get(int index)` | `T` | Returns the entry at the given index | O(n) |
| `contains(T anEntry)` | `boolean` | Returns true if the entry is found | O(n) |
| `count(T anEntry)` | `int` | Returns how many times the entry appears in the chain | O(n) |
| `size()` | `int` | Returns the number of entries | O(1) |
| `isEmpty()` | `boolean` | Returns true if the chain is empty | O(1) |
| `toArray(T[] values)` | `T[]` | Fills `values` with the chain's entries and returns it | O(n) |
| `toString()` | `String` | Returns the entries as a String | O(n) |
| `requireNonNull(T entry)` | `void` | Throws an exception if the entry is null | O(1) |

### `TableNextApp.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `main(String[] args)` | `void` | Creates the restaurant and output objects and starts the program | — |
| `run()` | `void` | Loops: selects a user type, shows that user's menu, and handles choices until quit | — |
| `selectUserType()` | `UserType` | Re-prompts until a valid user type is chosen | O(1) per attempt |
| `handleHostChoice(int choice)` | `void` | Performs the host action for that choice and sends the result to `Output` | O(n + t), t = tables |
| `handleServerChoice(int choice)` | `void` | Performs the server action for that choice and sends the result to `Output` | O(t + m), t = tables, m = menu items |
| `handleCustomerChoice(int choice)` | `void` | Performs the customer view action for that choice and sends the result to `Output` | O(n) |
| `readInt(String prompt, int min, int max)` | `int` | Re-prompts until a whole number in range is entered | O(1) per attempt |
| `readName(String prompt)` | `String` | Re-prompts until a non-blank name is entered | O(1) per attempt |
| `readYesNo(String prompt)` | `boolean` | Re-prompts until `y` or `n` is entered | O(1) per attempt |

### `Restaurant.java`

> The original spec has no method table for `Restaurant`. These signatures are collected from the diagram, the validation table, and the test plan. Big-O is still to be filled in.

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `addTable(int capacity)` | `boolean` | Creates a table with the given capacity; returns false if capacity is zero or negative | TBD |
| `addParty(String name, int size, boolean hasReservation)` | `Party` | Creates a party; seats it at an open table that fits, otherwise adds it to the end of the waitlist; returns null if no table could ever seat that size | TBD |
| `findOpenTable(int partySize)` | `Table` | Returns the smallest open table that fits; returns null if none | TBD |
| `findTable(int tableNumber)` | `Table` | Returns the table with that number; returns null if it does not exist | TBD |
| `seatNext(int tableNumber)` | `boolean` | Seats the earliest waiting party that fits at that table; returns false if the table doesn't exist, is occupied, or no party fits | TBD |
| `clearTable(int tableNumber)` | `boolean` | Empties the table; returns false if it doesn't exist or is already open | TBD |
| `addItemToTable(int tableNumber, MenuItem item)` | `boolean` | Adds the item to the seated party's order; returns false if the table is open or doesn't exist | TBD |
| `removeItemFromTable(int tableNumber, MenuItem item)` | `boolean` | Removes one matching item from the seated party's order; returns false if not found or the table doesn't exist | TBD |
| `getWaitlist()` | `Waitlist` | Returns the waitlist | TBD |
| `getTables()` | `Table[]` | Returns all tables as an array | TBD |

### `Waitlist.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `Waitlist()` | — | Creates an empty waitlist | O(1) |
| `addParty(Party party)` | `boolean` | Adds a party at the end of the line; returns true if successful | O(1) |
| `insertAt(Party party, int position)` | `boolean` | Shifts later parties back one spot and inserts the party; returns false if the position is invalid | O(n) |
| `removeAt(int position)` | `Party` | Removes and returns the party at that position and shifts later parties up; returns null if the position is invalid | O(n) |
| `peekNext()` | `Party` | Returns the party at position 1 without removing it; returns null if empty | O(n) |
| `removeFirstThatFits(int capacity)` | `Party` | Removes and returns the earliest party whose size fits the capacity; returns null if none fit | O(n) |
| `getInOrder()` | `Party[]` | Returns all waiting parties sorted by position | O(n²) |
| `getCount()` | `int` | Returns how many parties are waiting | O(1) |
| `isEmpty()` | `boolean` | Returns true if no parties are waiting | O(1) |
| `findByPosition(int position)` *(private)* | `Party` | Returns the party at that position; returns null if not found | O(n) |

### `Table.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `Table(int tableNumber, int capacity)` | — | Creates an open table with a number and capacity | O(1) |
| `getTableNumber()` | `int` | Returns the table's number | O(1) |
| `getCapacity()` | `int` | Returns how many people the table seats | O(1) |
| `getSeatedParty()` | `Party` | Returns the seated party; returns null if the table is open | O(1) |
| `isOpen()` | `boolean` | Returns true if no party is seated | O(1) |
| `fits(Party party)` | `boolean` | Returns true if the party's size is within the capacity | O(1) |
| `seat(Party party)` | `boolean` | Seats the party; returns false if the table is occupied or too small | O(1) |
| `clear()` | `Party` | Empties the table and returns the party that was seated; returns null if already open | O(1) |
| `equals(Object obj)` | `boolean` | Returns true if both tables have the same table number | O(1) |
| `toString()` | `String` | Returns the table number, capacity, and seated party as a String | O(1) |

### `Party.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `Party(int id, String name, int size, boolean hasReservation)` | — | Creates a party with an empty order | O(1) |
| `getId()` | `int` | Returns the party's unique ID | O(1) |
| `getName()` | `String` | Returns the party's name | O(1) |
| `getSize()` | `int` | Returns the number of people in the party | O(1) |
| `hasReservation()` | `boolean` | Returns true if the party has a reservation | O(1) |
| `getPosition()` | `int` | Returns the party's waiting position | O(1) |
| `setPosition(int position)` | `void` | Updates the party's waiting position | O(1) |
| `addItem(MenuItem item)` | `boolean` | Adds an item to the party's order; returns true if successful | O(1) |
| `removeItem(MenuItem item)` | `boolean` | Removes one matching item from the order; returns false if not found or the order is empty | O(n) |
| `getOrderItems()` | `MenuItem[]` | Returns the party's ordered items as an array | O(n) |
| `getOrderTotal()` | `double` | Returns the sum of all item prices in the order | O(n) |
| `equals(Object obj)` | `boolean` | Returns true if both parties have the same ID | O(1) |
| `toString()` | `String` | Returns the position, name, size, and reservation status as a String | O(1) |

### `MenuItem.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `MenuItem(String name, double price)` | — | Creates a menu item with a name and price | O(1) |
| `getName()` | `String` | Returns the item's name | O(1) |
| `getPrice()` | `double` | Returns the item's price | O(1) |
| `equals(Object obj)` | `boolean` | Returns true if both items have the same name and price | O(1) |
| `toString()` | `String` | Returns the name and price as a String | O(1) |

### `Output.java`

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `Output()` | — | Creates an output object that prints to the console | O(1) |
| `printUserTypeMenu()` | `void` | Prints the list of user types to choose from | O(1) |
| `printMenu(UserType user)` | `void` | Prints the options available to that user type | O(1) |
| `printWaitlist(Party[] parties, UserType user)` | `void` | Prints the waitlist in order, showing only the details that user type may see; prints a message if empty | O(n) |
| `printNext(Party party)` | `void` | Prints the next party in line; prints a message if the party is null | O(1) |
| `printCount(int count)` | `void` | Prints how many parties are waiting | O(1) |
| `printTables(Table[] tables)` | `void` | Prints each table's number, capacity, and seated party or open status | O(n) |
| `printOrder(Party party)` | `void` | Prints the party's ordered items and total; prints a message if the order is empty | O(n) |
| `printMessage(String message)` | `void` | Prints a confirmation or informational message | O(1) |
| `printError(String message)` | `void` | Prints an error message in a consistent format | O(1) |

---

## Where Validation Lives

Validation lives primarily in the main class (`TableNextApp`). Model classes report problems with `false` or `null` returns; constructors throw as a backup.

| Bad input | Caught by | What happens |
|---|---|---|
| Party size is text | `TableNextApp.readInt` | Catches `NumberFormatException`, sends an error to `Output.printError`, and re-prompts |
| Party size is zero or negative | `TableNextApp.readInt` (range check); `Party` constructor as backup | Re-prompts; the constructor throws `IllegalArgumentException` if a bad size ever reaches it |
| Empty or blank party name | `TableNextApp.readName`; `Party` constructor as backup | Re-prompts; the constructor throws `IllegalArgumentException` if a blank name ever reaches it |
| Removing from an empty waitlist | `Waitlist.removeAt` | Returns null; the app prints "The waitlist is empty" and returns to the menu |
| Viewing next or the full list with an empty waitlist | `Waitlist.peekNext` returns null; `Waitlist.getInOrder` returns an empty array | `Output.printNext` and `Output.printWaitlist` print "The waitlist is empty" |
| Position that does not exist (remove party 9 of 4) | `Waitlist.removeAt` through `findByPosition` | Returns null and changes nothing; the app prints an error and returns to the menu |
| Inserting at a position outside 1 to count + 1 | `Waitlist.insertAt` | Returns false and changes nothing; the app prints an error |
| Removing an item from an empty order | `Party.removeItem` | Returns false; the app prints "That item is not in the order" |
| Removing an item that is not in the order | `Party.removeItem` | Returns false; the order is unchanged |
| Duplicate group (same party added twice) | `Waitlist.addParty` using `LinkedChain.contains` | Returns false and does not add; parties with the same name but different IDs are allowed |
| Party larger than the biggest table | `Restaurant.addParty` | Returns null and adds nothing; the app prints that no table can seat a party that size |
| Table number that does not exist | `Restaurant.findTable` returns null; `clearTable`, `seatNext`, `addItemToTable`, and `removeItemFromTable` check for it | Those methods return false; the app prints an error |
| Clearing a table that is already open | `Restaurant.clearTable` | Returns false; nothing changes |
| Seating at a table that is occupied | `Table.seat` and `Restaurant.seatNext` | Returns false; the waitlist is unchanged |
| Adding an item to an open table | `Restaurant.addItemToTable` | Returns false; the app prints "No party is seated at that table" |
| Table capacity of zero or negative | `Restaurant.addTable` | Returns false; no table is created |
| Menu choice or user type out of range, or text | `TableNextApp.readInt` and `selectUserType` | Re-prompts |
| Yes/no answer that is not `y` or `n` | `TableNextApp.readYesNo` | Re-prompts |
| Null entry passed to the chain | `LinkedChain.requireNonNull` | Throws an exception; app classes check for null first, so this is never reached in normal use |

---

## Test Plan

### LinkedChain

| Method | Normal case | Bad-input case |
|---|---|---|
| `add(T newEntry)` | Add "A" to an empty chain: returns true and size is 1 | Add null: throws an exception and size is unchanged |
| `addToEnd(T newEntry)` | Add "B" to a chain holding "A": returns true and "B" is last | Add null: throws an exception |
| `remove()` | Chain holds "A" and "B": returns the first entry and size is 1 | Empty chain: returns null and size stays 0 |
| `remove(T anEntry)` | Chain holds two "A" and one "B", remove "A": returns true and one "A" remains | Remove "C" which is not in the chain: returns false and size is unchanged |
| `clear()` | Chain of 3 entries: size becomes 0 and `isEmpty` is true | Clear an empty chain: stays empty with no error |
| `get(int index)` | Valid index in a chain of 3: returns the entry at that index | Negative index or index past the end: returns null and the chain is unchanged |
| `contains(T anEntry)` | Chain holds "A", check "A": returns true | Check "C" which is not in the chain: returns false |
| `count(T anEntry)` | Chain holds two "A", count "A": returns 2 | Count "C" which is not in the chain: returns 0 |
| `size()` | Chain of 3 entries: returns 3 | Empty chain: returns 0 |
| `isEmpty()` | Chain of 1 entry: returns false | Empty chain: returns true |
| `toArray(T[] values)` | Chain of 3 entries: returns an array holding all 3 | Empty chain: returns an array with no entries |
| `toString()` | Chain of "A" and "B": returns a String showing both | Empty chain: returns an empty-chain String with no error |

### MenuItem

| Method | Normal case | Bad-input case |
|---|---|---|
| `MenuItem(String name, double price)` | "Burger", 12.00: creates the item with that name and price | Blank name or negative price: throws `IllegalArgumentException` |
| `getName()`, `getPrice()` | Item "Burger" at 12.00: returns "Burger" and 12.00 | No input, so no bad-input case |
| `equals(Object obj)` | Two items both "Burger" at 12.00: returns true | Compare to null or to a `Party`: returns false |
| `toString()` | "Burger" at 12.00: returns "Burger $12.00" | No input, so no bad-input case |

### Party

| Method | Normal case | Bad-input case |
|---|---|---|
| `Party(int id, String name, int size, boolean hasReservation)` | 1, "Kim", 3, false: creates the party with an empty order | Size of 0 or -2, or a blank name: throws `IllegalArgumentException` |
| `getId()`, `getName()`, `getSize()`, `hasReservation()`, `getPosition()` | Party 1 "Kim" of 3 at position 2: each returns its matching value | No input, so no bad-input case |
| `setPosition(int position)` | Set position to 2: `getPosition` returns 2 | Set position to 0 or -1: throws `IllegalArgumentException` and the position is unchanged |
| `addItem(MenuItem item)` | Add "Burger": returns true and the order has 1 item | Add null: returns false and the order is unchanged |
| `removeItem(MenuItem item)` | Order has two "Burger", remove "Burger": returns true and one remains | Remove from an empty order, or remove an item not ordered: returns false |
| `getOrderItems()` | Order of "Burger" and "Fries": returns an array of both | Empty order: returns an array with no entries |
| `getOrderTotal()` | "Burger" 12.00 and "Fries" 4.00: returns 16.00 | Empty order: returns 0.00 |
| `equals(Object obj)` | Two parties with ID 1: returns true, even if names differ | Compare to null, or parties named "Kim" with IDs 1 and 2: returns false |
| `toString()` | Party "Kim" of 3 at position 2: returns a String with those details | No input, so no bad-input case |

### Waitlist

| Method | Normal case | Bad-input case |
|---|---|---|
| `addParty(Party party)` | Add to a list of 2: returns true, the party gets position 3, count is 3 | Add the same party twice, or add null: returns false and count is unchanged |
| `insertAt(Party party, int position)` | Insert at 2 in a list of 3: new party is at 2, old positions 2 and 3 become 3 and 4 | Position 0, or position 6 in a list of 3: returns false and nothing changes |
| `removeAt(int position)` | Remove 2 from a list of 4: returns that party, old positions 3 and 4 become 2 and 3 | Remove 9 from a list of 4, or remove from an empty list: returns null and nothing changes |
| `peekNext()` | List of 3: returns the party at position 1 and count stays 3 | Empty list: returns null |
| `removeFirstThatFits(int capacity)` | Capacity 4 with parties of 6, 3, 2 in line: returns the party of 3, and the party of 2 moves to position 2 | Capacity 2 with parties of 6 and 4, or an empty list: returns null and nothing changes |
| `getInOrder()` | Parties added then one inserted at 1: returns the array ordered by position 1, 2, 3 | Empty list: returns an array with no entries |
| `getCount()` | List of 3: returns 3 | Empty list: returns 0 |
| `isEmpty()` | List of 1: returns false | Empty list: returns true |

### Table

| Method | Normal case | Bad-input case |
|---|---|---|
| `Table(int tableNumber, int capacity)` | 1, 4: creates open table 1 that seats 4 | Capacity of 0 or -1: throws `IllegalArgumentException` |
| `getTableNumber()`, `getCapacity()`, `getSeatedParty()` | Table 1 of 4 with "Kim" seated: returns 1, 4, and Kim's party | Open table: `getSeatedParty` returns null |
| `isOpen()` | New table: returns true | Table with a party seated: returns false |
| `fits(Party party)` | Party of 3 at a table of 4: returns true | Party of 6 at a table of 4, or null: returns false |
| `seat(Party party)` | Party of 3 at an open table of 4: returns true and `isOpen` is false | Occupied table, or a party of 6 at a table of 4: returns false and the table is unchanged |
| `clear()` | Table with "Kim" seated: returns Kim's party and `isOpen` is true | Table already open: returns null |
| `equals(Object obj)` | Two tables numbered 1: returns true | Compare to null or to table 2: returns false |
| `toString()` | Table 1 of 4 with "Kim": returns a String with those details | Open table: the String says the table is open |

### Restaurant

| Method | Normal case | Bad-input case |
|---|---|---|
| `addTable(int capacity)` | Capacity 4: returns true and `getTables` has one more table | Capacity 0 or -3: returns false and no table is created |
| `addParty(String name, int size, boolean hasReservation)` | "Kim", 3 with an open table of 4: party is seated there. With no open table that fits: party joins the end of the waitlist | Party of 12 when the biggest table seats 8: returns null and nothing is added |
| `findOpenTable(int partySize)` | Size 3 with open tables of 2, 4, and 8: returns the table of 4 | Size 3 with every table occupied or too small: returns null |
| `seatNext(int tableNumber)` | Open table of 4 with parties of 6 then 3 waiting: seats the party of 3, returns true, count drops by 1 | Table 99 does not exist, table is occupied, or no party fits: returns false and the waitlist is unchanged |
| `clearTable(int tableNumber)` | Table 2 has a party seated: returns true and the table is open | Table 99 does not exist, or table 2 is already open: returns false |
| `addItemToTable(int tableNumber, MenuItem item)` | Table 2 is seated, add "Burger": returns true and the order has 1 item | Table 2 is open, or table 99 does not exist: returns false |
| `removeItemFromTable(int tableNumber, MenuItem item)` | Table 2's order has two "Burger", remove "Burger": returns true and one remains | Empty order, item not ordered, or table does not exist: returns false |
| `getWaitlist()`, `getTables()` | Restaurant with 3 tables: `getTables` returns an array of 3, `getWaitlist` returns the waitlist | New restaurant: `getTables` returns an array with no entries |

### Output

| Method | Normal case | Bad-input case |
|---|---|---|
| `printUserTypeMenu()`, `printMenu(UserType user)` | `HOST`: prints the host options only | No user input, so no bad-input case |
| `printWaitlist(Party[] parties, UserType user)` | 3 parties as `HOST`: prints names, sizes, and reservations in order. As `CUSTOMER`: prints positions and sizes only | Array with no entries: prints "The waitlist is empty" |
| `printNext(Party party)` | Party "Kim" of 3: prints Kim as next | Null: prints "The waitlist is empty" |
| `printCount(int count)` | 3: prints "3 parties waiting" | 0: prints "No parties waiting" |
| `printTables(Table[] tables)` | 2 tables, one seated: prints each table's number, capacity, and status | Array with no entries: prints "No tables set up" |
| `printOrder(Party party)` | Order of "Burger" and "Fries": prints both items and the total | Empty order: prints "No items ordered" |
| `printMessage(String message)`, `printError(String message)` | "Kim removed": prints the message, with errors clearly marked | Null or blank message: prints nothing and does not crash |

### TableNextApp

| Method | Normal case | Bad-input case |
|---|---|---|
| `main(String[] args)`, `run()` | Choose a user type, pick options, then quit: the program ends normally | Covered by the read methods below; no input reaches `run` without passing through them |
| `selectUserType()` | Enter 1: returns `HOST` | Enter 9 or "abc": prints an error and re-prompts |
| `handleHostChoice(int choice)` | Choice for "add party" with "Kim", 3, n: party is added and a confirmation prints | Choice for "remove" with position 9 of 4: prints an error and returns to the menu |
| `handleServerChoice(int choice)` | Choice for "add item" at seated table 2: item is added and a confirmation prints | Choice for "add item" at open table 2: prints an error and returns to the menu |
| `handleCustomerChoice(int choice)` | Choice for "view waitlist": prints positions and sizes | Choice for "view waitlist" when empty: prints "The waitlist is empty" |
| `readInt(String prompt, int min, int max)` | Enter "3" with range 1 to 20: returns 3 | Enter "abc", "-2", "0", or nothing: prints an error and re-prompts until valid |
| `readName(String prompt)` | Enter "Kim": returns "Kim" | Enter nothing or only spaces: prints an error and re-prompts |
| `readYesNo(String prompt)` | Enter "y": returns true | Enter "maybe": prints an error and re-prompts |

---

## Revised Scope

| What changed | Why | Where it came from |
|---|---|---|
| The app grew from a waitlist to tables with capacity, seating, three kinds of user, and a separate `Output` class for printing | A waitlist exists to seat people, and the host, the server, and the customer need different actions | Group discussion |
| Each party carries its own order of `MenuItem` objects, with an order total. `Party` and `MenuItem` are the two element types | The project needs one chain class that holds at least two unrelated types | GenAI suggested pairing a party with a menu item when we brainstormed |
| The node no longer holds a waitlist number. The number is `Party.position`, and `Waitlist` renumbers the parties after every insert or removal | The provided `Node` is generic and holds only an entry and a link, so the position has to live somewhere else. Storing it on `Party` lets a party report its place without counting. The cost is an O(n) renumber on each change, the same order as finding the spot | GenAI flagged the node-with-a-number idea as hard to keep correct |
| We use the provided `LinkedChain` as it is, with no new methods. Insert at a position, remove from a position, and remove the first party that fits are written in `Waitlist` | The provided class is meant to own storage only, so restaurant rules, including the size comparison for fit, stay in `Waitlist` | Group discussion |
| Seating the first party that fits is part of the design instead of a stretch goal | Seating a party at an open table needs it | GenAI suggested it as a stretch goal; group discussion and grading from the scope made it part of the design |
| Errors come back as `false` or `null` results plus a message from `Output.printError`. Exceptions stay inside the classes as a backup | Our unknowns list said the program must not crash on bad input | Group discussion |
| The contract decisions are written down: `equals` is by ID for `Party`, by name and price for `MenuItem`, and by table number for `Table`. The chain allows duplicates, and `Waitlist.addParty` blocks the same party twice | The spec has to say what the chain promises | Drafted by GenAI |

---

## Work Attribution

| Section | Owner | Contribution |
|---|---|---|
| Class Design | Miles D'Antonio | Drafted the class responsibilities and structure |
| Method Signatures and Big-O | Miles D'Antonio | Defined the public methods, their behavior, and estimated time complexity |
| Where Validation Lives | Miles D'Antonio | Identified where each invalid input is caught and how the program responds |
| Test Plan | Miles D'Antonio | Created normal and bad-input test cases for the public methods |
| Generic Contract | Ethan Kahl | Defined the `LinkedChain<T>` type parameter, type-safety rules, and null-entry policy |
| Data and State | Ethan Kahl | Documented the fields, node storage, references, and collection state |
| Revised Scope | Ethan Kahl | Recorded design changes, reasons, and where each change originated |
| System Diagram | Alex Chen | Created the diagram showing the class relationships, data flow, and where `LinkedChain<T>` is used |
| Work Attribution and GenAI Reflection | Alex Chen | Created the contribution record and documented how GenAI was used to review and improve the specification |

---

## GenAI Reflection

Alex Chen used GenAI to help create and revise the system diagram for this specification. GenAI helped turn the group's written class design into a visual diagram and suggested which relationships needed labeled arrows, including `TableNextApp` calling `Restaurant` and `Output`, `Waitlist` storing `LinkedChain<Party>`, `Restaurant` storing tables, `Table` holding a seated `Party`, and `Party` storing `LinkedChain<MenuItem>`. It also helped revise the diagram layout so the arrows and class boxes were easier to read. Alex checked the final diagram against the group's written specification to make sure the class names, fields, and methods matched. GenAI was used for diagram design, revision, and to make sure nothing was missed, not to implement the project code.
