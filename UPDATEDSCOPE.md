# TableNext — Updated Scope

This updates [SCOPE.md](SCOPE.md) to match the code as it is now. It only lists what changed. Any section not mentioned here is still as written in `SCOPE.md`.

The biggest change: the `Output` class is now called **`Display`**, and it is finished. `Display`, `Party`, `MenuItem`, and `UserType` are done and compile. They are written so that `Restaurant`, `Waitlist`, and `TableNextApp` can be built on them exactly as `SCOPE.md` plans, with the few signature changes listed under [What the Remaining Classes Need to Know](#what-the-remaining-classes-need-to-know).

## Contents

- [Class Design](#class-design)
- [Display](#display)
- [What the Remaining Classes Need to Know](#what-the-remaining-classes-need-to-know)
- [Changes to Finished Classes](#changes-to-finished-classes)
- [Display Test Plan](#display-test-plan)
- [Revised Scope (additions)](#revised-scope-additions)

---

## Class Design

| Class | Role | Status |
|---|---|---|
| `TableNextApp` | Main class. Owns the input loop and validates all user input | Not started |
| `Restaurant` | Holds the waitlist, the tables, and the food menu, and coordinates seating and orders | Not started |
| `Waitlist` | Wraps `LinkedChain<Party>` with position-based waitlist operations | Not started |
| `Table` | One table with an automatic number, an even size, accessible seats, and an optional seated party | Done |
| `Party` | A waiting or seated group with a host name, accessibility need, optional reservation time, and its own order of `MenuItem`s | Done |
| `MenuItem` | A menu item with a name and price | Done |
| `Display` *(was `Output`)* | All console printing, including drawing tables | Done |
| `UserType` *(enum)* | `HOST`, `SERVER`, `CUSTOMER` | Done |

**How the diagram changes:** rename the `Output` box to `Display` and add the table-drawing methods and `printFoodMenu()`. The arrow from `TableNextApp` becomes a static call (`Display.printMenu(...)`), not an `output` field. Add an arrow from `Display` to `UserType` labeled "`printMenu()` and `printWaitlist()` take a `UserType`". Add arrows from `Display` to `Table`, `Party`, and `MenuItem` labeled "reads to print".

---

## Display

### Design decisions

- **All methods are `static`.** `Display` has no fields, only color constants, so there is no `Display()` constructor, and `TableNextApp` does not need an `output` field. This replaces the `Output()` constructor and the `TableNextApp.output` field in `SCOPE.md`.
- **Display only takes arrays and single objects.** It never takes a `Waitlist` or `Restaurant`, so it does not depend on either one. The caller passes in `waitlist.getInOrder()`, `restaurant.getTables()`, and so on.
- **Colors use ANSI escape codes.** Green means an open table or seat and red means taken. Blue is an open accessible seat and purple is a taken accessible seat. Yellow is the party ID drawn on a seated table. Errors print in red and confirmations in green.
- **Each user type sees different waitlist details.** A host sees position, ID, name, size, accessibility, and reservation time. Everyone else sees only position and size.

### Fields

| Field | Type | Value |
|---|---|---|
| `COLOR_RESET` | `static final String` | `\u001B[0m` |
| `COLOR_RED` | `static final String` | `\u001B[31m` |
| `COLOR_GREEN` | `static final String` | `\u001B[32m` |
| `COLOR_YELLOW` | `static final String` | `\u001B[33m` |
| `COLOR_BLUE` | `static final String` | `\u001B[34m` |
| `COLOR_PURPLE` | `static final String` | `\u001B[35m` |

### Method signatures

In this table, *s* is a table's seat count, *n* is the number of waiting parties, *t* is the number of tables, *m* is the number of items in an order, and *k* is the number of items on the food menu.

| Method | Returns | Behavior | Big-O |
|---|---|---|---|
| `generateFullTable(Table table)` | `String` | Builds the full drawing: label, top chairs, table, and bottom chairs | O(s) |
| `generateTable(Table table)` | `String` | Draws the table body. If a party is seated, the party ID appears in yellow in the middle | O(s) |
| `generatePartyId(Table table, int width)` | `String` | Centers `Party ID: __` in yellow, or `ID: __` if the longer form doesn't fit, then switches back to the table color | O(s) |
| `generateChair(Table table, int start)` | `String` | Draws one row of chairs, numbered from `start`, each colored by seat status | O(s) |
| `generateLabel(Table table)` | `String` | Centers `Table #N` above the table | O(s) |
| `getTableColor(Table table)` | `String` | Green if the table is open, red if not | O(1) |
| `getSeatColor(Table table, int seat)` | `String` | Green or red for a regular seat, blue or purple for an accessible seat | O(1) |
| `printUserTypeMenu()` | `void` | Prints Host, Server, Customer, and Quit | O(1) |
| `printMenu(UserType user)` | `void` | Prints only that user type's options. Prints nothing for `null` | O(1) |
| `printWaitlist(Party[] parties, UserType user)` | `void` | Prints parties in the order given. A host sees every detail; others see position and size only | O(n) |
| `printNext(Party party)` | `void` | Prints the next party's name, size, and ID | O(1) |
| `printCount(int count)` | `void` | Prints "N parties waiting", or "1 party waiting" for one | O(1) |
| `printTables(Table[] tables)` | `void` | Prints every table drawing, then a color key | O(t · s) |
| `printOrder(Party party)` | `void` | Prints each item and price in columns, in the order they were added, then the total | O(m) |
| `printFoodMenu(MenuItem[] items)` | `void` | Prints the food menu numbered from 1, so the server can pick an item by number | O(k) |
| `printMessage(String message)` | `void` | Prints a confirmation in green | O(1) |
| `printError(String message)` | `void` | Prints `Error: message` in red | O(1) |
| `formatReservation(Party party)` *(private)* | `String` | Formats the reservation time as `6:30 PM`, or returns `None` | O(1) |

### Validation in Display

| Bad input | What `Display` does |
|---|---|
| `null` or empty party array | `printWaitlist` prints "The waitlist is empty" |
| `null` party for next | `printNext` prints "The waitlist is empty" |
| Count of zero or less | `printCount` prints "No parties waiting" |
| `null` or empty table array | `printTables` prints "No tables set up" |
| `null` party for an order | `printOrder` prints the error "No party is seated at that table" |
| Order with no items | `printOrder` prints "No items ordered" |
| `null` or empty food menu | `printFoodMenu` prints "The menu is empty" |
| `null` or blank message | `printMessage` and `printError` print nothing |
| `null` user type | `printMenu` prints nothing |

---

## What the Remaining Classes Need to Know

### `Waitlist`

- `getInOrder()` must return the parties **sorted by position**, because `printWaitlist` prints them in the order it is given.
- `addParty` and `insertAt` must call `party.setPosition(...)`. Positions start at 1, and `setPosition` throws `IllegalArgumentException` for anything below 1.
- `addParty` can use `LinkedChain.contains` to block duplicates. `Party.equals` compares IDs, so two parties with the same name are still allowed.

### `Restaurant`

Because `Party` and `Table` changed, two methods in `SCOPE.md` can no longer be written as planned:

| SCOPE.md | Should be |
|---|---|
| `addParty(String name, int size, boolean hasReservation)` | `addParty(String hostName, int size, boolean needAccessibility, LocalDateTime reservationTime)` |
| `addTable(int capacity)`, returning false for zero or negative | `addTable(int size, int handicapSeats)`, returning false if the size is not a positive even number or the handicap seats don't fit |

- The `nextPartyId` field is no longer needed, because `Party` assigns its own IDs.
- **`Restaurant` needs a food menu**, which is new. The server's "add item to table" needs a list of `MenuItem`s to choose from, and `SCOPE.md` never says where that list lives. The suggestion is a `LinkedChain<MenuItem> menu` field with `getMenu()` returning `MenuItem[]`. `TableNextApp` passes that array to `Display.printFoodMenu` and reads a number from 1 to the array's length.

### `TableNextApp`

`printMenu` sets the option numbers. `handleHostChoice`, `handleServerChoice`, and `handleCustomerChoice` must use the same numbers:

| Choice | Host | Server | Customer |
|---|---|---|---|
| 1 | Add party | View tables | View waitlist |
| 2 | Insert party at position | Add item to table | View waitlist count |
| 3 | Remove party at position | Remove item from table | — |
| 4 | View waitlist | View table order | — |
| 5 | View next party | — | — |
| 6 | View waitlist count | — | — |
| 7 | Seat next party at table | — | — |
| 8 | Clear table | — | — |
| 9 | View tables | — | — |
| 0 | Switch user | Switch user | Switch user |

`printUserTypeMenu` uses 1 = Host, 2 = Server, 3 = Customer, and 0 = Quit, so `selectUserType` must map them the same way.

---

## Changes to Finished Classes

### `Party`

| SCOPE.md | Now |
|---|---|
| `Party(int id, String name, int size, boolean hasReservation)` | `Party(String hostName, int size, boolean needAccessibility, LocalDateTime reservationTime)`. The ID is assigned automatically, starting at 0. A blank or `null` name, or a size of 0 or less, throws `IllegalArgumentException` |
| `int id` | `long id` |
| `getName()` | `getHostName()` |
| `boolean hasReservation` field | `LocalDateTime reservationTime`. `hasReservation()` returns true when it is not `null` |
| — | `needAccessibility()`, `setNeedAccessibility(boolean)`, `setSize(int)` (throws for 0 or less), `getReservationTime()`, `updateReservationTime(boolean moveLater)` |
| `setPosition(int)` throws for 0 or less | Same, as planned |
| `addItem(MenuItem)`: O(1) | Adds to the **end**, so orders print in the order they were placed. O(m), because `LinkedChain.addToEnd` walks the chain |
| `removeItem(MenuItem)`: O(m) | Same, as planned. It calls `LinkedChain.remove(T)` directly |
| `getOrderItems()`: O(m) | Renamed to `getOrder()`. O(m), using `LinkedChain.toArray` |
| `getOrderTotal()`: O(m) | Same, as planned |
| — | `hashCode()` based on the ID, to match `equals` |

### `MenuItem`

| SCOPE.md | Now |
|---|---|
| — | `setName(String)` and `setPrice(double)`, with the same checks as the constructor |
| `equals`: same name and price | Same name (**ignoring case**) and price |
| `toString()` returns "Burger $12.00" | Same, as planned |
| — | `hashCode()` that ignores the name's case, to match `equals` |

### `Table`

| SCOPE.md | Now |
|---|---|
| `Table(int tableNumber, int capacity)` | `Table(int size)` and `Table(int size, int handicapSeats)`. Table numbers are assigned automatically, starting at 1 |
| Any positive capacity | Size must be a **positive even number**, because chairs are drawn in two equal rows. Otherwise the constructor and `setSize` throw `IllegalArgumentException` |
| `getCapacity()` | `getSize()` |
| — | `setSize(int)`, `getHandicapSeats()`, `isHandicapSeat(int seat)`, `isSeatTaken(int seat)` |
| `fits(Party)`, `equals(Object)`, `toString()` | Not written yet |

### `UserType`

Written as planned: `HOST`, `SERVER`, `CUSTOMER`.

The `Party` and `MenuItem` rows of the `SCOPE.md` test plan still apply as written, using the new names (`getHostName`, `getOrder`, and the new constructor).

---

## Display Test Plan

This replaces the `Output` test table in `SCOPE.md`.

| Method | Normal case | Bad-input case |
|---|---|---|
| `printUserTypeMenu()`, `printMenu(UserType user)` | `HOST`: prints the 9 host options and "0. Switch user" | `null`: prints nothing |
| `printWaitlist(Party[] parties, UserType user)` | 3 parties as `HOST`: prints position, ID, name, size, accessibility, and reservation. As `CUSTOMER`: position and size only | Empty array or `null`: prints "The waitlist is empty" |
| `printNext(Party party)` | "Kim" of 3: prints "Next up: Kim, party of 3 (ID 0)" | `null`: prints "The waitlist is empty" |
| `printCount(int count)` | 3: "3 parties waiting". 1: "1 party waiting" | 0: "No parties waiting" |
| `printTables(Table[] tables)` | One table of 2 seated and one table of 6 open: both are drawn, the seated one in red with its party ID in yellow, followed by the key | Empty array: prints "No tables set up" |
| `printOrder(Party party)` | Burger 12.00, Fries 4.00, Lemonade 3.50 added in that order: prints them in that order and a total of $19.50 | Empty order: "No items ordered". `null`: prints an error |
| `printFoodMenu(MenuItem[] items)` | Burger, Fries, Lemonade: prints them numbered 1 to 3 with prices | Empty array or `null`: prints "The menu is empty" |
| `printMessage`, `printError` | "Kim removed": prints in green. "Position 9 does not exist": prints `Error: …` in red | `null` or `"   "`: prints nothing |
| `generateFullTable(Table table)` | Table of 4 with 2 accessible seats and a party of 3 needing accessibility: seats 3 and 4 are purple, seat 1 is red, seat 2 is green | Open table: everything is green, with no party ID |
| `generatePartyId(Table table, int width)` | Width 18: `Party ID: 4` | Width 9: falls back to `ID: 4` |

---

## Revised Scope (additions)

| What changed | Why | Where it came from |
|---|---|---|
| `Output` was renamed to `Display`, and all of its methods are `static` | It has no state, so an object adds nothing | Miles D'Antonio |
| `Display` draws each table with numbered, colored chairs and shows the seated party's ID | Lets a host see at a glance which tables and seats are free | Miles D'Antonio; GenAI (Claude Code) added the party ID label |
| Tables must have an even size | The drawing puts half the chairs on each side | Miles D'Antonio; GenAI (Claude Code) wrote the check |
| `Display.printFoodMenu` and a food menu in `Restaurant` were added | The server's "add item" action needs a list of items to choose from, and `SCOPE.md` didn't say where it lives | Found by GenAI (Claude Code) while checking what the remaining classes need |
| `Restaurant.addParty` and `addTable` take the new `Party` and `Table` fields | The old signatures can't build a `Party` or `Table` anymore | Found by GenAI (Claude Code) while checking `Display` against the other classes |
| `Party` uses a host name, an accessibility need, and a reservation time instead of `hasReservation` | Hosts need to know when a party is booked and whether it needs accessible seating | Miles D'Antonio |
| `Party` and `MenuItem` were brought in line with the `SCOPE.md` test plan: input checks, orders kept in the order placed, O(m) order methods, `hashCode`, and `MenuItem.toString` | So `Waitlist` and `Restaurant` can rely on them as specified | GenAI (Claude Code), reviewed by Miles D'Antonio |
| `Display`'s print methods were written by GenAI | Done to the spec in the `Output` method table | GenAI (Claude Code), reviewed by Miles D'Antonio |
