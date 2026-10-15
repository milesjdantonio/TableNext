/**
 * Tests for Waitlist. Every public method gets a normal case and a bad
 * input case. Methods that take no input are checked with the closest
 * edge case instead, which is said in that test's comment.
 *
 * Positions in the waitlist start at one, so after every change the
 * parties should be numbered 1, 2, 3 and so on with no gaps.
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class WaitlistTest extends student.TestCase
{
    private Waitlist line;
    private Party one;
    private Party two;
    private Party three;
    private Party big;

    /**
     * Makes an empty waitlist and four parties that are not on it yet.
     */
    public void setUp()
    {
        line = new Waitlist();
        one = new Party("One", 2, false, null);
        two = new Party("Two", 3, false, null);
        three = new Party("Three", 4, false, null);
        big = new Party("Big", 8, false, null);
    }

    /**
     * Checks that the parties on the list are numbered 1, 2, 3 and so on.
     */
    private void assertPositionsInOrder()
    {
        Party[] waiting = line.getInOrder();

        for (int index = 0; index < waiting.length; index++)
        {
            assertEquals(index + 1, waiting[index].getPosition());
        }
    }

    /**
     * A brand new waitlist has nobody on it.
     */
    public void testConstructor()
    {
        assertTrue(line.isEmpty());
        assertEquals(0, line.getCount());
        assertEquals(0, line.getInOrder().length);
    }

    /**
     * Parties join the back of the line and get the next position.
     */
    public void testAddParty()
    {
        assertTrue(line.addParty(one));
        assertTrue(line.addParty(two));

        assertEquals(2, line.getCount());
        assertEquals(1, one.getPosition());
        assertEquals(2, two.getPosition());
        assertPositionsInOrder();
    }

    /**
     * A null party and a party that is already waiting are both refused.
     */
    public void testAddPartyBadInput()
    {
        line.addParty(one);

        assertFalse(line.addParty(null));
        assertFalse(line.addParty(one));
        assertEquals(1, line.getCount());
    }

    /**
     * A party can be slid into the middle of the line, and everybody
     * behind it moves back one spot.
     */
    public void testInsertAtMiddle()
    {
        line.addParty(one);
        line.addParty(two);

        assertTrue(line.insertAt(three, 2));

        Party[] waiting = line.getInOrder();

        assertEquals(3, waiting.length);
        assertEquals("One", waiting[0].getHostName());
        assertEquals("Three", waiting[1].getHostName());
        assertEquals("Two", waiting[2].getHostName());
        assertPositionsInOrder();
    }

    /**
     * Inserting at position one puts the party at the front, and
     * inserting one past the end puts it at the back.
     */
    public void testInsertAtFrontAndBack()
    {
        assertTrue(line.insertAt(one, 1));
        assertTrue(line.insertAt(two, 1));
        assertTrue(line.insertAt(three, 3));

        Party[] waiting = line.getInOrder();

        assertEquals("Two", waiting[0].getHostName());
        assertEquals("One", waiting[1].getHostName());
        assertEquals("Three", waiting[2].getHostName());
        assertPositionsInOrder();
    }

    /**
     * A null party, a repeat party, a position below one, and a position
     * past the end of the line are all refused.
     */
    public void testInsertAtBadInput()
    {
        line.addParty(one);

        assertFalse(line.insertAt(null, 1));
        assertFalse(line.insertAt(one, 1));
        assertFalse(line.insertAt(two, 0));
        assertFalse(line.insertAt(two, -3));
        assertFalse(line.insertAt(two, 3));
        assertEquals(1, line.getCount());
    }

    /**
     * Removing a party hands it back and closes the gap behind it.
     */
    public void testRemoveAt()
    {
        line.addParty(one);
        line.addParty(two);
        line.addParty(three);

        assertEquals(two, line.removeAt(2));

        Party[] waiting = line.getInOrder();

        assertEquals(2, waiting.length);
        assertEquals("One", waiting[0].getHostName());
        assertEquals("Three", waiting[1].getHostName());
        assertPositionsInOrder();
    }

    /**
     * A position below one, or past the end of the line, gives back null
     * and leaves the line alone.
     */
    public void testRemoveAtBadInput()
    {
        line.addParty(one);

        assertNull(line.removeAt(0));
        assertNull(line.removeAt(-1));
        assertNull(line.removeAt(2));
        assertEquals(1, line.getCount());
    }

    /**
     * Removing from an empty waitlist gives back null.
     */
    public void testRemoveAtWhenEmpty()
    {
        assertNull(line.removeAt(1));
        assertTrue(line.isEmpty());
    }

    /**
     * peekNext takes no input. It shows the party at the front without
     * taking it off the line.
     */
    public void testPeekNext()
    {
        line.addParty(one);
        line.addParty(two);

        assertEquals(one, line.peekNext());
        assertEquals(2, line.getCount());
    }

    /**
     * The edge case for peekNext is an empty waitlist, which gives back
     * null.
     */
    public void testPeekNextWhenEmpty()
    {
        assertNull(line.peekNext());
    }

    /**
     * The first party small enough for the table is taken off the line,
     * even when a bigger party is waiting ahead of it.
     */
    public void testRemoveFirstThatFits()
    {
        line.addParty(big);
        line.addParty(one);
        line.addParty(two);

        assertEquals(one, line.removeFirstThatFits(2));
        assertEquals(2, line.getCount());
        assertPositionsInOrder();
    }

    /**
     * When nobody waiting is small enough, nothing is removed. An empty
     * waitlist gives back null too.
     */
    public void testRemoveFirstThatFitsBadInput()
    {
        assertNull(line.removeFirstThatFits(4));

        line.addParty(big);

        assertNull(line.removeFirstThatFits(4));
        assertNull(line.removeFirstThatFits(0));
        assertEquals(1, line.getCount());
    }

    /**
     * getInOrder takes no input. It lists the parties front to back and
     * gives back a new array, so changing that array does not change the
     * real line.
     */
    public void testGetInOrder()
    {
        line.addParty(one);
        line.addParty(two);

        Party[] firstCopy = line.getInOrder();

        firstCopy[0] = null;

        Party[] secondCopy = line.getInOrder();

        assertEquals(2, secondCopy.length);
        assertEquals("One", secondCopy[0].getHostName());
        assertEquals("Two", secondCopy[1].getHostName());
    }

    /**
     * The edge case for getInOrder is an empty waitlist, which gives back
     * an empty array rather than null.
     */
    public void testGetInOrderWhenEmpty()
    {
        Party[] waiting = line.getInOrder();

        assertNotNull(waiting);
        assertEquals(0, waiting.length);
    }

    /**
     * getCount takes no input. It goes up as parties join and back down
     * as they leave.
     */
    public void testGetCount()
    {
        assertEquals(0, line.getCount());

        line.addParty(one);
        line.addParty(two);
        assertEquals(2, line.getCount());

        line.removeAt(1);
        assertEquals(1, line.getCount());
    }

    /**
     * isEmpty takes no input. It is true for a new waitlist, false once
     * somebody joins, and true again after everybody leaves.
     */
    public void testIsEmpty()
    {
        assertTrue(line.isEmpty());

        line.addParty(one);
        assertFalse(line.isEmpty());

        line.removeAt(1);
        assertTrue(line.isEmpty());
    }
}
