/**
 * Tests for Waitlist.
 *
 * @author Ethan Kahl
 * @version 10/13/26
 */
public class WaitlistTest extends student.TestCase
{
    private Waitlist list;
    private Party p1;
    private Party p2;
    private Party p3;
    private Party p4;

    /**
     * Gives every test an empty waitlist and four parties of size 2.
     */
    public void setUp()
    {
        list = new Waitlist();
        p1 = new Party(1, "One", 2, false, null);
        p2 = new Party(2, "Two", 2, false, null);
        p3 = new Party(3, "Three", 2, false, null);
        p4 = new Party(4, "Four", 2, false, null);
    }

    /**
     * Adds the first three parties in order.
     */
    private void fillThree()
    {
        list.addParty(p1);
        list.addParty(p2);
        list.addParty(p3);
    }

    /**
     * Checks that the parties in the list have positions 1, 2, 3, and so
     * on, in order.
     */
    private void assertPositionsInOrder()
    {
        Party[] order = list.getInOrder();
 
        for (int i = 0; i < order.length; i++)
        {
            assertEquals(i + 1, order[i].getPosition());
        }
    }

    /**
     * The constructor makes an empty waitlist.
     */
    public void testConstructor()
    {
        assertTrue(list.isEmpty());
        assertEquals(0, list.getCount());
        assertNull(list.peekNext());
    }

    /**
     * addParty adds to the end and gives the next position.
     */
    public void testAddParty()
    {
        list.addParty(p1);
        list.addParty(p2);
        assertTrue(list.addParty(p3));
        assertEquals(3, p3.getPosition());
        assertEquals(3, list.getCount());
        assertSame(p3, list.getInOrder()[2]);
    }

    /**
     * Adding the same party twice, or null, returns false.
     */
    public void testAddPartyBadInput()
    {
        fillThree();
        assertFalse(list.addParty(p2));
        assertFalse(list.addParty(null));
        assertEquals(3, list.getCount());
        assertEquals(2, p2.getPosition());
        assertPositionsInOrder();
    }

    /**
     * A party that was removed can be added again at the end.
     */
    public void testAddPartyAfterRemoval()
    {
        fillThree();
        list.removeAt(1);
        assertTrue(list.addParty(p1));
        assertEquals(3, p1.getPosition());
        assertPositionsInOrder();
    }

    /**
     * insertAt puts a party in the middle and shifts the rest back.
     */
    public void testInsertAt()
    {
        fillThree();
        assertTrue(list.insertAt(p4, 2));

        Party[] order = list.getInOrder();

        assertSame(p1, order[0]);
        assertSame(p4, order[1]);
        assertSame(p2, order[2]);
        assertSame(p3, order[3]);
        assertEquals(2, p4.getPosition());
        assertEquals(3, p2.getPosition());
        assertEquals(4, p3.getPosition());
    }

   /**
     * insertAt works at the front, at the very end, and in an empty list.
     */
    public void testInsertAtEdges()
    {
        assertTrue(list.insertAt(p1, 1));
        assertTrue(list.insertAt(p2, 1));
        assertTrue(list.insertAt(p3, 3));
        assertSame(p2, list.getInOrder()[0]);
        assertSame(p1, list.getInOrder()[1]);
        assertSame(p3, list.getInOrder()[2]);
        assertPositionsInOrder();
    }

    /**
     * Position 0, position 6 in a list of 3, null, and a party that is
     * already waiting are all refused and nothing changes.
     */
    public void testInsertAtBadInput()
    {
        fillThree();
        assertFalse(list.insertAt(p4, 0));
        assertFalse(list.insertAt(p4, -1));
        assertFalse(list.insertAt(p4, 5));
        assertFalse(list.insertAt(null, 2));
        assertFalse(list.insertAt(p2, 1));
        assertEquals(3, list.getCount());
        assertSame(p1, list.getInOrder()[0]);
        assertSame(p2, list.getInOrder()[1]);
        assertSame(p3, list.getInOrder()[2]);
        assertPositionsInOrder();
    }

    /**
     * removeAt takes out the party at a position and moves the rest up.
     */
    public void testRemoveAt()
    {
        fillThree();
        list.addParty(p4);
        assertEquals(p2, list.removeAt(2));
        assertEquals(3, list.getCount());
        assertEquals(2, p3.getPosition());
        assertEquals(3, p4.getPosition());
        assertPositionsInOrder();
    }

    /**
     * removeAt works on the first and on the last party.
     */
    public void testRemoveAtEdges()
    {
        fillThree();
        assertSame(p3, list.removeAt(3));
        assertSame(p1, list.removeAt(1));
        assertEquals(1, list.getCount());
        assertSame(p2, list.getInOrder()[0]);
        assertEquals(1, p2.getPosition());
    }

    /**
     * Removing position 9 of 4, position 0, or from an empty list returns
     * null and nothing changes.
     */
    public void testRemoveAtBadInput()
    {
        assertNull(list.removeAt(1));
        fillThree();
        list.addParty(p4);
        assertNull(list.removeAt(9));
        assertNull(list.removeAt(0));
        assertNull(list.removeAt(-1));
        assertNull(list.removeAt(5));
        assertEquals(4, list.getCount());
        assertPositionsInOrder();
    }

    /**
     * peekNext shows the first party and leaves it in line.
     */
    public void testPeekNext()
    {
        fillThree();
        assertEquals(p1, list.peekNext());
        assertEquals(3, list.getCount());
        list.removeAt(1);
        assertSame(p2, list.peekNext());
    }

    /**
     * peekNext on an empty list returns null.
     */
    public void testPeekNextEmpty()
    {
        assertNull(list.peekNext());
        list.addParty(p1);
        list.removeAt(1);
        assertNull(list.peekNext());
    }

    /**
     * removeFirstThatFits skips parties that are too big. With a capacity
     * of 4 and parties of 6, 3, and 2, the party of 3 is taken and the
     * party of 2 moves up to position 2.
     */
    public void testRemoveFirstThatFits()
    {
        Party six = new Party("Six", 6, false, null);
        Party three = new Party("Three", 3, false, null);
        Party two = new Party("Two", 2, false, null);
 
        list.addParty(six);
        list.addParty(three);
        list.addParty(two);
        assertSame(three, list.removeFirstThatFits(4));
        assertEquals(2, list.getCount());
        assertEquals(1, six.getPosition());
        assertEquals(2, two.getPosition());
        assertPositionsInOrder();
    }

    /**
     * A party exactly as big as the capacity fits, and when the first party
     * fits it is the one taken.
     */
    public void testRemoveFirstThatFitsExact()
    {
        list.addParty(p1);
        list.addParty(new Party("Four", 4, false, null));
        assertSame(p1, list.removeFirstThatFits(2));
 
        Party exact = list.removeFirstThatFits(4);
 
        assertEquals("Four", exact.getHostName());
        assertTrue(list.isEmpty());
    }

    /**
     * When nobody fits, when the list is empty, or when the capacity is 0
     * or negative, it returns null and nothing changes.
     */
    public void testRemoveFirstThatFitsBadInput()
    {
        assertNull(list.removeFirstThatFits(4));
        list.addParty(new Party("Six", 6, false, null));
        list.addParty(new Party("Four", 4, false, null));
        assertNull(list.removeFirstThatFits(2));
        assertNull(list.removeFirstThatFits(0));
        assertNull(list.removeFirstThatFits(-3));
        assertEquals(2, list.getCount());
        assertPositionsInOrder();
    }

    /**
     * getInOrder returns the parties by position.
     */
    public void testGetInOrder()
    {
        fillThree();
        list.insertAt(p4, 1);

        Party[] order = list.getInOrder();

        assertEquals(4, order.length);
        assertSame(p4, order[0]);
        assertSame(p1, order[1]);
        assertSame(p2, order[2]);
        assertSame(p3, order[3]);
    }

    /**
     * An empty list gives an array with no entries, and changing the array
     * that comes back does not change the waitlist.
     */
    public void testGetInOrderEmptyAndCopy()
    {
        assertEquals(0, list.getInOrder().length);
        fillThree();
 
        Party[] copy = list.getInOrder();
 
        copy[0] = null;
        assertSame(p1, list.getInOrder()[0]);
        assertEquals(3, list.getCount());
    }

    /**
     * getCount counts the waiting parties.
     */
    public void testGetCount()
    {
        fillThree();
        assertEquals(3, list.getCount());
        list.removeAt(2);
        assertEquals(2, list.getCount());
    }

    /**
     * An empty list has a count of 0, and a refused add does not change
     * the count.
     */
    public void testGetCountBadInput()
    {
        assertEquals(0, list.getCount());
        list.addParty(null);
        assertEquals(0, list.getCount());
    }

    /**
     * A list with one party is not empty.
     */
    public void testIsEmpty()
    {
        list.addParty(p1);
        assertFalse(list.isEmpty());
    }

    /**
     * A new list is empty, and a refused add leaves it empty.
     */
    public void testIsEmptyBadInput()
    {
        assertTrue(list.isEmpty());
        list.addParty(null);
        assertTrue(list.isEmpty());
    }
}
