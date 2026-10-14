/**
 * Tests for Table.
 *
 * @author Ethan Kahl
 * @version 10/12/26
 */
public class TableTest extends student.TestCase
{
    private Table plain;
    private Tbale accessible;
    private Party lee;
    private Party kim;

    /**
     * Gives every test an open table of 4 and a party of 3.
     */
    public void setUp()
    {
        plain = new Table(4);
        accessible = new Table(6, 2);
        lee = new Party("Lee", 2, false, null);
        kim = new Party("Kim", 4, false, null);
    }

    /**
     * Makes a party for the seat taking tests
     */
    private Party makeParty(int size, boolean needAccess)
    {
        return new Party("Guest", size, needsAccess, null);
    }

    /**
     * The constructor makes an open table.
     */
    public void testConstructor()
    {
        assertEquals(4, plain.getSize());
        assertEquals(0, plain.getHandicapSeats());
        assertTrue(plain.isOpen());
        assertNull(plain.getSeatedParty());
    }

    /**
     * Keeps handicap seat count, and 0 or all seats are allowed
     */
    public void testConstructorWithHandicapSeats()
    {
        assertEquals(6, accessible.getSize());
        assertEquals(2, accessible.getHandicapSeats());
        assertEquals(0, new Table(2, 0).getHandicapSeats());
    }

    /**
     * A size of 0, a negative size, or an odd size is rejected.
     */
    public void testConstructorBadSize()
    {
        try
        {
            new Table(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Table(-2);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Table(5);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
 
    /**
     * A negative handicap seat count, or more handicap seats than seats, is
     * rejected.
     */
    public void testConstructorBadHandicapSeats()
    {
        try
        {
            new Table(4, -1);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Table(4, 5);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
 
    /**
     * Each new table gets the next table number, and a rejected table does
     * not use up a number.
     */
    public void testGetTableNumber()
    {
        Table first = new Table(2);
 
        try
        {
            new Table(3);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        Table second = new Table(2);
 
        assertEquals(first.getTableNumber() + 1, second.getTableNumber());
    }
 
    /**
     * Two tables never share a table number. There is no input to get
     * wrong.
     */
    public void testGetTableNumberDifferent()
    {
        assertFalse(plain.getTableNumber() == accessible.getTableNumber());
    }
 
    /**
     * getHandicapSeats returns the count the table was made with.
     */
    public void testGetHandicapSeats()
    {
        assertEquals(0, plain.getHandicapSeats());
        assertEquals(2, accessible.getHandicapSeats());
    }
 
    /**
     * A rejected constructor call does not change the count on other
     * tables. There is no input to get wrong.
     */
    public void testGetHandicapSeatsUnchanged()
    {
        try
        {
            new Table(4, 9);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(2, accessible.getHandicapSeats());
        }
    }
 
    /**
     * The last seats at a table are the handicap seats.
     */
    public void testIsHandicapSeat()
    {
        assertFalse(accessible.isHandicapSeat(1));
        assertFalse(accessible.isHandicapSeat(4));
        assertTrue(accessible.isHandicapSeat(5));
        assertTrue(accessible.isHandicapSeat(6));
        assertFalse(plain.isHandicapSeat(4));
    }
 
    /**
     * Seat 0 and negative seats are not handicap seats.
     */
    public void testIsHandicapSeatBadInput()
    {
        assertFalse(accessible.isHandicapSeat(0));
        assertFalse(accessible.isHandicapSeat(-1));
        assertFalse(plain.isHandicapSeat(0));
    }
 
    /**
     * An open table has no taken seats.
     */
    public void testIsSeatTakenOpen()
    {
        for (int seat = 1; seat <= 6; seat++)
        {
            assertFalse(accessible.isSeatTaken(seat));
        }
    }
 
    /**
     * A party that does not need access fills the regular seats first.
     */
    public void testIsSeatTakenRegularParty()
    {
        accessible.seat(makeParty(2, false));
        assertTrue(accessible.isSeatTaken(1));
        assertTrue(accessible.isSeatTaken(2));
        assertFalse(accessible.isSeatTaken(3));
        assertFalse(accessible.isSeatTaken(4));
        assertFalse(accessible.isSeatTaken(5));
        assertFalse(accessible.isSeatTaken(6));
    }
 
    /**
     * A party too big for the regular seats spills into handicap seats.
     */
    public void testIsSeatTakenRegularPartySpills()
    {
        accessible.seat(makeParty(5, false));
        assertTrue(accessible.isSeatTaken(4));
        assertTrue(accessible.isSeatTaken(5));
        assertFalse(accessible.isSeatTaken(6));
    }
 
    /**
     * A party that needs access fills the handicap seats first.
     */
    public void testIsSeatTakenAccessibleParty()
    {
        accessible.seat(makeParty(1, true));
        assertFalse(accessible.isSeatTaken(1));
        assertFalse(accessible.isSeatTaken(4));
        assertTrue(accessible.isSeatTaken(5));
        assertFalse(accessible.isSeatTaken(6));
    }
 
    /**
     * An accessible party bigger than the handicap seats uses regular seats
     * for the rest, starting at seat 1.
     */
    public void testIsSeatTakenAccessiblePartySpills()
    {
        accessible.seat(makeParty(3, true));
        assertTrue(accessible.isSeatTaken(1));
        assertFalse(accessible.isSeatTaken(2));
        assertTrue(accessible.isSeatTaken(5));
        assertTrue(accessible.isSeatTaken(6));
    }
 
    /**
     * Clearing the table frees every seat again.
     */
    public void testIsSeatTakenAfterClear()
    {
        plain.seat(kim);
        assertTrue(plain.isSeatTaken(4));
        plain.clear();
        assertFalse(plain.isSeatTaken(4));
    }
 
    /**
     * getSize returns the size the table was made with.
     */
    public void testGetSize()
    {
        assertEquals(4, plain.getSize());
        assertEquals(6, accessible.getSize());
    }
 
    /**
     * A rejected setSize does not change the size. There is no other input
     * to get wrong.
     */
    public void testGetSizeAfterRejectedSet()
    {
        try
        {
            plain.setSize(3);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(4, plain.getSize());
        }
    }
 
    /**
     * setSize changes the size to another positive even number.
     */
    public void testSetSize()
    {
        plain.setSize(8);
        assertEquals(8, plain.getSize());
        plain.setSize(2);
        assertEquals(2, plain.getSize());
    }
 
    /**
     * A size of 0, a negative size, an odd size, or a size smaller than the
     * handicap seats is rejected and the size stays the same.
     */
    public void testSetSizeBadInput()
    {
        Table fourHandicap = new Table(6, 4);
 
        try
        {
            plain.setSize(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(4, plain.getSize());
        }
        try
        {
            plain.setSize(-4);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(4, plain.getSize());
        }
        try
        {
            plain.setSize(7);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(4, plain.getSize());
        }
        try
        {
            fourHandicap.setSize(2);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(6, fourHandicap.getSize());
        }
    }
 
    /**
     * getSeatedParty returns the party that was seated.
     */
    public void testGetSeatedParty()
    {
        plain.seat(lee);
        assertSame(lee, plain.getSeatedParty());
    }
 
    /**
     * An open table has no seated party, and a refused seat leaves it that
     * way.
     */
    public void testGetSeatedPartyOpen()
    {
        assertNull(plain.getSeatedParty());
        plain.seat(null);
        assertNull(plain.getSeatedParty());
    }
 
    /**
     * A new table is open.
     */
    public void testIsOpen()
    {
        assertTrue(plain.isOpen());
    }
 
    /**
     * A table with a party seated is not open. There is no input to get
     * wrong.
     */
    public void testIsOpenSeated()
    {
        plain.seat(lee);
        assertFalse(plain.isOpen());
    }
 
    /**
     * A party that is smaller than, or exactly as big as, the table fits.
     */
    public void testFits()
    {
        assertTrue(plain.fits(lee));
        assertTrue(plain.fits(kim));
    }
 
    /**
     * A party bigger than the table does not fit, and null does not fit.
     */
    public void testFitsBadInput()
    {
        assertFalse(plain.fits(makeParty(5, false)));
        assertFalse(plain.fits(null));
    }
 
    /**
     * seat puts a party at an open table and returns true.
     */
    public void testSeat()
    {
        assertTrue(plain.seat(lee));
        assertFalse(plain.isOpen());
        assertSame(lee, plain.getSeatedParty());
    }
 
    /**
     * An occupied table, a party that is too big, and null are refused and
     * the table does not change.
     */
    public void testSeatBadInput()
    {
        assertFalse(plain.seat(makeParty(5, false)));
        assertFalse(plain.seat(null));
        assertTrue(plain.isOpen());
        plain.seat(lee);
        assertFalse(plain.seat(kim));
        assertSame(lee, plain.getSeatedParty());
    }
 
    /**
     * clear empties the table and returns the party that was seated.
     */
    public void testClear()
    {
        plain.seat(lee);
        assertSame(lee, plain.clear());
        assertTrue(plain.isOpen());
    }
 
    /**
     * Clearing a table that is already open returns null.
     */
    public void testClearOpen()
    {
        assertNull(plain.clear());
        assertTrue(plain.isOpen());
    }
 
    /**
     * A table equals itself.
     */
    public void testEquals()
    {
        assertTrue(plain.equals(plain));
        assertTrue(accessible.equals(accessible));
    }
 
    /**
     * Null, another type, and a different table are not equal, even when
     * the other table has the same size.
     */
    public void testEqualsBadInput()
    {
        Table lookAlike = new Table(4);
 
        assertFalse(plain.equals(null));
        assertFalse(plain.equals("Table"));
        assertFalse(plain.equals(lee));
        assertFalse(plain.equals(lookAlike));
        assertFalse(lookAlike.equals(plain));
    }
 
    /**
     * A table gives the same hash code every time.
     */
    public void testHashCode()
    {
        assertEquals(plain.hashCode(), plain.hashCode());
    }
 
    /**
     * Different tables get different hash codes, because the hash code
     * comes from the table number. There is no input to get wrong.
     */
    public void testHashCodeDifferentTables()
    {
        assertFalse(plain.hashCode() == accessible.hashCode());
    }
 
    /**
     * An open table shows its number, size, and that it is open.
     */
    public void testToStringOpen()
    {
        assertEquals("Table " + plain.getTableNumber()
            + " (4 seats): Open", plain.toString());
    }
 
    /**
     * A seated table shows the host name of the party. There is no input to
     * get wrong.
     */
    public void testToStringSeated()
    {
        plain.seat(lee);
        assertEquals("Table " + plain.getTableNumber()
            + " (4 seats): Lee", plain.toString());
    }
}
