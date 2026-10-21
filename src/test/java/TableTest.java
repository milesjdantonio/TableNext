/**
 * Tests for Table. Every public method gets a normal case and a bad input
 * case. Methods that take no input are checked with the closest edge case
 * instead, which is said in that test's comment.
 *
 * Table hands out its number from a counter that keeps going for the whole
 * program, so no test here checks for an exact table number. They check
 * that two tables made one after the other are one apart, or they build
 * the expected text out of getTableNumber().
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class TableTest extends student.TestCase
{
    private Table small;
    private Table accessible;
    private Party couple;
    private Party needsRamp;

    /**
     * Makes a plain two seat table, a four seat table with two accessible
     * seats, and two parties to sit at them.
     */
    public void setUp()
    {
        small = new Table(2);
        accessible = new Table(4, 2);
        couple = new Party("Kim", 2, false, null);
        needsRamp = new Party("Lee", 2, true, null);
    }

    /**
     * The one argument constructor makes an open table with no accessible
     * seats.
     */
    public void testConstructorSizeOnly()
    {
        assertEquals(2, small.getSize());
        assertEquals(0, small.getHandicapSeats());
        assertTrue(small.isOpen());
        assertNull(small.getSeatedParty());
    }

    /**
     * The two argument constructor stores the accessible seat count.
     */
    public void testConstructorWithHandicapSeats()
    {
        assertEquals(4, accessible.getSize());
        assertEquals(2, accessible.getHandicapSeats());
        assertTrue(accessible.isOpen());
    }

    /**
     * A size that is odd, zero, or negative is refused, because the chairs
     * are drawn in two equal rows.
     */
    public void testConstructorBadSize()
    {
        Exception odd = null;
        Exception zero = null;
        Exception negative = null;

        try
        {
            new Table(3);
        }
        catch (IllegalArgumentException error)
        {
            odd = error;
        }
        try
        {
            new Table(0);
        }
        catch (IllegalArgumentException error)
        {
            zero = error;
        }
        try
        {
            new Table(-4);
        }
        catch (IllegalArgumentException error)
        {
            negative = error;
        }
        assertNotNull(odd);
        assertNotNull(zero);
        assertNotNull(negative);
    }

    /**
     * An accessible seat count below zero or above the table size is
     * refused.
     */
    public void testConstructorBadHandicapSeats()
    {
        Exception negative = null;
        Exception tooMany = null;

        try
        {
            new Table(4, -1);
        }
        catch (IllegalArgumentException error)
        {
            negative = error;
        }
        try
        {
            new Table(4, 5);
        }
        catch (IllegalArgumentException error)
        {
            tooMany = error;
        }
        assertNotNull(negative);
        assertNotNull(tooMany);
    }

    /**
     * getTableNumber takes no input. Two tables made one after the other
     * are one apart, and a table that was refused does not use up a
     * number.
     */
    public void testGetTableNumber()
    {
        Table first = new Table(2);

        try
        {
            new Table(3);
        }
        catch (IllegalArgumentException error)
        {
            // The refused table should not take a number.
        }
        Table second = new Table(2);

        assertEquals(first.getTableNumber() + 1, second.getTableNumber());
    }

    /**
     * getSize takes no input, so the edge case is the smallest table the
     * class allows, which is two seats.
     */
    public void testGetSize()
    {
        assertEquals(4, accessible.getSize());
        assertEquals(2, small.getSize());
    }

    /**
     * getHandicapSeats takes no input, so the edge case is a table that
     * has none.
     */
    public void testGetHandicapSeats()
    {
        assertEquals(2, accessible.getHandicapSeats());
        assertEquals(0, small.getHandicapSeats());
    }

    /**
     * setSize makes the table bigger.
     */
    public void testSetSize()
    {
        accessible.setSize(6);

        assertEquals(6, accessible.getSize());
    }

    /**
     * An odd size is refused and the old size is kept.
     */
    public void testSetSizeBadInput()
    {
        Exception thrown = null;

        try
        {
            accessible.setSize(5);
        }
        catch (IllegalArgumentException error)
        {
            thrown = error;
        }
        assertNotNull(thrown);
        assertEquals(4, accessible.getSize());
    }

    /**
     * The accessible seats are the last seats at the table, so on a four
     * seat table with two of them, seats three and four are accessible.
     */
    public void testIsHandicapSeat()
    {
        assertFalse(accessible.isHandicapSeat(1));
        assertFalse(accessible.isHandicapSeat(2));
        assertTrue(accessible.isHandicapSeat(3));
        assertTrue(accessible.isHandicapSeat(4));
    }

    /**
     * A table with no accessible seats has none at any of its real seats.
     */
    public void testIsHandicapSeatNone()
    {
        assertFalse(small.isHandicapSeat(1));
        assertFalse(small.isHandicapSeat(2));
    }

    /**
     * Nobody is sitting at an open table, so no seat is taken.
     */
    public void testIsSeatTakenWhenOpen()
    {
        assertFalse(accessible.isSeatTaken(1));
        assertFalse(accessible.isSeatTaken(3));
    }

    /**
     * A party that does not need accessible seating fills the regular
     * seats first and leaves the accessible ones free.
     */
    public void testIsSeatTakenRegularParty()
    {
        accessible.seat(couple);

        assertTrue(accessible.isSeatTaken(1));
        assertTrue(accessible.isSeatTaken(2));
        assertFalse(accessible.isSeatTaken(3));
        assertFalse(accessible.isSeatTaken(4));
    }

    /**
     * A party that needs accessible seating fills the accessible seats
     * first and leaves the regular ones free.
     */
    public void testIsSeatTakenAccessibleParty()
    {
        accessible.seat(needsRamp);

        assertFalse(accessible.isSeatTaken(1));
        assertFalse(accessible.isSeatTaken(2));
        assertTrue(accessible.isSeatTaken(3));
        assertTrue(accessible.isSeatTaken(4));
    }

    /**
     * getSeatedParty takes no input. It is null while the table is open
     * and the party once somebody sits down.
     */
    public void testGetSeatedParty()
    {
        assertNull(accessible.getSeatedParty());

        accessible.seat(couple);

        assertEquals(couple, accessible.getSeatedParty());
    }

    /**
     * isOpen takes no input. It is true for a new table and false once a
     * party is seated.
     */
    public void testIsOpen()
    {
        assertTrue(accessible.isOpen());

        accessible.seat(couple);

        assertFalse(accessible.isOpen());
    }

    /**
     * A party fits when the table has at least as many seats as the party
     * has people.
     */
    public void testFits()
    {
        Party four = new Party("Four", 4, false, null);

        assertTrue(accessible.fits(couple));
        assertTrue(accessible.fits(four));
    }

    /**
     * A null party and a party with too many people do not fit.
     */
    public void testFitsBadInput()
    {
        Party crowd = new Party("Crowd", 5, false, null);

        assertFalse(accessible.fits(null));
        assertFalse(accessible.fits(crowd));
    }

    /**
     * Seating a party that fits at an open table works.
     */
    public void testSeat()
    {
        assertTrue(accessible.seat(couple));
        assertFalse(accessible.isOpen());
        assertEquals(couple, accessible.getSeatedParty());
    }

    /**
     * A null party, a party that is too big, and a table that is already
     * taken are all refused, and the first party stays put.
     */
    public void testSeatBadInput()
    {
        Party crowd = new Party("Crowd", 5, false, null);

        assertFalse(accessible.seat(null));
        assertFalse(accessible.seat(crowd));

        assertTrue(accessible.seat(couple));
        assertFalse(accessible.seat(needsRamp));
        assertEquals(couple, accessible.getSeatedParty());
    }

    /**
     * clear takes no input. It hands back the party that was sitting there
     * and leaves the table open.
     */
    public void testClear()
    {
        accessible.seat(couple);

        assertEquals(couple, accessible.clear());
        assertTrue(accessible.isOpen());
    }

    /**
     * The edge case for clear is a table nobody was sitting at, which
     * gives back null.
     */
    public void testClearWhenOpen()
    {
        assertNull(accessible.clear());
        assertTrue(accessible.isOpen());
    }

    /**
     * A table is equal to itself and not to a different table, even when
     * both were built the same way.
     */
    public void testEquals()
    {
        Table twin = new Table(4, 2);

        assertTrue(accessible.equals(accessible));
        assertFalse(accessible.equals(twin));
    }

    /**
     * Null and another kind of object are refused.
     */
    public void testEqualsBadInput()
    {
        assertFalse(accessible.equals(null));
        assertFalse(accessible.equals("Table"));
    }

    /**
     * hashCode takes no input. The same table gives the same number every
     * time, and two different tables do not share one.
     */
    public void testHashCode()
    {
        assertEquals(accessible.hashCode(), accessible.hashCode());
        assertFalse(accessible.hashCode() == small.hashCode());
    }

    /**
     * toString shows the number, the seat count, and Open while nobody is
     * sitting there.
     */
    public void testToStringOpen()
    {
        String expected = "Table " + accessible.getTableNumber()
            + " (4 seats): Open";

        assertEquals(expected, accessible.toString());
    }

    /**
     * Once a party sits down, toString shows the host name instead of
     * Open.
     */
    public void testToStringSeated()
    {
        accessible.seat(couple);

        String expected = "Table " + accessible.getTableNumber()
            + " (4 seats): Kim";

        assertEquals(expected, accessible.toString());
    }
    
    /** Direct seating must enforce the party's accessibility needs. */
    public void testSeatRequiresAccessibleTable() {
        Table regular = new Table(4, 0);
        Party party = new Party("Accessible", 2, true, null);

        assertFalse(regular.seat(party));
        assertTrue(regular.isOpen());

        Table suitable = new Table(4, 1);
        assertTrue(suitable.seat(party));
        assertSame(party, suitable.getSeatedParty());
    }

    /** Seat queries must reject numbers outside the table's capacity. */
    public void testSeatQueriesOutsideTable() {
        Table table = new Table(4, 2);
        assertTrue(table.seat(new Party("Kim", 4, false, null)));

        assertFalse(table.isSeatTaken(0));
        assertFalse(table.isSeatTaken(-1));
        assertFalse(table.isSeatTaken(5));

        assertFalse(table.isHandicapSeat(0));
        assertFalse(table.isHandicapSeat(5));

        assertTrue(table.isSeatTaken(4));
        assertTrue(table.isHandicapSeat(4));
    }
}
