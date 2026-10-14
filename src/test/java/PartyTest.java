imprt java.time.LocalDateTime;

/**
 * Tests for Party. Every public method has at least one normal
 * case and one bad input case
 *
 * @author CS 2114 project team
 * @version 10/12/26
 */
public class PartyTest extends student.TestCase
{
    private LocalDateTime dinnerTime;
    private Party walkIn;
    private Party reserved;
    private MenuItem burger;
    private MenuItem fries;

    /**
     * Gives every test a party and two menu items.
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
     * The constructor makes a party with an empty order.
     */
    public void testConstructor()
    {
        assertEquals("Kim", walkIn.getHostName());
        assertEquals(3, walkIn.getSize());
        assertFalse(walkIn.needAccesibility());
        assertFalse(walkIn.hasReservation());
        assertNull(walkIn.getReservationTime());
        assertEquals(0, walkIn.getPosition());
        assertEquals(0, walkIn.getOrder().length);

    }
    /**
     * A party with a reservation time keeps it.
     */

    public void testConstructorWithReservation() {

        assertTrue(reserved.needAccesibility());
        assertTrue(reserved.hasReservation());
        assertEquals(dinnerTime, reserved.getReservationTime());

    }

    /**
     * A size of 0 or -2, or a blank or null name, is rejected.
     */
    public void testConstructorBadInput()
    {
        try
        {
            new Party(null, 2, false, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Party("", 2, false, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Party("   ", 2, false, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Party("Moe", 0, false, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Party("Moe", -2, false, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    /**
     * Each new party gets the next id
     * so two parties never share one. 
     */
    public void testGetId()
    {
        Party first = new Party("A", 1, false, null);
        Party second = new Party("B", 1, false, null);

        assertEquals(first.getId() + 1, second.getID());
        assertFalse(first.getId() == second.getId());

    }

    /**
     * getId takes no imput, so a failed constructor is the 
     * only bad case
     */
    public void testGetIdAfterFailedConstructor()
    {
        Party first = new Party("A", 1, false, null);

        try
        {
            new Party("", 1, false, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            //expected
        }
        Party second = new Party("B", 1, false, null);

        assertEquals(first.getId() + 1, second.getId());
    }

    /**
     * getHostName returns the name the party was made with
     */
    public void testGetHostName()
    {
        assertEquals("Kim", walkIn.getHostName());
        assertEquals("Lee", reserved.getHostName());
    }

    /**
     * getSize returns the size the party was made with
     */
    public void testGetSize()
    {
        assertEquals(3, walkIn.getSize());
        assertEquals(2, reserved.getSize())
    }

    /**
     * setSize changes the size
     */
    public void testSetSize()
    {
        walkIn.setSize(5);
        assertEquals(5, walkIn.getSize());
    }

    /**
     * A size of 0 or -1 is rejected and the size stays the same
     */
    public void testSetSizeBadInput()
    {
        try
        {
            walkIn.setSize(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(3, walkIn.getSize());
        }
        try
        {
            walkIn.setSize(-1);
            fail("Expected IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            assertEquals(3, walkIn.getSize());
        }
    }

    /**
     * needAccessibility reports the flag the party was made with.
     * There is no bad input
     */
    public void testNeedAccessibility()
    {
        assertFalse(walkIn.needAccesibility());
        assertTrue(reserved.needAccesibility());
    }

    /**
     * setNeedAccesibility truns the flag on and off. 
     * It takes a boolean.
     */
    public void testSetNeedAccessiblity()
    {
        walkIn.setNeedAccesibility(true);
        assertTrue(walkIn.needAccesibility());
        walkIn.setNeedAccesibility(false);
        assertFalse(walkIn.needAccesibility());
    }

    /**
     * hasReservation is true only when there is a reservation time. 
     */
    public void testHasReservation()
    {
        assertTrue(reserved.hasReservation());
        assertFalse(walkIn.hasReservation());
    }

    /**
     * hasReservation stays the same after the reservation time
     * is moved. A walk in stays a walk in. 
     */
    public void testHasReservationAfterUpdate()
    {
        reserved.updateReservationTime(true);
        walk.updateReservationTime(true);
        assertTrue(reserved.hasReservation());
        assertFalse(walkIn.hasReservation());

    }

    /**
     * getReservationTime returns the time the party was made with
     */
    public void testGetReservationTime()
    {
        assertEquals(dinnerTime, reserved.getReservationTime());
    }

    /**
     * A walk in party has no reservation time
     */
    public void testGetReservationTimeWalkIn()
    {
        assertNull(walkIn.getReservationTime());

    }

    /**
     * updateReservationTime moves the time one hour later
     * or one hour earlier and returns true.
     */
    public void testUpdateReservationTime()
    {
        assertTrue(reserved.updateReservationTime(true));
        assertEquals(dinnerTime.plusHours(1), 
        reserved.getReservationTime());
        assertTrue(reserved.updateReservationTime(false));
        assertTrue(reserved.updateReservationTime(false));
        assertEquals(dinnerTime.minusHours(1),
        reserved.getReservationTime());
    }

    /**
     * A walk in has no time to move, so it returns
     * false and stays without a time
     */
    public void testUpdateReservationTimeWalkIn()
    {
        assertFalse(walk.updateReservationTime(true));
        assertFalse(walk.updateReservationTime(false));
        assertNull(walk.getReservationTime());
    }

    /**
     * A new party has position 0, and getPosition returns
     * what setPosition stored
     */
    public void testGetPosition()
    {
        assertEquals(0, walkIn.getPosition());
        walkIn.setPosition(2);
        assertEquals(2, walk.getPosition());
    }

    /**
     * getPosition takes no input. A rejected setPosition
     * does not change it. 
     */
    public void testGetPositionAfterRejectedSet()
    {
        walkIn.setPosition(4);
        try
        {
            walkIn.setPosition(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(4, walkIn.getPosition());
        }
    }

    /**
     * setPosition updates the position.
     */
    public void testSetPosition()
    {
        walkIn.setPosition(1);
        assertEquals(1, walkIn.getPosition());
        walkIn.setPosition(7);
        assertEquals(7, walkIn.getPosition());

    }

    /**
     * A position of 0 or -1 is rejected and the position stays the same.
     */
    public void testSetPositionBadInput()
    {
        walkIn.setPosition(2);
        try
        {
            walkIn.setPosition(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(2, walkIn.getPosition());
        }
        try
        {
            walkIn.setPosition(-1);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(2, walkIn.getPosition());
        }
    }

    /**
     * addItem puts an item in the order.
     */
    public void testAddItem()
    {
        assertTrue(walkIn.addItem(burger));
        assertEquals(1, walkIn.getOrder().length);
    }

    /**
     * addItem puts each new item at the end of the order. 
     */
    public void testAddItemKeepsOrder()
    {
        walkIn.addItem(burger);
        walkIn.addItem(fries);

        MenuItem[] items = walkIn.getOrder();

        assertSame(burger, items[0]);
        assertSame(fries, items[1]);
    }

    /**
     * Adding null returns false and the order is unchanged.
     */
    public void testAddItemNull()
    {
        walkIn.addItem(burger);
        assertFalse(walkIn.addItem(null));
        assertEquals(1, walkIn.getOrder().length);
    }

    /**
     * removeItem takes out one of two equal items.
     */
    public void testRemoveItem()
    {
        walkIn.addItem(burger);
        walkIn.addItem(burger);
        assertTrue(walkIn.removeItem(burger));
        assertEquals(1, walkIn.getOrder().length);
        assertSame(burger, walkIn.getOrder()[0]);
    }

    /**
     * Removing from an empty order returns false. 
     */
    public void testRemoveItemEmptyOrder()
    {
        assertFalse(walkIn.removeItem(burger));
        assertEquals(0, walkIn.getOrder().length);
    }

    /**
     * Removing from an empty order, or removing an item that was not
     * ordered, returns false.
     */
    public void testRemoveItemBadInput()
    {
        walkIn.addItem(fries);
        assertFalse(walkIn.removeItem(burger));
        assertFalse(walkIn.removeItem(null));
        assertEquals(1, walkIn.getOrder().length);
    }

    /**
     * getOrder returns the items in the order
     * they were added
     * 
     */
    public void testGetOrder()
    {
        walkIn.addItem(burger);
        walkIn.addItem(fries);

        MenuItem[] items = walkIn.getOrder();

        assertEquals(2, items.length);
        assertSame(burger, items[0]);
        assertEquals(fries, items[1]);
    }

    /**
     * An empty order gives an array with no entries.
     */
    public void testGetOrderEmpty()
    {
        assertEquals(0, walkIn.getOrder().length);
    }

    /**
     * getOrderTotal adds up the prices.
     */
    public void testGetOrderTotal()
    {
        walkIn.addItem(burger);
        walkIn.addItem(fries);
        assertEquals(16.00, walkIn.getOrderTotal(), 0.001);
    }

    /**
     * An empty order totals 0.00.
     */
    public void testGetOrderTotalEmpty()
    {
        assertEquals(0.0, walkIn.getOrderTotal(), 0.001);
    }

    /**
     * Parties with the same id are equal, even if the names differ.
     */
    public void testEquals()
    {
        assertTrue(walkIn.equals(walkIn));
        assertTrue(reserved.equals(reserved));
    }

    /**
     * Null, another type, and a different id are not equal.
     */
    public void testEqualsBadInput()
    {
        Party lookAlike = new Party("Kim", 3, false, null);

        assertFalse(walkIn.equals(null));
        assertFalse(walkIn.equals("Kim"));
        assertFalse(walkIn.equals(burger));
        assertFalse(walkIn.equals(lookAlike));
        assertFalse(lookAlike.equals(walkIn));

    }

    /**
     * A party gives the same hash code very time
     */
    public void testHashCode()
    {
        assertEquals(walkIn.hashCode(), walkIn.hashCode());
    }

    /**
     * Different parties get different hash codes, 
     * because the hash code comes from the id.
     * There is no input to get wrong. 
     */
    public void testHashCodeDifferentParties()
    {
        assertFalse(walkIn.hashCode() == reserved.hashCode());
    }

    /**
     * toString shows position, name, size, and reservation. There is no bad
     * input.
     */
    public void testToString()
    {
        walkIn.setPosition(2);
        walkIn.addItem(burger);

        String text = walkIn.toString();

        assertTrue(text.contains("Host Name; Kim"));
        assertTrue(text.contains("Id: " + walkIn.getId()));
        assertTrue(text.contains("Size: 3"));
        assertTrue(text.contains("Position: 2"));
        assertTrue(text.constains("Reservation Time: null"));
        assertTrue(text.contains("Order: [" + burger.toString() + "]"));
    }

    /**
     * A reserved party shows its time, and an empty order
     * shows empty. 
     */
    public void testToStringReserved()
    {
        String text = reserved.toString();

        assertTrue(text.contains("Reservation Time: " + dinnerTime));
        assertTrue(text.contains("Order: []"));
    }
}
