import java.time.LocalDateTime;

/**
 * Coordinates restaurant actions. It owns the tables, the waitlist, and the
 * food menu while Party and Table objects keep their own smaller details.
 */
public class Restaurant {
    private final Waitlist waitlist;
    private final LinkedChain<Table> tables;
    private final LinkedChain<MenuItem> menu;

    /** Creates a restaurant with a small menu that servers can use. */
    public Restaurant() {
        waitlist = new Waitlist();
        tables = new LinkedChain<Table>();
        menu = new LinkedChain<MenuItem>();

        menu.addToEnd(new MenuItem("Burger", 12.00));
        menu.addToEnd(new MenuItem("Fries", 4.00));
        menu.addToEnd(new MenuItem("Salad", 8.00));
        menu.addToEnd(new MenuItem("Soda", 2.50));
    }

    /** Adds a table when its size and accessible-seat count are valid. */
    public boolean addTable(int size, int handicapSeats) {
        try {
            tables.addToEnd(new Table(size, handicapSeats));
            return true;
        }
        catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /** Convenience version for a table without accessible seats. */
    public boolean addTable(int size) {
        return addTable(size, 0);
    }

    /**
     * Adds a new party. It is seated immediately at the smallest open table
     * that fits; otherwise it joins the end of the waitlist.
     */
    public Party addParty(String hostName, int size, boolean needAccessibility,
        LocalDateTime reservationTime) {
        if (!canEverSeat(size, needAccessibility)) {
            return null;
        }

        Party party = new Party(hostName, size, needAccessibility, reservationTime);
        Table openTable = findOpenTable(size, needAccessibility);
        if (openTable != null) {
            openTable.seat(party);
        }
        else {
            waitlist.addParty(party);
        }
        return party;
    }

    /** Adds a reservation to a chosen waitlist position. */
    public boolean insertPartyAt(String hostName, int size,
        boolean needAccessibility, LocalDateTime reservationTime, int position) {
        if (!canEverSeat(size, needAccessibility)) {
            return false;
        }
        Party party = new Party(hostName, size, needAccessibility, reservationTime);
        return waitlist.insertAt(party, position);
    }

    /** Returns the smallest open table that can seat a party of this size. */
    public Table findOpenTable(int partySize) {
        return findOpenTable(partySize, false);
    }

    /**
     * Returns the smallest open table that fits the party; a party that
     * needs accessibility only matches tables with accessible seats.
     */
    public Table findOpenTable(int partySize, boolean needAccessibility) {
        // A valid party must contain at least one person.
        if (partySize <= 0) {
            return null;
        }
        Table bestTable = null;
        for (int index = 0; index < tables.size(); index++) {
            Table table = tables.get(index);
            if (needAccessibility && table.getHandicapSeats() == 0) {
                continue;
            }
            if (table.isOpen() && table.getSize() >= partySize
                && (bestTable == null || table.getSize() < bestTable.getSize())) {
                bestTable = table;
            }
        }
        return bestTable;
    }

    /** Finds one table by its automatically assigned number. */
    public Table findTable(int tableNumber) {
        for (int index = 0; index < tables.size(); index++) {
            Table table = tables.get(index);
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return null;
    }

    /** Seats the first waiting party that fits at an open table. */
    public boolean seatNext(int tableNumber) {
        Table table = findTable(tableNumber);
        if (table == null || !table.isOpen()) {
            return false;
        }

        Party party = waitlist.removeFirstThatFits(table.getSize(),
            table.getHandicapSeats());
        return party != null && table.seat(party);
    }

    /** Clears a table only when a party is seated there. */
    public boolean clearTable(int tableNumber) {
        Table table = findTable(tableNumber);
        return table != null && !table.isOpen() && table.clear() != null;
    }

    /** Adds one menu item to the seated party's order. */
    public boolean addItemToTable(int tableNumber, MenuItem item) {
        Table table = findTable(tableNumber);
        return table != null && !table.isOpen()
            && table.getSeatedParty().addItem(item);
    }

    /** Removes one matching menu item from the seated party's order. */
    public boolean removeItemFromTable(int tableNumber, MenuItem item) {
        Table table = findTable(tableNumber);
        return table != null && !table.isOpen()
            && table.getSeatedParty().removeItem(item);
    }

    public Waitlist getWaitlist() {
        return waitlist;
    }

    public Table[] getTables() {
        return tables.toArray(new Table[tables.size()]);
    }

    public MenuItem[] getMenu() {
        return menu.toArray(new MenuItem[menu.size()]);
    }

    /** Checks whether any existing table could ever seat this party. */
    private boolean canEverSeat(int partySize, boolean needAccessibility) {
        if (partySize <= 0) {
            return false;
        }
        for (int index = 0; index < tables.size(); index++) {
            Table table = tables.get(index);
            if (table.getSize() >= partySize
                && (!needAccessibility || table.getHandicapSeats() > 0)) {
                return true;
            }
        }
        return false;
    }
}
