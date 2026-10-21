# TableNext

TableNext is a console-based restaurant waitlist and table-management program. Hosts manage parties and tables, servers manage food orders, and customers view the public waitlist.

## Features

- **Host:** add, insert, and remove parties on the waitlist; view the next party and the waitlist count; seat and clear tables; view a color-coded table map; add tables.
- **Server:** view tables and add, remove, or view a seated party's order.
- **Customer:** view the waitlist (position and party size only) and the number of parties waiting.
- **Smart seating:** a new party is seated immediately at the smallest open table that fits, otherwise it joins the waitlist. Parties that need accessibility are only seated at tables with accessible seats.
- **Reservations:** parties can carry an optional reservation time.
- **Demo presets:** the Host's "Add Table" option can add a custom table or load a preset scenario (see below).

## Requirements

- Java 17 or newer
- A terminal that supports ANSI colors (for the table map)

## Build and Run

From the project root:

```bash
mkdir -p out
javac -d out src/main/java/*.java
java -cp out TableNextApp
```

On startup, choose a role: `1` Host, `2` Server, `3` Customer, or `0` to quit.

## Usage

Reservation times use the format `yyyy-MM-dd HH:mm`, for example `2026-10-14 18:30`.

Server menus accept a food item by number or by name (for example `1` or `Burger`).

### Host: Add Table and Presets

Host option `10` adds a table or loads a demonstration scenario on top of the starting tables:

| Option | Scenario |
| --- | --- |
| Custom Table | Add one table with a chosen size (even number) and number of accessible seats |
| Busy Night | Every table is full and several parties are waiting |
| Smallest Fit | Small parties take the smallest open table instead of larger ones |
| Accessibility | Accessible seats are in use and accessible parties wait for a suitable table |
| Reservations | Tables are full and the waitlist has reservation times, with one party inserted at the front |

Presets add to the current state, so load them on a fresh start for predictable results.

## Project Structure

| File | Purpose |
| --- | --- |
| `TableNextApp.java` | Console menus and input validation |
| `Restaurant.java` | Coordinates the waitlist, tables, menu, seating, and orders |
| `Waitlist.java` | Ordered waitlist with position tracking |
| `Table.java` | A table, its accessible seats, and the party seated there |
| `Party.java` | A party's details, reservation, and order |
| `MenuItem.java` | A food item with a name and price |
| `Display.java` | Console output, menus, and table drawings |
| `UserType.java` | Host, Server, and Customer roles |
| `LinkedChain.java` | Generic linked list used for the waitlist, tables, menu, and orders |

## Testing

Tests live in `src/test/java` and extend the CS 2114 `student.TestCase` (JUnit). Each class has a matching test class: `MenuItemTest`, `PartyTest`, `TableTest`, `WaitlistTest`, `RestaurantTest`, `DisplayTest`, and `TableNextAppTest`.

## Known Limitations

- Data is held in memory only and is lost when the program exits.
- Estimated wait times and text notifications are not implemented.

## Credits

- `LinkedChain.java` was provided by CS 2114 staff and is used unchanged.
- Alex Chen: `Waitlist`, `Restaurant`, `TableNextApp`, and additions to `Table`.
- Miles D'Antonio: `Party`, `MenuItem`, `Display`.
- Ethan Kahl: all test classes.
- The team defined `UserType` and the original `Table` design.

Parts of the code, tests, and documentation were developed with help from Claude (GenAI). Each file was reviewed by its listed contributor before use.
