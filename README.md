# TableNext

TableNext is a console-based restaurant waitlist and table-management program. Hosts can manage parties and tables, servers can manage orders, and customers can view the public waitlist.

## Running the Program

From the `TableNext-main` folder, compile and run:

```bash
mkdir -p out
javac -d out src/main/java/*.java
java -cp out TableNextApp
```

Enter `1` for Host, `2` for Server, `3` for Customer, or `0` to quit.

For a reservation time, use this format:

```text
yyyy-MM-dd HH:mm
```

Example:

```text
2026-10-14 18:30
```

## Running Tests
There are 7 test classes and 192 tests. 
Test class     Tests	      What it covers

MenuItemTest	  19	        Name and price validation, equals ignoring case, formatting
PartyTest	      28	        Party details, moving a reservation, waitlist position, order items
TableTest	      27	        Size and accessible-seat rules, which seats fill first, seating and clearing
WaitlistTest	  17	        Add, insert, and remove by position, and renumbering after every change
RestaurantTest	29	      Smallest-table seating, waitlist overflow, seating the first party that fits
DisplayTest	    35	      What each menu and message prints, and the empty-state wording
TableNextAppTest	37	    The typed-input loops, each role's menu, and re-asking on bad input



## Reflection

### What changed from the specification?

The original specification described an `Output` class and a simpler waitlist. The implemented program uses the renamed `Display` class, which contains static methods for console output and table drawings.

`Party` now assigns its own ID and stores a host name, party size, accessibility need, and optional `LocalDateTime` reservation time. `Table` assigns its own table number and supports accessible seats. A `Restaurant` class was added to coordinate the waitlist, tables, food menu, seating, and orders.

`TableNextApp` owns the console menu and input validation. It checks menu choices, names, numbers, yes/no answers, and reservation-time input before calling the model classes.

Because the provided `LinkedChain` does not have an insert-at-index method, `Waitlist.insertAt` rebuilds the chain using the public API and then updates each party’s position. Removing a party also updates later positions.

### What changed in the LinkedChain?

`LinkedChain` was used unchanged. The application reuses the same generic class for different element types:

- `LinkedChain<Party>` for the waitlist
- `LinkedChain<Table>` for restaurant tables
- `LinkedChain<MenuItem>` for the food menu and party orders

The application uses only public `LinkedChain` methods such as `addToEnd`, `remove`, `contains`, `get`, `size`, and `toArray`.

### What happened to the unknowns?

The team resolved how to keep waitlist positions correct, insert or remove parties by position, and reuse one generic linked chain for unrelated types. The remaining work is mainly completing and reviewing the JUnit tests, checking bad-input cases, and preparing for the grading demonstration.

### What does not work yet?

The core console prototype works. Estimated wait times, text notifications, and a generalized HokieWait version are not included in this prototype.

## Next Steps

1. Review the program’s input validation and fix any issues found by the tests.
2. Verify the full MVP path: add, insert, remove, view, count, seat, clear, and order items.
3. Prepare the final demonstration and make sure every team member can explain their code.

The team is keeping table-capacity seating and food orders as implemented extensions. Estimated wait times, text notifications, and the HokieWait generalization will not be included in this version.

## Attribution

| File | Contributors and role |
| --- | --- |
| `LinkedChain.java` | CS 2114 staff — provided starter implementation; used unchanged. |
| `Waitlist.java` | Alex Chen — implemented waitlist behavior and position updates; GenAI-assisted development, reviewed before use. |
| `Restaurant.java` | Alex Chen — implemented restaurant coordination, seating, menu, and order methods; GenAI-assisted development, reviewed before use. |
| `TableNextApp.java` | Alex Chen — implemented console menus and input validation; GenAI-assisted development, reviewed before use. |
| `Table.java` | Team — original table and accessibility design; Alex Chen added `fits`, `equals`, `hashCode`, and `toString`. |
| `Party.java` | Miles D’Antonio — reviewed party design and implementation; GenAI-assisted revisions were reviewed before use. |
| `MenuItem.java` | Miles D’Antonio — reviewed menu-item design and implementation; GenAI-assisted revisions were reviewed before use. |
| `Display.java` | Miles D’Antonio — reviewed console-display implementation; GenAI-assisted implementation was reviewed before use. |
| `UserType.java` | Team — defined user roles. |
| Test files in `src/test/java` | Ethan Kahl — writing and running JUnit tests for normal and bad-input cases. |

## GenAI Note
