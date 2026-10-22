import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/** Runs the console menus for hosts, servers, and customers. */
public class TableNextApp {
    private static final int MAX_PARTY_SIZE = 100;
    private static final DateTimeFormatter RESERVATION_FORMAT =
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    
    private final Scanner input;
    private final Restaurant restaurant;

    public TableNextApp() {
        input = new Scanner(System.in);
        restaurant = new Restaurant();
        addStartingTables();
    }

    public static void main(String[] args) {
        new TableNextApp().run();
    }

    /** Keeps asking for a role and then shows that role's menu. */
    public void run() {
        try {
            runMenus();
        }
        catch (NoSuchElementException exception) {
            Display.printMessage("Input closed. Goodbye!");
        }
    }

    private void runMenus() {
        while (true) {
            UserType user = selectUserType();
            if (user == null) {
                Display.printMessage("Goodbye!");
                return;
            }

            boolean switchUser = false;
            while (!switchUser) {
                Display.printMenu(user);
                int choice = readInt("Choose an option: ", 0, getMaximumChoice(user));
                if (choice == 0) {
                    switchUser = true;
                }
                else if (user == UserType.HOST) {
                    handleHostChoice(choice);
                }
                else if (user == UserType.SERVER) {
                    handleServerChoice(choice);
                }
                else {
                    handleCustomerChoice(choice);
                }
            }
        }
    }

    /** Returns the selected role, or null when the user chooses Quit. */
    public UserType selectUserType() {
        Display.printUserTypeMenu();
        int choice = readInt("Choose a user type: ", 0, 3);
        if (choice == 1) {
            return UserType.HOST;
        }
        if (choice == 2) {
            return UserType.SERVER;
        }
        if (choice == 3) {
            return UserType.CUSTOMER;
        }
        return null;
    }

    /** Performs a host action chosen from the Host menu. */
    public void handleHostChoice(int choice) {
        switch (choice) {
            case 1 ->                 {
                    Party party = addNewParty();
                    if (party == null) {
                        Display.printError("No table can seat a party that size");
                    }
                    else if (!isWaiting(party)) {
                        Display.printMessage(party.getHostName() + " was seated immediately");
                    }
                    else {
                        Display.printMessage(party.getHostName() + " was added to the waitlist");
                    }                      }
            case 2 -> {
                String name = readName("Host name: ");
                int size = readInt("Party size: ", 1, MAX_PARTY_SIZE);
                boolean accessible = readYesNo("Needs accessible seating? (y/n): ");
                LocalDateTime reservation = readReservationTime();
                int position = readInt("Waitlist position: ", 1,
                        restaurant.getWaitlist().getCount() + 1);
                if (restaurant.insertPartyAt(name, size, accessible, reservation, position)) {
                    Display.printMessage(name + " was inserted at position " + position);
                }
                else {
                    Display.printError("No table can seat a party that size");
                }
            }
            case 3 ->                 {
                    if (restaurant.getWaitlist().isEmpty()) {
                        Display.printError("The waitlist is empty");
                        return;
                    }
                    Party party = restaurant.getWaitlist().removeAt(
                            readInt("Position to remove: ", 1, Math.max(1,
                                    restaurant.getWaitlist().getCount())));
                    if (party == null) {
                        Display.printError("That waitlist position does not exist");
                    }
                    else {
                        Display.printMessage(party.getHostName() + " was removed");
                    }                      }
            case 4 -> Display.printWaitlist(restaurant.getWaitlist().getInOrder(), UserType.HOST);
            case 5 -> Display.printNext(restaurant.getWaitlist().peekNext());
            case 6 -> Display.printCount(restaurant.getWaitlist().getCount());
            case 7 -> {
                int tableNumber = readTableNumber();
                if (restaurant.seatNext(tableNumber)) {
                    Display.printMessage("A waiting party was seated");
                }
                else {
                    Display.printError("Table is unavailable or no waiting party fits");
                }
            }
            case 8 -> {
                if (restaurant.clearTable(readTableNumber())) {
                    Display.printMessage("Table cleared");
                    int waiting = restaurant.getWaitlist().getCount();
                    if (waiting > 0) {
                        Display.printMessage(waiting + (waiting == 1 ? " party is" : " parties are")
                            + " waiting; use option 7 to seat the next one");
                    }
                }
                else {
                    Display.printError("That table is already open or does not exist");
                }
            }
            case 9 -> Display.printTables(restaurant.getTables());
            case 10 -> handleAddTable();
            case 11 -> loadPreset1();
            case 12 -> loadPreset2();
            case 13 -> loadPreset3();
            default -> {
            }
        }
    }

    /** Adds one custom table from the host's input. */
    public void handleAddTable() {
        int size = readEvenInt("Table size (even number): ", 2, MAX_PARTY_SIZE);
        int accessible = readInt("Accessible seats: ", 0, size);
        if (restaurant.addTable(size, accessible)) {
            Display.printMessage("Table added");
        }
        else {
            Display.printError("Could not add that table");
        }
    }

    /** Reads an even whole number in the requested inclusive range. */
    private int readEvenInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt, min, max);
            if (value % 2 == 0) {
                return value;
            }
            Display.printError("Enter an even number");
        }
    }

    /** Adds tables sized to match, then fills every table with no one waiting. */
    private void loadPreset1() {
        restaurant.addTable(4, 0);
        restaurant.addTable(6, 2);
        restaurant.addTable(8, 2);
        int[] sizes = {2, 4, 4, 6, 6, 8};
        String[] names = {"Adams", "Baker", "Chen", "Diaz", "Evans", "Foster"};
        for (int i = 0; i < sizes.length; i++) {
            restaurant.addParty(names[i], sizes[i], false, null);
        }
        Display.printMessage("Preset 1 loaded: every table is full");
    }

    /** Fills every table like Preset 1, then leaves five parties waiting. */
    private void loadPreset2() {
        loadPreset1();
        int[] sizes = {3, 5, 2, 4, 6};
        String[] names = {"Garcia", "Hill", "Ito", "Jones", "Kim"};
        for (int i = 0; i < sizes.length; i++) {
            restaurant.addParty(names[i], sizes[i], false, null);
        }
        Display.printMessage("Preset 2 loaded: every table is full and 5 parties are waiting");
    }

    /** Seats some parties, then inserts a reservation at the front of the waitlist. */
    private void loadPreset3() {
        restaurant.addParty("Young", 2, false, null);
        restaurant.addParty("Zhang", 4, false, null);
        restaurant.addParty("Allen", 6, false, null);
        restaurant.addParty("Brooks", 2, false, null);
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        restaurant.addParty("Cole", 4, false, now.plusMinutes(30));
        restaurant.addParty("Dunn", 6, true, now.plusHours(1));
        restaurant.insertPartyAt("Ellis", 2, false, now.plusMinutes(15), 1);
        Display.printMessage("Preset 3 loaded: Ellis was inserted at the front of the waitlist");
    }

    /** Performs a server action chosen from the Server menu. */
    public void handleServerChoice(int choice) {
        switch (choice) {
            case 1 -> Display.printTables(restaurant.getTables());
            case 2, 3 -> {
                int tableNumber = readTableNumber();
                MenuItem[] menu = restaurant.getMenu();
                Display.printFoodMenu(menu);
                int menuChoice = readMenuChoice(menu);
                boolean changed;
                if (choice == 2) {
                    changed = restaurant.addItemToTable(tableNumber, menu[menuChoice - 1]);
                }
                else {
                    changed = restaurant.removeItemFromTable(tableNumber, menu[menuChoice - 1]);
                }   if (changed) {
                    Display.printMessage(choice == 2 ? "Item added" : "Item removed");
                }
                else {
                    Display.printError("No seated party has that item in its order");
                }
            }
            case 4 -> {
                Table table = restaurant.findTable(readTableNumber());
                Display.printOrder(table == null ? null : table.getSeatedParty());
            }
            default -> {
            }
        }
    }

    /** Performs a customer action chosen from the Customer menu. */
    public void handleCustomerChoice(int choice) {
        if (choice == 1) {
            Display.printWaitlist(restaurant.getWaitlist().getInOrder(),
                UserType.CUSTOMER);
        }
        else if (choice == 2) {
            Display.printCount(restaurant.getWaitlist().getCount());
        }
    }

    /** Reads a whole number in the requested inclusive range. */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
            }
            catch (NumberFormatException exception) {
                // The same error below also handles non-numeric input.
            }
            Display.printError("Enter a whole number from " + min + " to " + max);
        }
    }

    /** Reads a menu item by its number or its name, ignoring case. */
    private int readMenuChoice(MenuItem[] menu) {
        while (true) {
            System.out.print("Choose a menu item (number or name): ");
            String text = input.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (value >= 1 && value <= menu.length) {
                    return value;
                }
            }
            catch (NumberFormatException exception) {
                for (int i = 0; i < menu.length; i++) {
                    if (menu[i].getName().equalsIgnoreCase(text)) {
                        return i + 1;
                    }
                }
            }
            Display.printError("Enter a menu number from 1 to " + menu.length + " or an item name");
        }
    }

    /** Reads a non-blank name. */
    public String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = input.nextLine().trim();
            if (!name.isEmpty()) {
                return name;
            }
            Display.printError("Name cannot be blank");
        }
    }

    /** Reads y or n and changes it to a boolean value. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = input.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            Display.printError("Enter y or n");
        }
    }

    /** Creates one party from the host's input. */
    private Party addNewParty() {
        String name = readName("Host name: ");
        int size = readInt("Party size: ", 1, MAX_PARTY_SIZE);
        boolean accessible = readYesNo("Needs accessible seating? (y/n): ");
        LocalDateTime reservation = readReservationTime();
        return restaurant.addParty(name, size, accessible, reservation);
    }

    /** Reads a reservation only when the host says that one exists. */
    private LocalDateTime readReservationTime() {
        if (!readYesNo("Has a reservation? (y/n): ")) {
            return null;
        }

        while (true) {
            System.out.print("Reservation time (yyyy-MM-dd HH:mm): ");
            try {
                LocalDateTime time = LocalDateTime.parse(input.nextLine().trim(), RESERVATION_FORMAT);
                if (time.isBefore(LocalDateTime.now())) {
                    Display.printError("Reservation time cannot be in the past");
                    continue;
                }
                return time;
            }
            catch (DateTimeParseException exception) {
                Display.printError("Use the format yyyy-MM-dd HH:mm");
            }
        }
    }

    /** Reads a table number between the smallest and largest existing numbers. */
    private int readTableNumber() {
        int smallest = Integer.MAX_VALUE;
        int largest = 0;
        for (Table table : restaurant.getTables()) {
            smallest = Math.min(smallest, table.getTableNumber());
            largest = Math.max(largest, table.getTableNumber());
        }
        if (largest == 0) {
            smallest = 1;
            largest = 1;
        }
        return readInt("Table number: ", smallest, largest);
    }

    /** Checks whether a party is on the waitlist rather than seated. */
    private boolean isWaiting(Party party) {
        for (Party waiting : restaurant.getWaitlist().getInOrder()) {
            if (waiting.equals(party)) {
                return true;
            }
        }
        return false;
    }

    private int getMaximumChoice(UserType user) {
        if (user == UserType.HOST) {
            return 13;
        }
        if (user == UserType.SERVER) {
            return 4;
        }
        return 2;
    }

    /** Adds a few tables so the program is usable when it starts. */
    private void addStartingTables() {
        restaurant.addTable(2, 0);
        restaurant.addTable(4, 2);
        restaurant.addTable(6, 2);
    }
}
