import java.time.LocalDateTime;
 
/**
 * Tests for Restaurant. Every public method has at least one normal case and
 * one bad-input case (or a note that it takes no input).
 *
 * @author CS 2114 project team
 * @version October 2026
 */
public class RestaurantTest extends student.TestCase
{
    private Restaurant r;
 
    /**
     * Gives every test a new restaurant with no tables.
     */
    public void setUp()
    {
        r = new Restaurant();
    }
 
    /**
     * Returns the table that was added most recently.
     */
    private Table lastTable()
    {
        Table[] all = r.getTables();
 
        return all[all.length - 1];
    }
 
    /**
     * A new restaurant has no tables, nobody waiting, and the starting menu.
     */
    public void testConstructor()
    {
        assertEquals(0, r.getTables().length);
        assertTrue(r.getWaitlist().isEmpty());
        assertEquals(4, r.getMenu().length);
    }
 
    /**
     * addTable with a size and handicap seats adds a table at the end.
     */
    public void testAddTable()
    {
        assertTrue(r.addTable(4, 1));
        assertTrue(r.addTable(6, 0));
 
        Table[] all = r.getTables();
 
        assertEquals(2, all.length);
        assertEquals(4, all[0].getSize());
        assertEquals(1, all[0].getHandicapSeats());
        assertEquals(6, all[1].getSize());
        assertEquals(0, all[1].getHandicapSeats());
        assertTrue(all[0].isOpen());
    }
 
    /**
     * A size of 0, a negative size, an odd size, too many handicap seats,
     * and negative handicap seats are all refused, and no table is added.
     */
    public void testAddTableBadInput()
    {
        r.addTable(4, 0);
        assertFalse(r.addTable(0, 0));
        assertFalse(r.addTable(-2, 0));
        assertFalse(r.addTable(3, 0));
        assertFalse(r.addTable(4, 5));
        assertFalse(r.addTable(4, -1));
        assertEquals(1, r.getTables().length);
    }
 
    /**
     * addTable with only a size makes a table with no handicap seats.
     */
    public void testAddTableSizeOnly()
    {
        assertTrue(r.addTable(4));
        assertEquals(1, r.getTables().length);
        assertEquals(4, lastTable().getSize());
        assertEquals(0, lastTable().getHandicapSeats());
    }
 
    /**
     * A size of 0, a negative size, and an odd size are refused.
     */
    public void testAddTableSizeOnlyBadInput()
    {
        assertFalse(r.addTable(0));
        assertFalse(r.addTable(-4));
        assertFalse(r.addTable(5));
        assertEquals(0, r.getTables().length);
    }
 
    /**
     * A party is seated right away when a table is open and big enough.
     */
    public void testAddParty()
    {
        r.addTable(4);
 
        Party p = r.addParty("Kim", 2, false, null);
 
        assertNotNull(p);
        assertEquals("Kim", p.getHostName());
        assertEquals(2, p.getSize());
        assertFalse(lastTable().isOpen());
        assertSame(p, lastTable().getSeatedParty());
        assertTrue(r.getWaitlist().isEmpty());
    }
 
    /**
     * The party goes to the smallest open table that fits.
     */
    public void testAddPartySmallestTable()
    {
        r.addTable(8);
        r.addTable(2);
        r.addTable(4);
 
        Table[] all = r.getTables();
        Party three = r.addParty("Three", 3, false, null);
        Party two = r.addParty("Two", 2, false, null);
 
        assertSame(three, all[2].getSeatedParty());
        assertSame(two, all[1].getSeatedParty());
        assertTrue(all[0].isOpen());
    }
 
    /**
     * A taken table is skipped, so the next party moves up to a bigger one.
     */
    public void testAddPartySkipsTakenTable()
    {
        r.addTable(2);
        r.addTable(4);
 
        Table[] all = r.getTables();
        Party first = r.addParty("First", 2, false, null);
        Party second = r.addParty("Second", 2, false, null);
 
        assertSame(first, all[0].getSeatedParty());
        assertSame(second, all[1].getSeatedParty());
    }
 
    /**
     * When every table that fits is taken, the party joins the waitlist.
     */
    public void testAddPartyWaitlist()
    {
        r.addTable(4);
 
        Party first = r.addParty("First", 2, false, null);
        Party second = r.addParty("Second", 2, false, null);
 
        assertNotNull(second);
        assertSame(first, lastTable().getSeatedParty());
        assertEquals(1, r.getWaitlist().getCount());
        assertSame(second, r.getWaitlist().peekNext());
        assertEquals(1, second.getPosition());
    }
 
    /**
     * The accessibility flag and reservation time are kept on the party.
     */
    public void testAddPartyKeepsDetails()
    {
        r.addTable(4);
 
        LocalDateTime time = LocalDateTime.of(2026, 10, 20, 18, 30);
        Party p = r.addParty("Lee", 3, true, time);
 
        assertTrue(p.needAccessibility());
        assertTrue(p.hasReservation());
        assertEquals(time, p.getReservationTime());
    }
 
    /**
     * With no tables, a size of 0, a negative size, or a size bigger than
     * every table, nothing is created and the waitlist stays empty.
     */
    public void testAddPartyNeverFits()
    {
        assertNull(r.addParty("Nobody", 2, false, null));
        r.addTable(4);
        assertNull(r.addParty("Big", 5, false, null));
        assertNull(r.addParty("Zero", 0, false, null));
        assertNull(r.addParty("Negative", -1, false, null));
        assertTrue(r.getWaitlist().isEmpty());
        assertTrue(lastTable().isOpen());
    }
 
    /**
     * insertPartyAt puts a reservation into the waitlist at the position
     * asked for and moves the others back.
     */
    public void testInsertPartyAt()
    {
        r.addTable(4);
 
        Party seated = r.addParty("Seated", 2, false, null);
        Party w1 = r.addParty("W1", 2, false, null);
        Party w2 = r.addParty("W2", 2, false, null);
 
        assertTrue(r.insertPartyAt("Inserted", 2, false, null, 2));
 
        Party[] line = r.getWaitlist().getInOrder();
 
        assertSame(seated, lastTable().getSeatedParty());
        assertEquals(3, line.length);
        assertSame(w1, line[0]);
        assertEquals("Inserted", line[1].getHostName());
        assertSame(w2, line[2]);
        assertEquals(2, line[1].getPosition());
        assertEquals(3, line[2].getPosition());
    }
 
    /**
     * It also works at the front and in an empty waitlist, and the party
     * waits even when a table is open.
     */
    public void testInsertPartyAtFront()
    {
        r.addTable(4);
        assertTrue(r.insertPartyAt("Only", 2, false, null, 1));
        assertTrue(r.insertPartyAt("Front", 2, false, null, 1));
        assertEquals(2, r.getWaitlist().getCount());
        assertEquals("Front", r.getWaitlist().peekNext().getHostName());
        assertTrue(lastTable().isOpen());
    }
 
    /**
     * A party that can never fit, and a position of 0, a negative position,
     * or one past the end of the waitlist, are refused and nothing changes.
     */
    public void testInsertPartyAtBadInput()
    {
        r.addTable(4);
        assertFalse(r.insertPartyAt("Big", 5, false, null, 1));
        assertFalse(r.insertPartyAt("Zero", 0, false, null, 1));
        assertFalse(r.insertPartyAt("Early", 2, false, null, 0));
        assertFalse(r.insertPartyAt("Early", 2, false, null, -1));
        assertFalse(r.insertPartyAt("Late", 2, false, null, 2));
        assertTrue(r.getWaitlist().isEmpty());
    }
 
    /**
     * findOpenTable returns the smallest open table that is big enough.
     */
    public void testFindOpenTable()
    {
        r.addTable(8);
        r.addTable(2);
        r.addTable(4);
 
        Table[] all = r.getTables();
 
        assertSame(all[2], r.findOpenTable(3));
        assertSame(all[1], r.findOpenTable(2));
        assertSame(all[0], r.findOpenTable(5));
        assertSame(all[0], r.findOpenTable(8));
    }
 
    /**
     * A table that is taken is skipped.
     */
    public void testFindOpenTableSkipsTaken()
    {
        r.addTable(2);
        r.addTable(4);
        r.addParty("Kim", 2, false, null);
        assertSame(r.getTables()[1], r.findOpenTable(2));
    }
 
    /**
     * With no tables, a party bigger than every table, or every table
     * taken, it returns null.
     */
    public void testFindOpenTableBadInput()
    {
        assertNull(r.findOpenTable(2));
        r.addTable(4);
        assertNull(r.findOpenTable(5));
        r.addParty("Kim", 4, false, null);
        assertNull(r.findOpenTable(2));
    }
 
    /**
     * findTable finds each table by its number.
     */
    public void testFindTable()
    {
        r.addTable(2);
        r.addTable(4);
        r.addTable(6);
 
        Table[] all = r.getTables();
 
        for (int i = 0; i < all.length; i++)
        {
            assertSame(all[i], r.findTable(all[i].getTableNumber()));
        }
    }
 
    /**
     * No tables, a number that was never given out, 0, and a negative number
     * all return null.
     */
    public void testFindTableBadInput()
    {
        assertNull(r.findTable(1));
        r.addTable(4);
 
        int number = lastTable().getTableNumber();
 
        assertNull(r.findTable(number + 1000));
        assertNull(r.findTable(0));
        assertNull(r.findTable(-1));
    }
 
    /**
     * seatNext seats the first waiting party at the table and the rest
     * move up.
     */
    public void testSeatNext()
    {
        r.addTable(4);
        r.addParty("Seated", 2, false, null);
 
        Party w1 = r.addParty("W1", 2, false, null);
        Party w2 = r.addParty("W2", 3, false, null);
        int number = lastTable().getTableNumber();
 
        assertTrue(r.clearTable(number));
        assertTrue(r.seatNext(number));
        assertSame(w1, lastTable().getSeatedParty());
        assertEquals(1, r.getWaitlist().getCount());
        assertSame(w2, r.getWaitlist().peekNext());
        assertEquals(1, w2.getPosition());
    }
 
    /**
     * A party too big for the table is skipped and stays in line.
     */
    public void testSeatNextSkipsBigParty()
    {
        r.addTable(6);
        r.addTable(2);
        r.addParty("Six", 6, false, null);
        r.addParty("Two", 2, false, null);
 
        Party big = r.addParty("Three", 3, false, null);
        Party small = r.addParty("Small", 2, false, null);
        int number = r.getTables()[1].getTableNumber();
 
        r.clearTable(number);
        assertTrue(r.seatNext(number));
        assertSame(small, r.getTables()[1].getSeatedParty());
        assertSame(big, r.getWaitlist().peekNext());
        assertEquals(1, r.getWaitlist().getCount());
    }
 
    /**
     * An unknown table, an empty waitlist, and a table that is taken all
     * return false. A party that fits the taken table stays in line.
     */
    public void testSeatNextBadInput()
    {
        r.addTable(2);
        r.addTable(6);
 
        Table two = r.getTables()[0];
        int number = two.getTableNumber();
 
        assertFalse(r.seatNext(number + 1000));
        assertFalse(r.seatNext(number));
        r.addParty("Six", 6, false, null);
 
        Party seated = r.addParty("Two", 2, false, null);
        Party waiting = r.addParty("Waiting", 2, false, null);
 
        assertFalse(r.seatNext(number));
        assertSame(seated, two.getSeatedParty());
        assertEquals(1, r.getWaitlist().getCount());
        assertSame(waiting, r.getWaitlist().peekNext());
    }
 
    /**
     * When nobody in line fits the open table, it returns false, the table
     * stays open, and the line does not change.
     */
    public void testSeatNextNobodyFits()
    {
        r.addTable(2);
        r.addTable(6);
 
        Table two = r.getTables()[0];
        int number = two.getTableNumber();
 
        r.addParty("Six", 6, false, null);
        r.addParty("Two", 2, false, null);
 
        Party three = r.addParty("Three", 3, false, null);
 
        r.clearTable(number);
        assertFalse(r.seatNext(number));
        assertTrue(two.isOpen());
        assertSame(three, r.getWaitlist().peekNext());
        assertEquals(1, r.getWaitlist().getCount());
    }
 
    /**
     * clearTable empties a table that has a party.
     */
    public void testClearTable()
    {
        r.addTable(4);
        r.addParty("Kim", 2, false, null);
 
        int number = lastTable().getTableNumber();
 
        assertTrue(r.clearTable(number));
        assertTrue(lastTable().isOpen());
        assertNull(lastTable().getSeatedParty());
    }
 
    /**
     * An unknown table, an open table, and a table already cleared all
     * return false.
     */
    public void testClearTableBadInput()
    {
        r.addTable(4);
 
        int number = lastTable().getTableNumber();
 
        assertFalse(r.clearTable(number + 1000));
        assertFalse(r.clearTable(number));
        r.addParty("Kim", 2, false, null);
        assertTrue(r.clearTable(number));
        assertFalse(r.clearTable(number));
    }
 
    /**
     * addItemToTable adds menu items to the party's order.
     */
    public void testAddItemToTable()
    {
        r.addTable(4);
 
        Party p = r.addParty("Kim", 2, false, null);
        MenuItem[] menu = r.getMenu();
        int number = lastTable().getTableNumber();
 
        assertTrue(r.addItemToTable(number, menu[0]));
        assertTrue(r.addItemToTable(number, menu[3]));
        assertEquals(2, p.getOrder().length);
        assertEquals(14.50, p.getOrderTotal(), 0.001);
    }
 
    /**
     * An unknown table, an open table, and a null item return false and the
     * order stays empty.
     */
    public void testAddItemToTableBadInput()
    {
        r.addTable(4);
 
        MenuItem burger = r.getMenu()[0];
        int number = lastTable().getTableNumber();
 
        assertFalse(r.addItemToTable(number, burger));
        assertFalse(r.addItemToTable(number + 1000, burger));
 
        Party p = r.addParty("Kim", 2, false, null);
 
        assertFalse(r.addItemToTable(number, null));
        assertEquals(0, p.getOrder().length);
    }
 
    /**
     * removeItemFromTable takes one matching item out of the order.
     */
    public void testRemoveItemFromTable()
    {
        r.addTable(4);
 
        Party p = r.addParty("Kim", 2, false, null);
        MenuItem[] menu = r.getMenu();
        int number = lastTable().getTableNumber();
 
        r.addItemToTable(number, menu[0]);
        r.addItemToTable(number, menu[0]);
        r.addItemToTable(number, menu[1]);
        assertTrue(r.removeItemFromTable(number, menu[0]));
        assertEquals(2, p.getOrder().length);
        assertEquals(16.00, p.getOrderTotal(), 0.001);
    }
 
    /**
     * An unknown table, an open table, a null item, and an item that was
     * never ordered return false and the order stays the same.
     */
    public void testRemoveItemFromTableBadInput()
    {
        r.addTable(4);
 
        MenuItem[] menu = r.getMenu();
        int number = lastTable().getTableNumber();
 
        assertFalse(r.removeItemFromTable(number, menu[0]));
        assertFalse(r.removeItemFromTable(number + 1000, menu[0]));
 
        Party p = r.addParty("Kim", 2, false, null);
 
        r.addItemToTable(number, menu[0]);
        assertFalse(r.removeItemFromTable(number, null));
        assertFalse(r.removeItemFromTable(number, menu[2]));
        assertEquals(1, p.getOrder().length);
    }
 
    /**
     * getWaitlist gives the same waitlist every time, and parties that wait
     * show up in it.
     */
    public void testGetWaitlist()
    {
        r.addTable(2);
        r.addParty("Seated", 2, false, null);
 
        Party waiting = r.addParty("Waiting", 2, false, null);
 
        assertSame(r.getWaitlist(), r.getWaitlist());
        assertEquals(1, r.getWaitlist().getCount());
        assertSame(waiting, r.getWaitlist().peekNext());
    }
 
    /**
     * A new restaurant has an empty waitlist, and a party that could never
     * be seated does not end up in it.
     */
    public void testGetWaitlistBadInput()
    {
        assertTrue(r.getWaitlist().isEmpty());
        r.addTable(2);
        r.addParty("Big", 9, false, null);
        assertTrue(r.getWaitlist().isEmpty());
    }
 
    /**
     * getTables lists the tables in the order they were added.
     */
    public void testGetTables()
    {
        r.addTable(2);
        r.addTable(4);
        r.addTable(6);
 
        Table[] all = r.getTables();
 
        assertEquals(3, all.length);
        assertEquals(2, all[0].getSize());
        assertEquals(4, all[1].getSize());
        assertEquals(6, all[2].getSize());
    }
 
    /**
     * With no tables the array is empty, and changing the array that comes
     * back does not change the restaurant.
     */
    public void testGetTablesBadInput()
    {
        assertEquals(0, r.getTables().length);
        r.addTable(4);
 
        Table[] copy = r.getTables();
 
        copy[0] = null;
        assertNotNull(r.getTables()[0]);
        assertEquals(1, r.getTables().length);
    }
 
    /**
     * getMenu lists the starting menu in order with its prices.
     */
    public void testGetMenu()
    {
        MenuItem[] menu = r.getMenu();
 
        assertEquals(4, menu.length);
        assertEquals("Burger", menu[0].getName());
        assertEquals(12.00, menu[0].getPrice(), 0.001);
        assertEquals("Fries", menu[1].getName());
        assertEquals(4.00, menu[1].getPrice(), 0.001);
        assertEquals("Salad", menu[2].getName());
        assertEquals(8.00, menu[2].getPrice(), 0.001);
        assertEquals("Soda", menu[3].getName());
        assertEquals(2.50, menu[3].getPrice(), 0.001);
    }
 
    /**
     * Changing the array that comes back does not change the menu.
     */
    public void testGetMenuBadInput()
    {
        MenuItem[] menu = r.getMenu();
 
        menu[0] = null;
        assertNotNull(r.getMenu()[0]);
        assertEquals(4, r.getMenu().length);
    }
}
