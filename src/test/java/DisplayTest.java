import java.time.LocalDateTime;

/**
 * Tests for Display. Every public method gets a normal case and a bad
 * input case. Methods that take no input are checked with the closest
 * edge case instead, which is said in that test's comment.
 *
 * The print methods send their text to System.out, so these tests read it
 * back with systemOut().getHistory(). setUp wipes that text first, which
 * also switches the recording on, so each test only sees its own output.
 *
 * Display puts color codes in its text. These tests do not spell those
 * codes out. Where the color is the point, they just check that two
 * colors are different from each other.
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class DisplayTest extends student.TestCase
{
    private Table openTable;
    private Table takenTable;
    private Party kim;
    private Party lee;

    /**
     * Wipes the recorded output, then makes two tables and two parties.
     * One table has a party sitting at it and one is left open.
     */
    public void setUp()
    {
        systemOut().clearHistory();
        kim = new Party("Kim", 2, false,
            LocalDateTime.of(2026, 10, 20, 18, 30));
        lee = new Party("Lee", 2, true, null);
        openTable = new Table(4, 2);
        takenTable = new Table(4, 2);
        takenTable.seat(kim);
    }

    /**
     * Returns everything that has been printed during this test.
     */
    private String printed()
    {
        return systemOut().getHistory();
    }

    /**
     * The label shows the table's own number.
     */
    public void testGenerateLabel()
    {
        String label = Display.generateLabel(openTable);

        assertTrue(label.contains("Table #" + openTable.getTableNumber()));
    }

    /**
     * The edge case is the smallest table, which has less room for the
     * label but still shows it.
     */
    public void testGenerateLabelSmallTable()
    {
        Table twoSeater = new Table(2);
        String label = Display.generateLabel(twoSeater);

        assertTrue(label.contains("Table #" + twoSeater.getTableNumber()));
    }

    /**
     * The drawn table is a box of dashes and bars, and an open one shows
     * no party inside it.
     */
    public void testGenerateTableOpen()
    {
        String box = Display.generateTable(openTable);

        assertTrue(box.contains("|"));
        assertTrue(box.contains("-"));
        assertFalse(box.contains("ID:"));
    }

    /**
     * The edge case is a table with somebody at it, which shows the
     * party's id inside the box.
     */
    public void testGenerateTableSeated()
    {
        String box = Display.generateTable(takenTable);

        assertTrue(box.contains("Party ID: " + kim.getId()));
    }

    /**
     * The party id is centered inside the box when there is room for the
     * long version of the label.
     */
    public void testGeneratePartyId()
    {
        String text = Display.generatePartyId(takenTable, 18);

        assertTrue(text.contains("Party ID: " + kim.getId()));
    }

    /**
     * When the table is too narrow for the long label, the short version
     * is used instead so the drawing still lines up.
     */
    public void testGeneratePartyIdNarrowTable()
    {
        String text = Display.generatePartyId(takenTable, 9);

        assertTrue(text.contains("ID: " + kim.getId()));
        assertFalse(text.contains("Party ID: " + kim.getId()));
    }

    /**
     * The chairs are drawn as three rows, and the seat numbers start at
     * the number that was passed in.
     */
    public void testGenerateChair()
    {
        String chairs = Display.generateChair(openTable, 1);

        assertTrue(chairs.contains("|-----|"));
        assertTrue(chairs.contains("1"));
        assertTrue(chairs.contains("2"));
    }

    /**
     * The edge case is the bottom row, which starts counting at a higher
     * seat number.
     */
    public void testGenerateChairSecondRow()
    {
        String chairs = Display.generateChair(openTable, 3);

        assertTrue(chairs.contains("3"));
        assertTrue(chairs.contains("4"));
        assertFalse(chairs.contains("1"));
    }

    /**
     * An open table and a taken table are drawn in two different colors.
     */
    public void testGetTableColor()
    {
        String openColor = Display.getTableColor(openTable);
        String takenColor = Display.getTableColor(takenTable);

        assertFalse(openColor.equals(takenColor));
    }

    /**
     * The edge case is clearing a table, which should put its color back
     * to the one an open table uses.
     */
    public void testGetTableColorAfterClearing()
    {
        String openColor = Display.getTableColor(openTable);

        takenTable.clear();

        assertEquals(openColor, Display.getTableColor(takenTable));
    }

    /**
     * A free seat and a taken seat are two different colors. The party
     * here does not need accessible seating, so it fills seat one.
     */
    public void testGetSeatColor()
    {
        String takenSeat = Display.getSeatColor(takenTable, 1);
        String freeSeat = Display.getSeatColor(takenTable, 3);

        assertFalse(takenSeat.equals(freeSeat));
    }

    /**
     * The edge case is an accessible seat, which gets its own color that
     * is not the one a regular free seat uses.
     */
    public void testGetSeatColorAccessibleSeat()
    {
        String regularFree = Display.getSeatColor(openTable, 1);
        String accessibleFree = Display.getSeatColor(openTable, 4);

        assertFalse(regularFree.equals(accessibleFree));
    }

    /**
     * The full drawing puts the label, the chairs, and the box together.
     */
    public void testGenerateFullTable()
    {
        String drawing = Display.generateFullTable(openTable);

        assertTrue(drawing.contains("Table #" + openTable.getTableNumber()));
        assertTrue(drawing.contains("|-----|"));
    }

    /**
     * The edge case is a taken table, whose drawing also shows the party
     * sitting there.
     */
    public void testGenerateFullTableSeated()
    {
        String drawing = Display.generateFullTable(takenTable);

        assertTrue(drawing.contains("Party ID: " + kim.getId()));
    }

    /**
     * printUserTypeMenu takes no input. It lists all three roles and the
     * quit choice.
     */
    public void testPrintUserTypeMenu()
    {
        Display.printUserTypeMenu();

        String shown = printed();

        assertTrue(shown.contains("Who Are You?"));
        assertTrue(shown.contains("1. Host"));
        assertTrue(shown.contains("2. Server"));
        assertTrue(shown.contains("3. Customer"));
        assertTrue(shown.contains("0. Quit"));
    }

    /**
     * Each role gets its own menu, and every one of them ends with the
     * choice to switch user.
     */
    public void testPrintMenu()
    {
        Display.printMenu(UserType.HOST);
        assertTrue(printed().contains("Host Menu"));
        assertTrue(printed().contains("1. Add Party"));

        systemOut().clearHistory();
        Display.printMenu(UserType.SERVER);
        assertTrue(printed().contains("Server Menu"));
        assertTrue(printed().contains("2. Add Item to Table"));

        systemOut().clearHistory();
        Display.printMenu(UserType.CUSTOMER);
        assertTrue(printed().contains("Customer Menu"));
        assertTrue(printed().contains("0. Switch User"));
    }

    /**
     * A null role prints nothing at all.
     */
    public void testPrintMenuBadInput()
    {
        Display.printMenu(null);

        assertEquals("", printed());
    }

    /**
     * The host sees the position, name, size, and reservation of every
     * party waiting.
     */
    public void testPrintWaitlistForHost()
    {
        kim.setPosition(1);
        lee.setPosition(2);

        Party[] waiting = {kim, lee};

        Display.printWaitlist(waiting, UserType.HOST);

        String shown = printed();

        assertTrue(shown.contains("Waitlist"));
        assertTrue(shown.contains("Kim"));
        assertTrue(shown.contains("Lee"));
        assertTrue(shown.contains("6:30"));
    }

    /**
     * A customer sees only positions and sizes, never the other guests'
     * names.
     */
    public void testPrintWaitlistForCustomer()
    {
        kim.setPosition(1);

        Party[] waiting = {kim};

        Display.printWaitlist(waiting, UserType.CUSTOMER);

        String shown = printed();

        assertTrue(shown.contains("Pos"));
        assertTrue(shown.contains("Size"));
        assertFalse(shown.contains("Kim"));
    }

    /**
     * A null list and an empty list both say the waitlist is empty.
     */
    public void testPrintWaitlistBadInput()
    {
        Party[] nobody = {};

        Display.printWaitlist(null, UserType.HOST);
        assertTrue(printed().contains("The Waitlist Is Empty"));

        systemOut().clearHistory();
        Display.printWaitlist(nobody, UserType.HOST);
        assertTrue(printed().contains("The Waitlist Is Empty"));
    }

    /**
     * printNext names the party at the front of the line.
     */
    public void testPrintNext()
    {
        Display.printNext(kim);

        String shown = printed();

        assertTrue(shown.contains("Next Up: Kim"));
        assertTrue(shown.contains("Party of 2"));
    }

    /**
     * A null party means nobody is waiting.
     */
    public void testPrintNextBadInput()
    {
        Display.printNext(null);

        assertTrue(printed().contains("The Waitlist Is Empty"));
    }

    /**
     * The count is worded for one party and for several.
     */
    public void testPrintCount()
    {
        Display.printCount(1);
        assertTrue(printed().contains("1 Party Waiting"));

        systemOut().clearHistory();
        Display.printCount(3);
        assertTrue(printed().contains("3 Parties Waiting"));
    }

    /**
     * Zero and a negative count both say nobody is waiting.
     */
    public void testPrintCountBadInput()
    {
        Display.printCount(0);
        assertTrue(printed().contains("No Parties Waiting"));

        systemOut().clearHistory();
        Display.printCount(-5);
        assertTrue(printed().contains("No Parties Waiting"));
    }

    /**
     * printTables draws every table and then the color key.
     */
    public void testPrintTables()
    {
        Table[] tables = {openTable, takenTable};

        Display.printTables(tables);

        String shown = printed();

        assertTrue(shown.contains("Table #" + openTable.getTableNumber()));
        assertTrue(shown.contains("Table #" + takenTable.getTableNumber()));
        assertTrue(shown.contains("Key:"));
    }

    /**
     * A null list and an empty list both say there are no tables.
     */
    public void testPrintTablesBadInput()
    {
        Table[] none = {};

        Display.printTables(null);
        assertTrue(printed().contains("No Tables Set Up"));

        systemOut().clearHistory();
        Display.printTables(none);
        assertTrue(printed().contains("No Tables Set Up"));
    }

    /**
     * An order shows every item and adds up a total.
     */
    public void testPrintOrder()
    {
        kim.addItem(new MenuItem("Burger", 12.00));
        kim.addItem(new MenuItem("Fries", 4.00));

        Display.printOrder(kim);

        String shown = printed();

        assertTrue(shown.contains("Order for Kim"));
        assertTrue(shown.contains("Burger"));
        assertTrue(shown.contains("Fries"));
        assertTrue(shown.contains("Total"));
        assertTrue(shown.contains("16.00"));
    }

    /**
     * A party that has not ordered yet says so instead of showing a
     * total.
     */
    public void testPrintOrderNothingOrdered()
    {
        Display.printOrder(kim);

        String shown = printed();

        assertTrue(shown.contains("No Items Ordered"));
        assertFalse(shown.contains("Total"));
    }

    /**
     * A null party means nobody is sitting at that table.
     */
    public void testPrintOrderBadInput()
    {
        Display.printOrder(null);

        assertTrue(printed().contains("No Party Is Seated at That Table"));
    }

    /**
     * The food menu is numbered from one and shows each price.
     */
    public void testPrintFoodMenu()
    {
        MenuItem[] menu = {new MenuItem("Burger", 12.00),
            new MenuItem("Soda", 2.50)};

        Display.printFoodMenu(menu);

        String shown = printed();

        assertTrue(shown.contains("1. Burger"));
        assertTrue(shown.contains("2. Soda"));
        assertTrue(shown.contains("12.00"));
        assertTrue(shown.contains("2.50"));
    }

    /**
     * A null menu and an empty menu both say the menu is empty.
     */
    public void testPrintFoodMenuBadInput()
    {
        MenuItem[] none = {};

        Display.printFoodMenu(null);
        assertTrue(printed().contains("The Menu Is Empty"));

        systemOut().clearHistory();
        Display.printFoodMenu(none);
        assertTrue(printed().contains("The Menu Is Empty"));
    }

    /**
     * A message is printed for the user to read.
     */
    public void testPrintMessage()
    {
        Display.printMessage("Table cleared");

        assertTrue(printed().contains("Table cleared"));
    }

    /**
     * Null and a blank message print nothing at all.
     */
    public void testPrintMessageBadInput()
    {
        Display.printMessage(null);
        Display.printMessage("");
        Display.printMessage("   ");

        assertEquals("", printed());
    }

    /**
     * An error is printed with the word Error in front of it.
     */
    public void testPrintError()
    {
        Display.printError("Table is unavailable");

        assertTrue(printed().contains("Error: Table is unavailable"));
    }

    /**
     * Null and a blank error print nothing at all.
     */
    public void testPrintErrorBadInput()
    {
        Display.printError(null);
        Display.printError("");
        Display.printError("   ");

        assertEquals("", printed());
    }
}
