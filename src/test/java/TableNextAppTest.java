/**
 * Tests for TableNextApp. Every public method gets a normal case and a
 * bad input case. Methods that take no input are checked with the closest
 * edge case instead, which is said in that test's comment.
 *
 * The app reads from the keyboard, so these tests use setSystemIn to hand
 * it the lines a person would type. The lines are joined with multiline.
 * setSystemIn has to be called before the app is built, because the app
 * makes its Scanner in its constructor. startApp below does both in the
 * right order.
 *
 * What the app prints is read back with systemOut().getHistory(). setUp
 * wipes that text first, which also switches the recording on.
 *
 * One thing these tests stay away from on purpose: the menu choices that
 * ask for a table number. Table numbers come from a counter that keeps
 * going for the whole program, but the app only lets a user type 1 to 3,
 * so whether a typed number finds a real table depends on how many tables
 * other tests made first. Those tests check that the app asks for a table
 * number and complains about bad input, instead of checking which table
 * it found. See the note in the README about this.
 *
 * @author Ethan Kahl
 * @version 10/14/26
 */
public class TableNextAppTest extends student.TestCase
{
    private TableNextApp app;

    /**
     * Wipes the recorded output before each test.
     */
    public void setUp()
    {
        systemOut().clearHistory();
    }

    /**
     * Builds the app with the typed lines already waiting to be read.
     *
     * @param keys the lines the user is pretending to type
     */
    private void startApp(String keys)
    {
        setSystemIn(keys);
        app = new TableNextApp();
    }

    /**
     * Returns everything the app has printed during this test.
     */
    private String shown()
    {
        return systemOut().getHistory();
    }

    /**
     * The app starts with a two, a four, and a six seat table, so a party
     * of six can be seated but a party of seven cannot.
     */
    public void testConstructor()
    {
        startApp(multiline("Big", "6", "n", "n"));
        app.handleHostChoice(1);
        assertTrue(shown().contains("was seated immediately"));

        systemOut().clearHistory();
        startApp(multiline("Huge", "7", "n", "n"));
        app.handleHostChoice(1);
        assertTrue(shown().contains("No table can seat a party that size"));
    }

    /**
     * main takes the program arguments, which this app ignores. Quitting
     * right away prints the goodbye message.
     */
    public void testMain()
    {
        setSystemIn(multiline("0"));
        TableNextApp.main(new String[0]);

        assertTrue(shown().contains("Goodbye!"));
    }

    /**
     * The edge case for main is being handed an argument anyway, which
     * should change nothing.
     */
    public void testMainIgnoresArguments()
    {
        setSystemIn(multiline("0"));
        TableNextApp.main(new String[] {"extra"});

        assertTrue(shown().contains("Goodbye!"));
    }

    /**
     * run shows the role menu, then that role's menu, does the choice
     * that was picked, and quits when the user picks 0 twice. Choice 9 is
     * used here because it is the highest one the host menu takes.
     */
    public void testRun()
    {
        startApp(multiline("1", "9", "0", "0"));
        app.run();

        String text = shown();

        assertTrue(text.contains("Who Are You?"));
        assertTrue(text.contains("Host Menu"));
        assertTrue(text.contains("Table #"));
        assertTrue(text.contains("Key:"));
        assertTrue(text.contains("Goodbye!"));
    }

    /**
     * Each role's menu takes its own range of choices, so the server menu
     * accepts 4 and the customer menu accepts 2.
     */
    public void testRunUsesEachMenusRange()
    {
        startApp(multiline("2", "4", "1", "0", "3", "2", "0", "0"));
        app.run();

        String text = shown();

        assertTrue(text.contains("Server Menu"));
        assertTrue(text.contains("No Party Is Seated at That Table"));
        assertTrue(text.contains("Customer Menu"));
        assertTrue(text.contains("No Parties Waiting"));
        assertTrue(text.contains("Goodbye!"));
    }

    /**
     * run keeps going after a role that is not on the list is typed, and
     * it lets the user switch from one role to another.
     */
    public void testRunBadInput()
    {
        startApp(multiline("abc", "9", "3", "0", "0"));
        app.run();

        String text = shown();

        assertTrue(text.contains("Enter a whole number from 0 to 3"));
        assertTrue(text.contains("Customer Menu"));
        assertTrue(text.contains("Goodbye!"));
    }

    /**
     * selectUserType takes no input of its own, it reads the keyboard.
     * Each number gives back the matching role.
     */
    public void testSelectUserType()
    {
        startApp(multiline("1"));
        assertEquals(UserType.HOST, app.selectUserType());

        startApp(multiline("2"));
        assertEquals(UserType.SERVER, app.selectUserType());

        startApp(multiline("3"));
        assertEquals(UserType.CUSTOMER, app.selectUserType());
    }

    /**
     * Quitting gives back null, and a number that is not on the list is
     * asked for again.
     */
    public void testSelectUserTypeBadInput()
    {
        startApp(multiline("0"));
        assertNull(app.selectUserType());

        systemOut().clearHistory();
        startApp(multiline("7", "1"));
        assertEquals(UserType.HOST, app.selectUserType());
        assertTrue(shown().contains("Enter a whole number from 0 to 3"));
    }

    /**
     * Host choice 1 adds a party and says it was seated, since tables are
     * open when the app starts.
     */
    public void testHandleHostChoiceAddParty()
    {
        startApp(multiline("Kim", "2", "n", "n"));
        app.handleHostChoice(1);

        assertTrue(shown().contains("Kim was seated immediately"));
    }

    /**
     * A party can be added with a reservation time typed in.
     */
    public void testHandleHostChoiceAddPartyWithReservation()
    {
        startApp(multiline("Lee", "2", "y", "y", "2099-10-20 18:30"));
        app.handleHostChoice(1);

        assertTrue(shown().contains("Lee was seated immediately"));
    }

    /**
     * A badly typed reservation time is asked for again until it is
     * right.
     */
    public void testHandleHostChoiceAddPartyBadDate()
    {
        startApp(multiline("Lee", "2", "n", "y", "tomorrow",
            "2099-10-20 18:30"));
        app.handleHostChoice(1);

        String text = shown();

        assertTrue(text.contains("Use the format yyyy-MM-dd HH:mm"));
        assertTrue(text.contains("Lee was seated immediately"));
    }

    /**
     * Once the three tables are full the next party is put on the
     * waitlist instead of being seated.
     */
    public void testHandleHostChoiceAddPartyGoesToWaitlist()
    {
        startApp(multiline("One", "2", "n", "n",
            "Two", "2", "n", "n",
            "Three", "2", "n", "n",
            "Four", "2", "n", "n"));
        app.handleHostChoice(1);
        app.handleHostChoice(1);
        app.handleHostChoice(1);

        systemOut().clearHistory();
        app.handleHostChoice(1);

        assertTrue(shown().contains("Four was added to the waitlist"));
    }

    /**
     * A party bigger than every table is turned away with an error.
     */
    public void testHandleHostChoiceAddPartyTooBig()
    {
        startApp(multiline("Crowd", "20", "n", "n"));
        app.handleHostChoice(1);

        assertTrue(shown().contains("No table can seat a party that size"));
    }

    /**
     * Host choice 2 puts a reservation on the waitlist at the position
     * that was typed.
     */
    public void testHandleHostChoiceInsertParty()
    {
        startApp(multiline("Lee", "2", "n", "n", "1"));
        app.handleHostChoice(2);

        assertTrue(shown().contains("Lee was inserted at position 1"));
    }

    /**
     * A party bigger than every table cannot be put on the waitlist
     * either.
     */
    public void testHandleHostChoiceInsertPartyTooBig()
    {
        startApp(multiline("Crowd", "20", "n", "n", "1"));
        app.handleHostChoice(2);

        assertTrue(shown().contains("No table can seat a party that size"));
    }

    /**
     * Host choice 3 takes a party back off the waitlist.
     */
    public void testHandleHostChoiceRemoveParty()
    {
        startApp(multiline("Lee", "2", "n", "n", "1", "1"));
        app.handleHostChoice(2);

        systemOut().clearHistory();
        app.handleHostChoice(3);

        assertTrue(shown().contains("Lee was removed"));
    }

    /**
     * Removing from an empty waitlist says the position is not there.
     */
    public void testHandleHostChoiceRemovePartyBadInput()
    {
        startApp(multiline("1"));
        app.handleHostChoice(3);

        assertTrue(shown().contains("That waitlist position does not exist"));
    }

    /**
     * Host choice 4 shows the waiting parties with their names.
     */
    public void testHandleHostChoiceViewWaitlist()
    {
        startApp(multiline("Lee", "3", "n", "n", "1"));
        app.handleHostChoice(2);

        systemOut().clearHistory();
        app.handleHostChoice(4);

        String text = shown();

        assertTrue(text.contains("Waitlist"));
        assertTrue(text.contains("Lee"));
    }

    /**
     * With nobody waiting, choices 4, 5, and 6 all say the line is empty.
     */
    public void testHandleHostChoiceViewsWhenEmpty()
    {
        startApp("");
        app.handleHostChoice(4);
        assertTrue(shown().contains("The Waitlist Is Empty"));

        systemOut().clearHistory();
        app.handleHostChoice(5);
        assertTrue(shown().contains("The Waitlist Is Empty"));

        systemOut().clearHistory();
        app.handleHostChoice(6);
        assertTrue(shown().contains("No Parties Waiting"));
    }

    /**
     * Host choice 5 names the party at the front of the line and choice 6
     * counts the line.
     */
    public void testHandleHostChoiceNextAndCount()
    {
        startApp(multiline("Lee", "3", "n", "n", "1"));
        app.handleHostChoice(2);

        systemOut().clearHistory();
        app.handleHostChoice(5);
        assertTrue(shown().contains("Next Up: Lee"));

        systemOut().clearHistory();
        app.handleHostChoice(6);
        assertTrue(shown().contains("1 Party Waiting"));
    }

    /**
     * Host choices 7 and 8 both ask which table to work with.
     */
    public void testHandleHostChoiceAsksForTable()
    {
        startApp(multiline("1"));
        app.handleHostChoice(7);
        assertTrue(shown().contains("Table number:"));

        systemOut().clearHistory();
        startApp(multiline("1"));
        app.handleHostChoice(8);
        assertTrue(shown().contains("Table number:"));
    }

    /**
     * A table number that is not a number at all is asked for again.
     */
    public void testHandleHostChoiceBadTableNumber()
    {
        startApp(multiline("not a table", "1"));
        app.handleHostChoice(7);

        assertTrue(shown().contains("Enter a whole number from 1 to 3"));
    }

    /**
     * Host choice 9 draws the tables and the color key.
     */
    public void testHandleHostChoiceViewTables()
    {
        startApp("");
        app.handleHostChoice(9);

        String text = shown();

        assertTrue(text.contains("Table #"));
        assertTrue(text.contains("Key:"));
    }

    /**
     * A host choice that is not on the menu does nothing and prints
     * nothing.
     */
    public void testHandleHostChoiceBadInput()
    {
        startApp("");
        app.handleHostChoice(0);
        app.handleHostChoice(-4);
        app.handleHostChoice(15);

        assertEquals("", shown());
    }

    /**
     * Server choice 1 draws the tables.
     */
    public void testHandleServerChoiceViewTables()
    {
        startApp("");
        app.handleServerChoice(1);

        String text = shown();

        assertTrue(text.contains("Table #"));
        assertTrue(text.contains("Key:"));
    }

    /**
     * Server choices 2 and 3 ask for a table and then show the food menu
     * to pick from.
     */
    public void testHandleServerChoiceShowsFoodMenu()
    {
        startApp(multiline("1", "1"));
        app.handleServerChoice(2);

        String text = shown();

        assertTrue(text.contains("Table number:"));
        assertTrue(text.contains("Burger"));
        assertTrue(text.contains("Soda"));

        systemOut().clearHistory();
        startApp(multiline("1", "1"));
        app.handleServerChoice(3);
        assertTrue(shown().contains("Burger"));
    }

    /**
     * A food choice that is not on the menu is asked for again.
     */
    public void testHandleServerChoiceBadMenuChoice()
    {
        startApp(multiline("1", "99", "1"));
        app.handleServerChoice(2);

        assertTrue(shown().contains("Enter a whole number from 1 to 4"));
    }

    /**
     * Server choice 4 asks for a table, and with nobody seated it says
     * there is no party there.
     */
    public void testHandleServerChoiceViewOrder()
    {
        startApp(multiline("1"));
        app.handleServerChoice(4);

        assertTrue(shown().contains("No Party Is Seated at That Table"));
    }

    /**
     * A server choice that is not on the menu does nothing and prints
     * nothing.
     */
    public void testHandleServerChoiceBadInput()
    {
        startApp("");
        app.handleServerChoice(0);
        app.handleServerChoice(-1);
        app.handleServerChoice(9);

        assertEquals("", shown());
    }

    /**
     * Customer choice 1 shows the line without any names on it, and
     * choice 2 counts it.
     */
    public void testHandleCustomerChoice()
    {
        startApp(multiline("Lee", "3", "n", "n", "1"));
        app.handleHostChoice(2);

        systemOut().clearHistory();
        app.handleCustomerChoice(1);

        String text = shown();

        assertTrue(text.contains("Pos"));
        assertTrue(text.contains("Size"));
        assertFalse(text.contains("Lee"));

        systemOut().clearHistory();
        app.handleCustomerChoice(2);
        assertTrue(shown().contains("1 Party Waiting"));
    }

    /**
     * A customer choice that is not on the menu does nothing and prints
     * nothing.
     */
    public void testHandleCustomerChoiceBadInput()
    {
        startApp("");
        app.handleCustomerChoice(0);
        app.handleCustomerChoice(-2);
        app.handleCustomerChoice(5);

        assertEquals("", shown());
    }

    /**
     * readInt takes a number inside the range it was given, and spaces
     * around it are ignored.
     */
    public void testReadInt()
    {
        startApp(multiline("5", "  7  "));

        assertEquals(5, app.readInt("Pick: ", 1, 10));
        assertEquals(7, app.readInt("Pick: ", 1, 10));
    }

    /**
     * Words, an empty line, and numbers outside the range are all asked
     * for again until a good one is typed.
     */
    public void testReadIntBadInput()
    {
        startApp(multiline("abc", "", "99", "0", "4"));

        assertEquals(4, app.readInt("Pick: ", 1, 10));
        assertTrue(shown().contains("Enter a whole number from 1 to 10"));
    }

    /**
     * readName takes any name that is not blank, and trims the spaces off
     * the ends.
     */
    public void testReadName()
    {
        startApp(multiline("Kim", "  Lee  "));

        assertEquals("Kim", app.readName("Name: "));
        assertEquals("Lee", app.readName("Name: "));
    }

    /**
     * An empty line and a line of only spaces are asked for again.
     */
    public void testReadNameBadInput()
    {
        startApp(multiline("", "   ", "Kim"));

        assertEquals("Kim", app.readName("Name: "));
        assertTrue(shown().contains("Name cannot be blank"));
    }

    /**
     * readYesNo takes y, yes, n, and no, in capitals or not.
     */
    public void testReadYesNo()
    {
        startApp(multiline("y", "YES", "n", "No"));

        assertTrue(app.readYesNo("Sure? "));
        assertTrue(app.readYesNo("Sure? "));
        assertFalse(app.readYesNo("Sure? "));
        assertFalse(app.readYesNo("Sure? "));
    }

    /**
     * Anything that is not a yes or a no is asked for again.
     */
    public void testReadYesNoBadInput()
    {
        startApp(multiline("maybe", "", "5", "y"));

        assertTrue(app.readYesNo("Sure? "));
        assertTrue(shown().contains("Enter y or n"));
    }

    /**
     * A reservation time in the past is asked for again.
     */
    public void testHandleHostChoiceAddPartyPastDate()
    {
        startApp(multiline("Lee", "2", "n", "y", "2000-01-01 10:00",
            "2099-10-20 18:30"));
        app.handleHostChoice(1);

        assertTrue(shown().contains("Reservation time cannot be in the past"));
    }

    /**
     * Option 10 adds a custom table.
     */
    public void testHandleHostChoiceAddCustomTable()
    {
        startApp(multiline("1", "8", "2"));
        app.handleHostChoice(10);

        assertTrue(shown().contains("Table added"));
    }

    /**
     * Loading a preset puts parties on the waitlist.
     */
    public void testHandleHostChoiceBusyNightPreset()
    {
        startApp(multiline("2"));
        app.handleHostChoice(10);

        assertTrue(shown().contains("Busy Night loaded"));
    }
}
