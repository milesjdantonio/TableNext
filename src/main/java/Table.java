public class Table {
    private final int tableNumber;
    private static int nextTableNumber = 1;
    private int size;
    private int handicapSeats;
    private Party seatedParty;

    public Table(int size) {
        this(size, 0);
    }

    public Table(int size, int handicapSeats) {
        requireValidSize(size);
        if (handicapSeats < 0 || handicapSeats > size) {
            throw new IllegalArgumentException("Handicap seats must be between 0 and the table size");
        }

        this.tableNumber = nextTableNumber++;
        this.size = size;
        this.handicapSeats = handicapSeats;
        this.seatedParty = null;
    }

    public int getHandicapSeats() {
        return this.handicapSeats;
    }

    // Handicap seats are the last seats at the table (highest numbers)
    public boolean isHandicapSeat(int seat) {
        return seat > this.size - this.handicapSeats;
    }

    // A party fills regular seats first, unless it needs accessibility,
    // in which case it fills the handicap seats first
    public boolean isSeatTaken(int seat) {
        if (isOpen()) {
            return false;
        }

        int partySize = this.seatedParty.getSize();
        int regularSeats = this.size - this.handicapSeats;
        int handicapTaken;
        int regularTaken;

        if (this.seatedParty.needAccessibility()) {
            handicapTaken = Math.min(partySize, this.handicapSeats);
            regularTaken = partySize - handicapTaken;
        } else {
            regularTaken = Math.min(partySize, regularSeats);
            handicapTaken = partySize - regularTaken;
        }

        if (isHandicapSeat(seat)) {
            return seat - regularSeats <= handicapTaken;
        }
        return seat <= regularTaken;
    }

    public int getTableNumber() {
        return this.tableNumber;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        requireValidSize(size);
        if (size < this.handicapSeats) {
            throw new IllegalArgumentException("Table size cannot be smaller than its handicap seats");
        }
        this.size = size;
    }

    // Chairs are drawn in two equal rows, so a table must have a positive, even size
    private static void requireValidSize(int size) {
        if (size <= 0 || size % 2 != 0) {
            throw new IllegalArgumentException("Table size must be a positive even number");
        }
    }

    public Party getSeatedParty() {
        return this.seatedParty;
    }

    public boolean isOpen() {
        return this.seatedParty == null;
    }

    public boolean seat(Party party) {
        if (party == null || !isOpen() || party.getSize() > this.size) {
            return false;
        }

        this.seatedParty = party;
        return true;
    }

    public Party clear() {
        Party result = this.seatedParty;
        this.seatedParty = null;
        return result;
    }
}
