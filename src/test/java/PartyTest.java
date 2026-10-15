import java.time.LocalDateTime;

/**
 * Tests for Party. Every public method gets a normal case and a bad input
 * case. Methods that take no input are checked with the closest edge case
 * instead, which is said in that test's comment.
 *
 * Party hands out id numbers from a counter that keeps going for the whole
 * program, so no test here checks for an exact id. They check that two
 * parties made one after the other are one apart.
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class PartyTest extends student.TestCase
{
    private static final double CENT = 0.001;

    private LocalDateTime dinnerTime;
    private Party walkIn;
    private Party reserved;
    private MenuItem burger;
    private MenuItem fries;

    /**
     * Makes two parties and two menu items for the tests to use.
     */
    public void setUp()
    {
        dinnerTime = LocalDateTime.of(2026, 10, 14, 18, 30);
        walkIn = new Party("Kim", 3, false, null);
        reserved = new Party("Lee", 2, true, dinnerTime);
        burger = new MenuItem("Burger", 12.00);
        fries = new MenuItem("Fries", 4.00);
    }

    /**
     * A walk-in party keeps its name and size, has no reservation, is not
     * on the waitlist yet, and has nothing ordered.
     */
    public void testConstructor()
    {
        assertEquals("Kim", walkIn.getHostName());
        assertEquals(3, walkIn.getSize());
        assertFalse(walkIn.needAccessibility());
        assertFalse(walkIn.hasReservation());
        assertNull(walkIn.getReservationTime());
        assertEquals(0, walkIn.getPosition());
        assertEquals(0, walkIn.getOrder().length);
    }

    /**
     * A party built with a time keeps that time and needs accessibility.
     */
    public void testConstructorWithReservation()
    {
        assertTrue(reserved.needAccessibility());
        assertTrue(reserved.hasReservation());
        assertEquals(dinnerTime, reserved.getReservationTime());
    }

    /**
     * A null or blank host name is refused.
     */
    public void testConstructorBadName()
    {
        Exception nullName = null;
        Exception blankName = null;

        try
        {
            new Party(null, 2, false, null);
        }
        catch (IllegalArgumentException error)
        {
            nullName = error;
        }
        try
        {
            new Party("   ", 2, false, null);
        }
        catch (IllegalArgumentException error)
        {
            blankName = error;
        }
        assertNotNull(nullName);
        assertNotNull(blankName);
    }

    /**
     * A size of zero or less is refused.
     */
    public void testConstructorBadSize()
    {
        Exception zeroSize = null;
        Exception negativeSize = null;

        try
        {
            new Party("Kim", 0, false, null);
        }
        catch (IllegalArgumentException error)
        {
            zeroSize = error;
        }
        try
        {
            new Party("Kim", -2, false, null);
        }
        catch (IllegalArgumentException error)
        {
            negativeSize = error;
        }
        assertNotNull(zeroSize);
        assertNotNull(negativeSize);
    }

    /**
     * getId takes no input. Two parties made one after the other get ids
     * one apart, and a party that was refused does not use up an id.
     */
    public void testGetId()
    {
        Party first = new Party("First", 2, false, null);

        try
        {
            new Party("", 2, false, null);
        }
        catch (IllegalArgumentException error)
        {
            // The refused party should not take an id number.
        }
        Party second = new Party("Second", 2, false, null);

        assertEquals(first.getId() + 1, second.getId());
    }

    /**
     * getHostName takes no input, so the edge case is a one letter name.
     */
    public void testGetHostName()
    {
        Party shortName = new Party("A", 2, false, null);

        assertEquals("Kim", walkIn.getHostName());
        assertEquals("A", shortName.getHostName());
    }

    /**
     * getSize takes no input, so the edge case is a party of one.
     */
    public void testGetSize()
    {
        Party single = new Party("Solo", 1, false, null);

        assertEquals(3, walkIn.getSize());
        assertEquals(1, single.getSize());
    }

    /**
     * setSize changes the size.
     */
    public void testSetSize()
    {
        walkIn.setSize(5);

        assertEquals(5, walkIn.getSize());
    }

    /**
     * A size of zero or less is refused and the old size is kept.
     */
    public void testSetSizeBadInput()
    {
        Exception thrown = null;

        try
        {
            walkIn.setSize(0);
        }
        catch (IllegalArgumentException error)
        {
            thrown = error;
        }
        assertNotNull(thrown);
        assertEquals(3, walkIn.getSize());
    }

    /**
     * needAccessibility takes no input. It is false for a walk-in and true
     * for the party that asked for it.
     */
    public void testNeedAccessibility()
    {
        assertFalse(walkIn.needAccessibility());
        assertTrue(reserved.needAccessibility());
    }

    /**
     * setNeedAccessibility can turn the flag on and back off again.
     */
    public void testSetNeedAccessibility()
    {
        walkIn.setNeedAccessibility(true);
        assertTrue(walkIn.needAccessibility());

        walkIn.setNeedAccessibility(false);
        assertFalse(walkIn.needAccessibility());
    }

    /**
     * hasReservation takes no input. It is true only when a time was given.
     */
    public void testHasReservation()
    {
        assertTrue(reserved.hasReservation());
        assertFalse(walkIn.hasReservation());
    }

    /**
     * getReservationTime takes no input. It gives the time back, or null
     * when the party walked in without one.
     */
    public void testGetReservationTime()
    {
        assertEquals(dinnerTime, reserved.getReservationTime());
        assertNull(walkIn.getReservationTime());
    }

    /**
     * Moving a reservation later adds an hour and moving it earlier takes
     * an hour away.
     */
    public void testUpdateReservationTime()
    {
        assertTrue(reserved.updateReservationTime(true));
        assertEquals(dinnerTime.plusHours(1), reserved.getReservationTime());

        assertTrue(reserved.updateReservationTime(false));
        assertEquals(dinnerTime, reserved.getReservationTime());
    }

    /**
     * A party with no reservation cannot have its time moved.
     */
    public void testUpdateReservationTimeBadInput()
    {
        assertFalse(walkIn.updateReservationTime(true));
        assertNull(walkIn.getReservationTime());
    }

    /**
     * getPosition takes no input. A new party is not on the waitlist, so
     * its position starts at zero.
     */
    public void testGetPosition()
    {
        assertEquals(0, walkIn.getPosition());
        walkIn.setPosition(2);
        assertEquals(2, walkIn.getPosition());
    }

    /**
     * setPosition stores a spot in line.
     */
    public void testSetPosition()
    {
        walkIn.setPosition(1);

        assertEquals(1, walkIn.getPosition());
    }

    /**
     * A position below one is refused and the old position is kept.
     */
    public void testSetPositionBadInput()
    {
        Exception zero = null;
        Exception negative = null;

        try
        {
            walkIn.setPosition(0);
        }
        catch (IllegalArgumentException error)
        {
            zero = error;
        }
        try
        {
            walkIn.setPosition(-1);
        }
        catch (IllegalArgumentException error)
        {
            negative = error;
        }
        assertNotNull(zero);
        assertNotNull(negative);
        assertEquals(0, walkIn.getPosition());
    }

    /**
     * addItem puts items on the order in the order they were added.
     */
    public void testAddItem()
    {
        assertTrue(walkIn.addItem(burger));
        assertTrue(walkIn.addItem(fries));

        MenuItem[] order = walkIn.getOrder();

        assertEquals(2, order.length);
        assertEquals("Burger", order[0].getName());
        assertEquals("Fries", order[1].getName());
    }

    /**
     * A null item is refused and nothing is added to the order.
     */
    public void testAddItemBadInput()
    {
        assertFalse(walkIn.addItem(null));
        assertEquals(0, walkIn.getOrder().length);
    }

    /**
     * removeItem takes one matching item back off the order.
     */
    public void testRemoveItem()
    {
        walkIn.addItem(burger);
        walkIn.addItem(fries);

        assertTrue(walkIn.removeItem(burger));

        MenuItem[] order = walkIn.getOrder();

        assertEquals(1, order.length);
        assertEquals("Fries", order[0].getName());
    }

    /**
     * Removing null, or an item that was never ordered, does nothing.
     */
    public void testRemoveItemBadInput()
    {
        walkIn.addItem(burger);

        assertFalse(walkIn.removeItem(null));
        assertFalse(walkIn.removeItem(fries));
        assertEquals(1, walkIn.getOrder().length);
    }

    /**
     * getOrder takes no input. It gives back a new array each time, so
     * changing that array does not change the real order.
     */
    public void testGetOrder()
    {
        walkIn.addItem(burger);

        MenuItem[] firstCopy = walkIn.getOrder();

        firstCopy[0] = null;

        MenuItem[] secondCopy = walkIn.getOrder();

        assertEquals(1, secondCopy.length);
        assertNotNull(secondCopy[0]);
    }

    /**
     * getOrderTotal takes no input. It adds up the prices, and the edge
     * case is an order with nothing on it.
     */
    public void testGetOrderTotal()
    {
        assertEquals(0.00, walkIn.getOrderTotal(), CENT);

        walkIn.addItem(burger);
        walkIn.addItem(fries);

        assertEquals(16.00, walkIn.getOrderTotal(), CENT);
    }

    /**
     * A party is equal to itself and not to a different party, even when
     * the two were built with the same name and size.
     */
    public void testEquals()
    {
        Party twin = new Party("Kim", 3, false, null);

        assertTrue(walkIn.equals(walkIn));
        assertFalse(walkIn.equals(twin));
    }

    /**
     * Null and another kind of object are refused.
     */
    public void testEqualsBadInput()
    {
        assertFalse(walkIn.equals(null));
        assertFalse(walkIn.equals("Kim"));
    }

    /**
     * hashCode takes no input. The same party gives the same number every
     * time, and two different parties do not share one.
     */
    public void testHashCode()
    {
        assertEquals(walkIn.hashCode(), walkIn.hashCode());
        assertFalse(walkIn.hashCode() == reserved.hashCode());
    }

    /**
     * toString takes no input. It lists the party's details, and the edge
     * case is a party with no reservation, which shows null for the time.
     */
    public void testToString()
    {
        String text = walkIn.toString();

        assertTrue(text.contains("Host Name: Kim"));
        assertTrue(text.contains("Size: 3"));
        assertTrue(text.contains("Position: 0"));
        assertTrue(text.contains("Reservation Time: null"));
    }
}
