import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Runs the console menus for hosts, servers, and customers. */
public class TableNextApp {
    private static final int MAX_PARTY_SIZE = 100;
    private static final DateTimeFormatter RESERVATION_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Scanner input;
    private Restaurant restaurant;

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
        if (choice == 1) {
            Party party = addNewParty();
            if (party == null) {
                Display.printError("No table can seat a party that size");
            }
            else if (party.getPosition() == 0) {
                Display.printMessage(party.getHostName() + " was seated immediately");
            }
            else {
                Display.printMessage(party.getHostName() + " was added to the waitlist");
            }
        }
        else if (choice == 2) {
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
        else if (choice == 3) {
            Party party = restaurant.getWaitlist().removeAt(
                readInt("Position to remove: ", 1, Math.max(1,
                    restaurant.getWaitlist().getCount())));
            if (party == null) {
                Display.printError("That waitlist position does not exist");
            }
            else {
                Display.printMessage(party.getHostName() + " was removed");
            }
        }
        else if (choice == 4) {
            Display.printWaitlist(restaurant.getWaitlist().getInOrder(), UserType.HOST);
        }
        else if (choice == 5) {
            Display.printNext(restaurant.getWaitlist().peekNext());
        }
        else if (choice == 6) {
            Display.printCount(restaurant.getWaitlist().getCount());
        }
        else if (choice == 7) {
            int tableNumber = readTableNumber();
            if (restaurant.seatNext(tableNumber)) {
                Display.printMessage("A waiting party was seated");
            }
            else {
                Display.printError("Table is unavailable or no waiting party fits");
            }
        }
        else if (choice == 8) {
            if (restaurant.clearTable(readTableNumber())) {
                Display.printMessage("Table cleared");
            }
            else {
                Display.printError("That table is already open or does not exist");
            }
        }
        else if (choice == 9) {
            Display.printTables(restaurant.getTables());
        }
    }

    /** Performs a server action chosen from the Server menu. */
    public void handleServerChoice(int choice) {
        if (choice == 1) {
            Display.printTables(restaurant.getTables());
        }
        else if (choice == 2 || choice == 3) {
            int tableNumber = readTableNumber();
            MenuItem[] menu = restaurant.getMenu();
            Display.printFoodMenu(menu);
            int menuChoice = readInt("Choose a menu item: ", 1, menu.length);
            boolean changed;
            if (choice == 2) {
                changed = restaurant.addItemToTable(tableNumber, menu[menuChoice - 1]);
            }
            else {
                changed = restaurant.removeItemFromTable(tableNumber, menu[menuChoice - 1]);
            }
            if (changed) {
                Display.printMessage(choice == 2 ? "Item added" : "Item removed");
            }
            else {
                Display.printError("No seated party has that item in its order");
            }
        }
        else if (choice == 4) {
            Table table = restaurant.findTable(readTableNumber());
            Display.printOrder(table == null ? null : table.getSeatedParty());
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
                return LocalDateTime.parse(input.nextLine().trim(), RESERVATION_FORMAT);
            }
            catch (DateTimeParseException exception) {
                Display.printError("Use the format yyyy-MM-dd HH:mm");
            }
        }
    }

    private int readTableNumber() {
        int largestNumber = restaurant.getTables().length;
        return readInt("Table number: ", 1, Math.max(1, largestNumber));
    }

    private int getMaximumChoice(UserType user) {
        if (user == UserType.HOST) {
            return 9;
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
