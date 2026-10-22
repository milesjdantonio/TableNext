import java.time.format.DateTimeFormatter;

public class Display {
    private static final String COLOR_RESET = "\u001B[0m";
    private static final String COLOR_RED = "\u001B[31m";
    private static final String COLOR_YELLOW = "\u001B[33m";
    private static final String COLOR_GREEN = "\u001B[32m";
    private static final String COLOR_BLUE = "\u001B[34m";
    private static final String COLOR_PURPLE = "\u001B[35m";
    private static final int ESTIMATED_MINUTES_PER_PARTY = 15;

    /* 
        TABLE GENERATION SECTION
    */
    
    public static String generateTable(Table table) {
        StringBuilder sb = new StringBuilder();
        int inner = 9 * (table.getSize() / 2);   // chair row width + one dash past each side
        int height = table.getSize() == 2 ? 2 : 4;
        int nameRow = (height - 1) / 2;

        sb.append("\n|").append("-".repeat(inner)).append("|\n");
        for (int row = 0; row < height; row++) {
            if (row == nameRow && !table.isOpen()) {
                sb.append("|").append(generatePartyId(table, inner)).append("|\n");
            } else {
                sb.append("|").append(" ".repeat(inner)).append("|\n");
            }
        }
        sb.append("|").append("-".repeat(inner)).append("|\n");

        return sb.toString();
    }

    // Centers the seated party's ID in yellow, then switches back to the table color
    public static String generatePartyId(Table table, int width) {
        long id = table.getSeatedParty().getId();
        String name = "Party ID: " + id;
        if (name.length() > width) {
            name = "ID: " + id;
        }
        int left = Math.max(0, (width - name.length()) / 2);
        int right = Math.max(0, width - name.length() - left);

        return " ".repeat(left) + COLOR_YELLOW + name + getTableColor(table) + " ".repeat(right);
    }

    public static String generateChair(Table table, int start) {
        StringBuilder sb = new StringBuilder("  ");
        int chairs = table.getSize() / 2;

        for (int topRow = 0; topRow < chairs; topRow++) {
            sb.append(getSeatColor(table, start + topRow));
            sb.append("|").append("-----").append("|").append(COLOR_RESET).append("  ");
        }
        sb.append("\n  ");
        for (int topMiddle = 0; topMiddle < chairs; topMiddle++) {
            sb.append(getSeatColor(table, start + topMiddle));
            sb.append("|").append(String.format("%3d  ", start + topMiddle)).append("|").append(COLOR_RESET).append("  ");
        }
        sb.append("\n  ");
        for (int bottomRow = 0; bottomRow < chairs; bottomRow++) {
            sb.append(getSeatColor(table, start + bottomRow));
            sb.append("|").append("-----").append("|").append(COLOR_RESET).append("  ");
        }
        return sb.toString();
    } 

    public static String generateLabel(Table table) {
        String label = "Table #" + table.getTableNumber();
        int width = 9 * (table.getSize() / 2) + 2;   // full width of the table, borders included
        int padding = Math.max(0, (width - label.length()) / 2);

        return " ".repeat(padding) + label + "\n";
    }

    public static String getTableColor(Table table) {
        if (table.isOpen()) {
            return COLOR_GREEN;
        }
        return COLOR_RED;
    }

    public static String getSeatColor(Table table, int seat) {
        boolean taken = table.isSeatTaken(seat);

        if (table.isHandicapSeat(seat)) {
            return taken ? COLOR_PURPLE : COLOR_BLUE;
        }
        return taken ? COLOR_RED : COLOR_GREEN;
    }

    public static String generateFullTable(Table table) {
        StringBuilder sb = new StringBuilder();

        sb.append(getTableColor(table)).append(generateLabel(table)).append(COLOR_RESET);
        sb.append(generateChair(table, 1));
        sb.append(getTableColor(table)).append(generateTable(table)).append(COLOR_RESET);
        sb.append(generateChair(table, table.getSize() / 2 + 1));

        return sb.toString();
    }

    /*
        MENU SECTION
    */

    public static void printUserTypeMenu() {
        System.out.println("\nWho Are You?");
        System.out.println("  1. Host");
        System.out.println("  2. Server");
        System.out.println("  3. Customer");
        System.out.println("  0. Quit");
    }

    public static void printMenu(UserType user) {
        if (user == null) {
            return;
        }

        switch (user) {
            case HOST -> {
                System.out.println("\nHost Menu");
                System.out.println("  1. Add Party");
                System.out.println("  2. Insert Party at Position");
                System.out.println("  3. Remove Party at Position");
                System.out.println("  4. View Waitlist");
                System.out.println("  5. View Next Party");
                System.out.println("  6. View Waitlist Count");
                System.out.println("  7. Seat Next Party at Table");
                System.out.println("  8. Clear Table");
                System.out.println("  9. View Tables");
                System.out.println("  10. Add Custom Table");
                System.out.println("  11. Preset 1");
                System.out.println("  12. Preset 2");
                System.out.println("  13. Preset 3");
            }
            case SERVER -> {
                System.out.println("\nServer Menu");
                System.out.println("  1. View Tables");
                System.out.println("  2. Add Item to Table");
                System.out.println("  3. Remove Item from Table");
                System.out.println("  4. View Table Order");
            }
            case CUSTOMER -> {
                System.out.println("\nCustomer Menu");
                System.out.println("  1. View Waitlist");
                System.out.println("  2. View Waitlist Count");
            }
        }
        System.out.println("  0. Switch User");
    }

    /*
        WAITLIST SECTION
    */

    public static void printWaitlist(Party[] parties, UserType user) {
        if (parties == null || parties.length == 0) {
            System.out.println("The Waitlist Is Empty");
            return;
        }

        System.out.println("\nWaitlist");
        if (user == UserType.HOST) {
            System.out.println(String.format("  %-4s %-4s %-15s %-5s %-11s %-11s %s",
                "Pos", "ID", "Name", "Size", "Accessible", "Est. Wait", "Reservation"));
            for (Party party : parties) {
                System.out.println(String.format("  %-4d %-4d %-15.15s %-5d %-11s %-11s %s",
                    party.getPosition(), party.getId(), party.getHostName(), party.getSize(),
                    party.needAccessibility() ? "Yes" : "No", formatEstimatedWait(party),
                    formatReservation(party)));
            }
        } else {
            System.out.println(String.format("  %-4s %-5s %s", "Pos", "Size", "Est. Wait"));
            for (Party party : parties) {
                System.out.println(String.format("  %-4d %-5d %s",
                    party.getPosition(), party.getSize(), formatEstimatedWait(party)));
            }
        }
    }

    /** Estimates a party's remaining wait from its position in line. */
    private static String formatEstimatedWait(Party party) {
        return (party.getPosition() * ESTIMATED_MINUTES_PER_PARTY) + " min";
    }

    public static void printNext(Party party) {
        if (party == null) {
            System.out.println("The Waitlist Is Empty");
            return;
        }
        System.out.println("Next Up: " + party.getHostName() + ", Party of " + party.getSize()
            + " (ID " + party.getId() + ")");
    }

    public static void printCount(int count) {
        if (count <= 0) {
            System.out.println("No Parties Waiting");
        } else if (count == 1) {
            System.out.println("1 Party Waiting");
        } else {
            System.out.println(count + " Parties Waiting");
        }
    }

    private static String formatReservation(Party party) {
        if (!party.hasReservation()) {
            return "None";
        }
        return party.getReservationTime().format(DateTimeFormatter.ofPattern("MMM d, h:mm a"));
    }

    /*
        TABLE AND ORDER SECTION
    */

    public static void printTables(Table[] tables) {
        if (tables == null || tables.length == 0) {
            System.out.println("No Tables Set Up");
            return;
        }

        for (Table table : tables) {
            System.out.println(generateFullTable(table));
        }
        System.out.println("Key: " + COLOR_GREEN + "Open" + COLOR_RESET + "  "
            + COLOR_RED + "Taken" + COLOR_RESET + "  "
            + COLOR_BLUE + "Accessible Open" + COLOR_RESET + "  "
            + COLOR_PURPLE + "Accessible Taken" + COLOR_RESET);
    }

    public static void printOrder(Party party) {
        if (party == null) {
            printError("No Party Is Seated at That Table");
            return;
        }

        MenuItem[] items = party.getOrder();
        System.out.println("\nOrder for " + party.getHostName() + " (ID " + party.getId() + ")");
        if (items.length == 0) {
            System.out.println("  No Items Ordered");
            return;
        }

        for (MenuItem item : items) {
            System.out.println(String.format("  %-20s $%7.2f", item.getName(), item.getPrice()));
        }
        System.out.println("  " + "-".repeat(29));
        System.out.println(String.format("  %-20s $%7.2f", "Total", party.getOrderTotal()));
    }

    public static void printFoodMenu(MenuItem[] items) {
        if (items == null || items.length == 0) {
            System.out.println("The Menu Is Empty");
            return;
        }

        System.out.println("\nMenu");
        for (int i = 0; i < items.length; i++) {
            System.out.println(String.format("  %2d. %-20s $%7.2f", i + 1, items[i].getName(), items[i].getPrice()));
        }
    }

    /*
        MESSAGE SECTION
    */

    public static void printMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println(COLOR_GREEN + message + COLOR_RESET);
    }

    public static void printError(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        System.out.println(COLOR_RED + "Error: " + message + COLOR_RESET);
    }
}