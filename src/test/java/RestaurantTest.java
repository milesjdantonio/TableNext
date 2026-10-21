import java.time.LocalDateTime;

/**
 * Tests for Restaurant. Every public method gets a normal case and a bad
 * input case. Methods that take no input are checked with the closest
 * edge case instead, which is said in that test's comment.
 *
 * Table numbers come from a counter that keeps going for the whole
 * program, so these tests never type a table number in by hand. They ask
 * the restaurant for its tables and use the real numbers it gives back.
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class RestaurantTest extends student.TestCase
{
    private static final double CENT = 0.001;
    private static final int MENU_SIZE = 4;

    private Restaurant cafe;
    private MenuItem burger;
    private MenuItem fries;

    /**
     * Makes a restaurant with the same three tables the real program
     * starts with: a two seater, a four seater, and a six seater.
     */
    public void setUp()
    {
        cafe = new Restaurant();
        cafe.addTable(2, 0);
        cafe.addTable(4, 2);
        cafe.addTable(6, 2);
        burger = new MenuItem("Burger", 12.00);
        fries = new MenuItem("Fries", 4.00);
    }

    /**
     * Looks up the real number of a table by where it sits in the list.
     *
     * @param index 0 for the two seater, 1 for the four, 2 for the six
     * @return the table number the restaurant gave that table
     */
    private int numberOf(int index)
    {
        Table[] tables = cafe.getTables();

        return tables[index].getTableNumber();
    }

    /**
     * Finds a table number that no table in this restaurant has, so the
     * bad input tests have something safe to ask for.
     */
    private int missingNumber()
    {
        Table[] tables = cafe.getTables();
        int highest = 0;

        for (int index = 0; index < tables.length; index++)
        {
            if (tables[index].getTableNumber() > highest)
            {
                highest = tables[index].getTableNumber();
            }
        }

        return highest + 1;
    }

    /**
     * A new restaurant has no tables and nobody waiting, but it does come
     * with the four item food menu.
     */
    public void testConstructor()
    {
        Restaurant empty = new Restaurant();

        assertEquals(0, empty.getTables().length);
        assertTrue(empty.getWaitlist().isEmpty());
        assertEquals(MENU_SIZE, empty.getMenu().length);
    }

    /**
     * Tables with a valid size are added in the order they were given.
     */
    public void testAddTable()
    {
        Table[] tables = cafe.getTables();

        assertEquals(3, tables.length);
        assertEquals(2, tables[0].getSize());
        assertEquals(4, tables[1].getSize());
        assertEquals(6, tables[2].getSize());
        assertEquals(2, tables[1].getHandicapSeats());
    }

    /**
     * The one argument version makes a table with no accessible seats.
     */
    public void testAddTableSizeOnly()
    {
        assertTrue(cafe.addTable(8));

        Table[] tables = cafe.getTables();

        assertEquals(4, tables.length);
        assertEquals(8, tables[3].getSize());
        assertEquals(0, tables[3].getHandicapSeats());
    }

    /**
     * A bad size or a bad accessible seat count gives back false instead
     * of crashing, and no table is added.
     */
    public void testAddTableBadInput()
    {
        assertFalse(cafe.addTable(3));
        assertFalse(cafe.addTable(0));
        assertFalse(cafe.addTable(-2));
        assertFalse(cafe.addTable(4, 5));
        assertFalse(cafe.addTable(4, -1));
        assertEquals(3, cafe.getTables().length);
    }

    /**
     * A party that fits an open table is sat down right away, at the
     * smallest table that can hold it.
     */
    public void testAddPartySeatsImmediately()
    {
        Party kim = cafe.addParty("Kim", 2, false, null);

        assertNotNull(kim);
        assertEquals(0, kim.getPosition());
        assertTrue(cafe.getWaitlist().isEmpty());
        assertEquals(kim, cafe.getTables()[0].getSeatedParty());
    }

    /**
     * Each new party takes the smallest table still open, so the second
     * party of two moves up to the four seater.
     */
    public void testAddPartyUsesSmallestOpenTable()
    {
        cafe.addParty("First", 2, false, null);

        Party second = cafe.addParty("Second", 2, false, null);

        assertEquals(second, cafe.getTables()[1].getSeatedParty());
    }

    /**
     * Once every table is full the next party joins the waitlist instead.
     */
    public void testAddPartyGoesToWaitlist()
    {
        cafe.addParty("First", 2, false, null);
        cafe.addParty("Second", 2, false, null);
        cafe.addParty("Third", 2, false, null);

        Party fourth = cafe.addParty("Fourth", 2, false, null);

        assertNotNull(fourth);
        assertEquals(1, fourth.getPosition());
        assertEquals(1, cafe.getWaitlist().getCount());
    }

    /**
     * A reservation time is handed on to the party that gets made.
     */
    public void testAddPartyWithReservation()
    {
        LocalDateTime sevenPm =
            LocalDateTime.of(2026, 10, 20, 19, 0);

        Party lee = cafe.addParty("Lee", 2, true, sevenPm);

        assertTrue(lee.hasReservation());
        assertEquals(sevenPm, lee.getReservationTime());
        assertTrue(lee.needAccessibility());
    }

    /**
     * A party too big for every table, or a size of zero or less, is
     * turned away and nobody joins the waitlist.
     */
    public void testAddPartyBadInput()
    {
        assertNull(cafe.addParty("Crowd", 7, false, null));
        assertNull(cafe.addParty("Nobody", 0, false, null));
        assertNull(cafe.addParty("Negative", -3, false, null));
        assertTrue(cafe.getWaitlist().isEmpty());
    }

    /**
     * A restaurant with no tables cannot take any party at all.
     */
    public void testAddPartyNoTables()
    {
        Restaurant empty = new Restaurant();

        assertNull(empty.addParty("Kim", 2, false, null));
    }

    /**
     * A reservation goes onto the waitlist at the spot that was asked
     * for, even while tables are still open.
     */
    public void testInsertPartyAt()
    {
        assertTrue(cafe.insertPartyAt("Kim", 2, false, null, 1));
        assertTrue(cafe.insertPartyAt("Lee", 4, false, null, 1));

        Party[] waiting = cafe.getWaitlist().getInOrder();

        assertEquals(2, waiting.length);
        assertEquals("Lee", waiting[0].getHostName());
        assertEquals("Kim", waiting[1].getHostName());
        assertTrue(cafe.getTables()[0].isOpen());
    }

    /**
     * A party too big for every table, or a position that is not on the
     * line, is refused and the waitlist does not change.
     */
    public void testInsertPartyAtBadInput()
    {
        assertFalse(cafe.insertPartyAt("Crowd", 9, false, null, 1));
        assertFalse(cafe.insertPartyAt("Kim", 2, false, null, 0));
        assertFalse(cafe.insertPartyAt("Kim", 2, false, null, 5));
        assertTrue(cafe.getWaitlist().isEmpty());
    }

    /**
     * findOpenTable gives the smallest open table that can hold the
     * party.
     */
    public void testFindOpenTable()
    {
        assertEquals(2, cafe.findOpenTable(2).getSize());
        assertEquals(4, cafe.findOpenTable(3).getSize());
        assertEquals(6, cafe.findOpenTable(5).getSize());
    }

    /**
     * A party bigger than every table has no open table to go to, and
     * neither does a party looking at a restaurant that is full.
     */
    public void testFindOpenTableBadInput()
    {
        assertNull(cafe.findOpenTable(7));

        cafe.addParty("First", 2, false, null);
        cafe.addParty("Second", 2, false, null);
        cafe.addParty("Third", 2, false, null);

        assertNull(cafe.findOpenTable(2));
    }

    /**
     * findTable picks out the table with the number that was asked for.
     */
    public void testFindTable()
    {
        Table found = cafe.findTable(numberOf(1));

        assertNotNull(found);
        assertEquals(4, found.getSize());
    }

    /**
     * A table number that does not belong to any table gives back null.
     */
    public void testFindTableBadInput()
    {
        assertNull(cafe.findTable(missingNumber()));
        assertNull(cafe.findTable(0));
        assertNull(cafe.findTable(-1));
    }

    /**
     * seatNext takes the first waiting party that fits and sits it down.
     */
    public void testSeatNext()
    {
        cafe.addParty("First", 2, false, null);
        cafe.addParty("Second", 2, false, null);
        cafe.addParty("Third", 2, false, null);
        cafe.addParty("Waiting", 2, false, null);

        cafe.clearTable(numberOf(0));

        assertTrue(cafe.seatNext(numberOf(0)));
        assertTrue(cafe.getWaitlist().isEmpty());
        assertEquals("Waiting",
            cafe.findTable(numberOf(0))
                .getSeatedParty().getHostName());
    }

    /**
     * A party too big for the table is skipped and a smaller party
     * further back in the line is seated instead.
     */
    public void testSeatNextSkipsPartyThatDoesNotFit()
    {
        cafe.insertPartyAt("Big", 6, false, null, 1);
        cafe.insertPartyAt("Small", 2, false, null, 2);

        assertTrue(cafe.seatNext(numberOf(0)));
        assertEquals("Small",
            cafe.findTable(numberOf(0))
                .getSeatedParty().getHostName());
        assertEquals(1, cafe.getWaitlist().getCount());
    }

    /**
     * An unknown table, a table that is already taken, an empty waitlist,
     * and a line where nobody fits all give back false.
     */
    public void testSeatNextBadInput()
    {
        assertFalse(cafe.seatNext(missingNumber()));
        assertFalse(cafe.seatNext(numberOf(0)));

        cafe.insertPartyAt("Big", 6, false, null, 1);

        assertFalse(cafe.seatNext(numberOf(0)));
        assertEquals(1, cafe.getWaitlist().getCount());

        cafe.addParty("Sitting", 2, false, null);

        assertFalse(cafe.seatNext(numberOf(0)));
    }

    /**
     * Clearing a table that has somebody at it opens the table back up.
     */
    public void testClearTable()
    {
        cafe.addParty("Kim", 2, false, null);

        assertTrue(cafe.clearTable(numberOf(0)));
        assertTrue(cafe.findTable(numberOf(0)).isOpen());
    }

    /**
     * Clearing a table that is already open, or one that does not exist,
     * gives back false.
     */
    public void testClearTableBadInput()
    {
        assertFalse(cafe.clearTable(numberOf(0)));
        assertFalse(cafe.clearTable(missingNumber()));
    }

    /**
     * An item added to a table lands on the seated party's order.
     */
    public void testAddItemToTable()
    {
        Party kim = cafe.addParty("Kim", 2, false, null);

        assertTrue(cafe.addItemToTable(numberOf(0), burger));
        assertEquals(1, kim.getOrder().length);
        assertEquals(12.00, kim.getOrderTotal(), CENT);
    }

    /**
     * An unknown table, an open table, and a null item are all refused.
     */
    public void testAddItemToTableBadInput()
    {
        assertFalse(cafe.addItemToTable(missingNumber(), burger));
        assertFalse(cafe.addItemToTable(numberOf(0), burger));

        cafe.addParty("Kim", 2, false, null);

        assertFalse(cafe.addItemToTable(numberOf(0), null));
    }

    /**
     * Removing an item takes it back off the seated party's order.
     */
    public void testRemoveItemFromTable()
    {
        Party kim = cafe.addParty("Kim", 2, false, null);

        cafe.addItemToTable(numberOf(0), burger);
        cafe.addItemToTable(numberOf(0), fries);

        assertTrue(cafe.removeItemFromTable(numberOf(0), burger));
        assertEquals(1, kim.getOrder().length);
        assertEquals(4.00, kim.getOrderTotal(), CENT);
    }

    /**
     * An unknown table, an open table, a null item, and an item that was
     * never ordered are all refused.
     */
    public void testRemoveItemFromTableBadInput()
    {
        assertFalse(cafe.removeItemFromTable(missingNumber(), burger));
        assertFalse(cafe.removeItemFromTable(numberOf(0), burger));

        cafe.addParty("Kim", 2, false, null);

        assertFalse(cafe.removeItemFromTable(numberOf(0), null));
        assertFalse(cafe.removeItemFromTable(numberOf(0), fries));
    }

    /**
     * getWaitlist takes no input. It hands back the real waitlist, so a
     * party added through the restaurant shows up on it.
     */
    public void testGetWaitlist()
    {
        assertNotNull(cafe.getWaitlist());
        assertTrue(cafe.getWaitlist().isEmpty());

        cafe.insertPartyAt("Kim", 2, false, null, 1);

        assertEquals(1, cafe.getWaitlist().getCount());
    }

    /**
     * getTables takes no input. It gives back a new array each time, so
     * changing that array does not change the restaurant.
     */
    public void testGetTables()
    {
        Table[] firstCopy = cafe.getTables();

        firstCopy[0] = null;

        Table[] secondCopy = cafe.getTables();

        assertEquals(3, secondCopy.length);
        assertNotNull(secondCopy[0]);
    }

    /**
     * The edge case for getTables is a restaurant with none, which gives
     * back an empty array rather than null.
     */
    public void testGetTablesWhenEmpty()
    {
        Restaurant empty = new Restaurant();
        Table[] tables = empty.getTables();

        assertNotNull(tables);
        assertEquals(0, tables.length);
    }

    /**
     * getMenu takes no input. It lists the four food items, and it gives
     * back a new array each time.
     */
    public void testGetMenu()
    {
        MenuItem[] menu = cafe.getMenu();

        assertEquals(MENU_SIZE, menu.length);
        assertEquals("Burger", menu[0].getName());
        assertEquals("Fries", menu[1].getName());
        assertEquals("Salad", menu[2].getName());
        assertEquals("Soda", menu[3].getName());
        assertEquals(12.00, menu[0].getPrice(), CENT);
        assertEquals(4.00, menu[1].getPrice(), CENT);
        assertEquals(8.00, menu[2].getPrice(), CENT);
        assertEquals(2.50, menu[3].getPrice(), CENT);

        menu[0] = null;

        assertNotNull(cafe.getMenu()[0]);
    }

    /**
     * A party that needs accessibility skips the table with no accessible
     * seats, even when that table is the smallest one that fits.
     */
    public void testAddPartyAccessibleSkipsPlainTable()
    {
        Party party = cafe.addParty("Kim", 2, true, null);

        assertEquals(4, cafe.findTable(numberOf(1)).getSize());
        assertSame(party,
            cafe.findTable(numberOf(1)).getSeatedParty());
        assertTrue(cafe.findTable(numberOf(0)).isOpen());
    }

    /**
     * An accessible party is turned away when no table has accessible
     * seats.
     */
    public void testAddPartyAccessibleBadInput()
    {
        Restaurant plain = new Restaurant();
        plain.addTable(4, 0);

        assertNull(plain.addParty("Kim", 2, true, null));
        assertFalse(plain.insertPartyAt("Kim", 2, true, null, 1));
    }

    /**
     * seatNext does not seat an accessible party at a table with no
     * accessible seats. The party stays on the waitlist and can be
     * seated once a suitable accessible table is cleared.
     */
    public void testSeatNextSkipsAccessibleParty()
    {
        cafe.addParty("A", 4, false, null);
        cafe.addParty("B", 6, false, null);
        cafe.addParty("C", 2, false, null);

        Party waiting = cafe.addParty("D", 2, true, null);

        assertNotNull(waiting);
        assertEquals(1, cafe.getWaitlist().getCount());

        // Confirm that the plain table exists and is successfully cleared.
        // Otherwise a missing table could make seatNext return false
        // without actually checking accessibility.
        assertTrue(cafe.clearTable(numberOf(0)));
        assertTrue(cafe.findTable(numberOf(0)).isOpen());

        assertFalse(cafe.seatNext(numberOf(0)));
        assertEquals(1, cafe.getWaitlist().getCount());
        assertSame(waiting, cafe.getWaitlist().peekNext());

        // The same waiting party can use a table with accessible seats.
        assertTrue(cafe.clearTable(numberOf(1)));
        assertTrue(cafe.seatNext(numberOf(1)));
        assertSame(waiting,
            cafe.findTable(numberOf(1)).getSeatedParty());
        assertTrue(cafe.getWaitlist().isEmpty());
    }

    /**
     * A plain table skips an accessible party and seats the next fitting
     * party that does not need accessibility. The skipped party remains
     * first in line and its position is still correct.
     */
    public void testSeatNextSkipsAccessibleAndSeatsPlainParty()
    {
        cafe.addParty("A", 4, false, null);
        cafe.addParty("B", 6, false, null);
        cafe.addParty("C", 2, false, null);

        Party accessible = cafe.addParty("D", 2, true, null);
        Party plain = cafe.addParty("E", 2, false, null);

        assertEquals(2, cafe.getWaitlist().getCount());
        assertTrue(cafe.clearTable(numberOf(0)));
        assertTrue(cafe.seatNext(numberOf(0)));

        assertSame(plain,
            cafe.findTable(numberOf(0)).getSeatedParty());
        assertSame(accessible, cafe.getWaitlist().peekNext());
        assertEquals(1, accessible.getPosition());
    }
}
