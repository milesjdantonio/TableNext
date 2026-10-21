/**
 * Tests for MenuItem. Every public method gets a normal case and a bad
 * input case. Methods that take no input are checked with the closest
 * edge case instead, which is said in that test's comment.
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class MenuItemTest extends student.TestCase
{
    private static final double CENT = 0.001;

    private MenuItem burger;
    private MenuItem fries;

    /**
     * Makes two menu items for the tests to use.
     */
    public void setUp()
    {
        burger = new MenuItem("Burger", 12.00);
        fries = new MenuItem("Fries", 4.00);
    }

    /**
     * A new item keeps the name and price it was built with.
     */
    public void testConstructor()
    {
        assertEquals("Burger", burger.getName());
        assertEquals(12.00, burger.getPrice(), CENT);
    }

    /**
     * A null name is refused.
     */
    public void testConstructorNullName()
    {
        Exception thrown = null;

        try
        {
            new MenuItem(null, 5.00);
        }
        catch (IllegalArgumentException error)
        {
            thrown = error;
        }
        assertNotNull(thrown);
    }

    /**
     * An empty name and a name of only spaces are both refused.
     */
    public void testConstructorBlankName()
    {
        Exception emptyName = null;
        Exception spacesOnly = null;

        try
        {
            new MenuItem("", 5.00);
        }
        catch (IllegalArgumentException error)
        {
            emptyName = error;
        }
        try
        {
            new MenuItem("   ", 5.00);
        }
        catch (IllegalArgumentException error)
        {
            spacesOnly = error;
        }
        assertNotNull(emptyName);
        assertNotNull(spacesOnly);
    }

    /**
     * A negative price is refused.
     */
    public void testConstructorNegativePrice()
    {
        Exception thrown = null;

        try
        {
            new MenuItem("Water", -1.00);
        }
        catch (IllegalArgumentException error)
        {
            thrown = error;
        }
        assertNotNull(thrown);
    }

    /**
     * A price of zero is allowed, since some items are free.
     */
    public void testConstructorZeroPrice()
    {
        MenuItem water = new MenuItem("Water", 0.00);

        assertEquals(0.00, water.getPrice(), CENT);
    }

    /**
     * getName takes no input, so the edge case here is reading the name
     * again after setName changed it.
     */
    public void testGetName()
    {
        assertEquals("Fries", fries.getName());
        fries.setName("Curly Fries");
        assertEquals("Curly Fries", fries.getName());
    }

    /**
     * getPrice takes no input, so the edge case here is a price of zero.
     */
    public void testGetPrice()
    {
        MenuItem water = new MenuItem("Water", 0.00);

        assertEquals(4.00, fries.getPrice(), CENT);
        assertEquals(0.00, water.getPrice(), CENT);
    }

    /**
     * setName replaces the name and leaves the price alone.
     */
    public void testSetName()
    {
        burger.setName("Double Burger");

        assertEquals("Double Burger", burger.getName());
        assertEquals(12.00, burger.getPrice(), CENT);
    }

    /**
     * A null or blank name is refused and the old name is kept.
     */
    public void testSetNameBadInput()
    {
        Exception nullName = null;
        Exception blankName = null;

        try
        {
            burger.setName(null);
        }
        catch (IllegalArgumentException error)
        {
            nullName = error;
        }
        try
        {
            burger.setName("  ");
        }
        catch (IllegalArgumentException error)
        {
            blankName = error;
        }
        assertNotNull(nullName);
        assertNotNull(blankName);
        assertEquals("Burger", burger.getName());
    }

    /**
     * setPrice replaces the price and leaves the name alone.
     */
    public void testSetPrice()
    {
        burger.setPrice(13.50);

        assertEquals(13.50, burger.getPrice(), CENT);
        assertEquals("Burger", burger.getName());
    }

    /**
     * A negative price is refused and the old price is kept.
     */
    public void testSetPriceBadInput()
    {
        Exception thrown = null;

        try
        {
            burger.setPrice(-0.01);
        }
        catch (IllegalArgumentException error)
        {
            thrown = error;
        }
        assertNotNull(thrown);
        assertEquals(12.00, burger.getPrice(), CENT);
    }

    /**
     * Two items with the same name and price are equal.
     */
    public void testEqualsSameValues()
    {
        MenuItem copy = new MenuItem("Burger", 12.00);

        assertTrue(burger.equals(copy));
    }

    /**
     * The name is compared without caring about capital letters.
     */
    public void testEqualsIgnoresCase()
    {
        MenuItem shouting = new MenuItem("BURGER", 12.00);

        assertTrue(burger.equals(shouting));
    }

    /**
     * An item is equal to itself.
     */
    public void testEqualsSameObject()
    {
        assertTrue(burger.equals(burger));
    }

    /**
     * The same name at a different price is not equal.
     */
    public void testEqualsDifferentPrice()
    {
        MenuItem pricey = new MenuItem("Burger", 15.00);

        assertFalse(burger.equals(pricey));
    }

    /**
     * Null, another kind of object, and a different name are all refused.
     */
    public void testEqualsBadInput()
    {
        assertFalse(burger.equals(null));
        assertFalse(burger.equals("Burger"));
        assertFalse(burger.equals(fries));
    }

    /**
     * hashCode takes no input. Equal items must give the same number, and
     * the edge case is that capital letters do not change it.
     */
    public void testHashCode()
    {
        MenuItem shouting = new MenuItem("BURGER", 12.00);

        assertEquals(burger.hashCode(), shouting.hashCode());
        assertEquals(burger.hashCode(), burger.hashCode());
    }

    /**
     * toString takes no input. It shows the name and the price with two
     * decimal places.
     */
    public void testToString()
    {
        assertEquals("Burger $12.00", burger.toString());
    }

    /**
     * The edge case for toString is a free item, which still shows cents.
     */
    public void testToStringZeroPrice()
    {
        MenuItem water = new MenuItem("Water", 0.00);

        assertEquals("Water $0.00", water.toString());
    }

    /** Non-finite prices must be rejected by both construction and updates. */
    public void testNonFinitePrices() {
        double[] invalid = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        };

        for (double price : invalid) {
            try {
                new MenuItem("Invalid", price);
                fail("Constructor accepted a non-finite price");
            }
            catch (IllegalArgumentException expected) {
                // Expected: invalid prices must be rejected.
            }

            try {
                burger.setPrice(price);
                fail("Setter accepted a non-finite price");
            }
            catch (IllegalArgumentException expected) {
                // The previous valid price must remain unchanged.
            }

            assertEquals(12.0, burger.getPrice(), CENT);
        }
    }

    /** Equal items must have equal hashes across zero and case variants. */
    public void testHashCodeForEqualValues() {
        MenuItem positive = new MenuItem("Soda", 0.0);
        MenuItem negative = new MenuItem("soda", -0.0);

        assertEquals(positive, negative);
        assertEquals(positive.hashCode(), negative.hashCode());

        java.util.Locale original = java.util.Locale.getDefault();

        try {
            java.util.Locale.setDefault(
                java.util.Locale.forLanguageTag("tr-TR"));

            MenuItem upper = new MenuItem("FRIES", 4.0);
            MenuItem lower = new MenuItem("fries", 4.0);
            MenuItem dotted = new MenuItem("FR\u0130ES", 4.0);

            assertEquals(upper, lower);
            assertEquals(upper.hashCode(), lower.hashCode());

            assertEquals(upper, dotted);
            assertEquals(upper.hashCode(), dotted.hashCode());
        }
        finally {
            // Restore the locale so other tests are not affected.
            java.util.Locale.setDefault(original);
        }
    }
}
