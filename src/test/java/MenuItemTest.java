/**
 * Tests for MenuItem.
 *
 * @author Ethan Kahl
 * @version 10/13/26
 */
public class MenuItemTest extends student.TestCase
{
    private MenuItem burger;

    /**
     * Gives every test a burger at 12.00.
     */
    public void setUp()
    {
        burger = new MenuItem("Burger", 12.00);
    }

    /**
     * The constructor keeps the name and price.
     */
    public void testConstructor()
    {
        assertEquals("Burger", burger.getName());
        assertEquals(12.00, burger.getPrice(), 0.001);
    }

    /**
     * A free item (price 0) is allowed.
     */
    public void testConstructorFreeItem()
    {
        MenuItem water = new MenuItem("Water", 0.0);
 
        assertEquals(0.0, water.getPrice(), 0.001);
    }

    /**
     * A blank name or a negative price is rejected.
     */
    public void testConstructorBadInput()
    {
        try
        {
            new MenuItem(null, 5.00);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new MenuItem("", 5.00);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new MenuItem("   ", 5.00);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new MenuItem("Soda", -1.00);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    /**
     * getName returns the name the item was made with. There is no bad
     * input.
     */
    public void testGetName()
    {
        assertEquals("Burger", burger.getName());
        assertEquals("Fries", new MenuItem("Fries", 4.00).getName());
    }
 
    /**
     * getPrice returns the price the item was made with. There is no bad
     * input.
     */
    public void testGetPrice()
    {
        assertEquals(12.00, burger.getPrice(), 0.001);
        assertEquals(4.50, new MenuItem("Fries", 4.50).getPrice(), 0.001);
    }
 
    /**
     * setName changes the name.
     */
    public void testSetName()
    {
        burger.setName("Cheeseburger");
        assertEquals("Cheeseburger", burger.getName());
    }
 
    /**
     * A null, empty, or blank name is rejected and the name stays the same.
     */
    public void testSetNameBadInput()
    {
        try
        {
            burger.setName(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Burger", burger.getName());
        }
        try
        {
            burger.setName("");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Burger", burger.getName());
        }
        try
        {
            burger.setName("   ");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Burger", burger.getName());
        }
    }
 
    /**
     * setPrice changes the price, including down to 0.
     */
    public void testSetPrice()
    {
        burger.setPrice(10.50);
        assertEquals(10.50, burger.getPrice(), 0.001);
        burger.setPrice(0.0);
        assertEquals(0.0, burger.getPrice(), 0.001);
    }
 
    /**
     * A negative price is rejected and the price stays the same.
     */
    public void testSetPriceBadInput()
    {
        try
        {
            burger.setPrice(-1.00);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(12.00, burger.getPrice(), 0.001);
        }
    }
 
    /**
     * Two items with the same name and price are equal, and an item equals
     * itself. Names are compared without caring about upper or lower case.
     */
    public void testEquals()
    {
        assertTrue(burger.equals(burger));
        assertTrue(burger.equals(new MenuItem("Burger", 12.00)));
        assertTrue(burger.equals(new MenuItem("BURGER", 12.00)));
        assertTrue(burger.equals(new MenuItem("burger", 12.00)));
    }
 
    /**
     * Null, another type, a different name, and a different price are not
     * equal.
     */
    public void testEqualsBadInput()
    {
        assertFalse(burger.equals(null));
        assertFalse(burger.equals("Burger"));
        assertFalse(burger.equals(new Party("Kim", 2, false, null)));
        assertFalse(burger.equals(new MenuItem("Fries", 12.00)));
        assertFalse(burger.equals(new MenuItem("Burger", 11.99)));
    }
 
    /**
     * Items that are equal give the same hash code, even when the names
     * differ in case.
     */
    public void testHashCode()
    {
        assertEquals(burger.hashCode(), burger.hashCode());
        assertEquals(burger.hashCode(),
            new MenuItem("Burger", 12.00).hashCode());
        assertEquals(burger.hashCode(),
            new MenuItem("bUrGeR", 12.00).hashCode());
    }
 
    /**
     * Items with a different name or price get different hash codes in
     * these examples. There is no input to get wrong.
     */
    public void testHashCodeDifferentItems()
    {
        assertFalse(burger.hashCode()
            == new MenuItem("Fries", 12.00).hashCode());
        assertFalse(burger.hashCode()
            == new MenuItem("Burger", 4.00).hashCode());
    }
 
    /**
     * toString shows the name and the price with two decimals.
     */
    public void testToString()
    {
        assertEquals("Burger $12.00", burger.toString());
        assertEquals("Soda $2.50", new MenuItem("Soda", 2.5).toString());
    }
 
    /**
     * A free item shows $0.00. There is no input to get wrong.
     */
    public void testToStringFree()
    {
        assertEquals("Water $0.00", new MenuItem("Water", 0).toString());
    }
}
