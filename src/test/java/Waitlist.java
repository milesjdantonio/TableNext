/**
 * Stores the parties waiting to be seated. The linked chain stays in the
 * same order as the waitlist, so the first chain entry is position 1.
 */
public class Waitlist {
    private LinkedChain<Party> line;

    /** Creates an empty waitlist. */
    public Waitlist() {
        line = new LinkedChain<Party>();
    }

    /** Adds a party to the end of the waitlist. */
    public boolean addParty(Party party) {
        if (party == null || line.contains(party)) {
            return false;
        }

        party.setPosition(line.size() + 1);
        return line.addToEnd(party);
    }

    /**
     * Inserts a party at a one-based waitlist position. Since LinkedChain
     * does not insert at an index, this method rebuilds the chain in order.
     */
    public boolean insertAt(Party party, int position) {
        if (party == null || line.contains(party)
            || position < 1 || position > line.size() + 1) {
            return false;
        }

        LinkedChain<Party> updatedLine = new LinkedChain<Party>();
        for (int index = 0; index < line.size(); index++) {
            if (index == position - 1) {
                updatedLine.addToEnd(party);
            }
            updatedLine.addToEnd(line.get(index));
        }
        if (position == line.size() + 1) {
            updatedLine.addToEnd(party);
        }

        line = updatedLine;
        updatePositions();
        return true;
    }

    /** Removes and returns the party at a one-based position. */
    public Party removeAt(int position) {
        Party party = findByPosition(position);
        if (party == null) {
            return null;
        }

        line.remove(party);
        updatePositions();
        return party;
    }

    /** Returns the first party without removing it. */
    public Party peekNext() {
        return findByPosition(1);
    }

    /** Removes the earliest party whose size fits the table capacity. */
    public Party removeFirstThatFits(int capacity) {
        for (int index = 0; index < line.size(); index++) {
            Party party = line.get(index);
            if (party.getSize() <= capacity) {
                line.remove(party);
                updatePositions();
                return party;
            }
        }
        return null;
    }

    /** Returns parties in their current waitlist order. */
    public Party[] getInOrder() {
        return line.toArray(new Party[line.size()]);
    }

    public int getCount() {
        return line.size();
    }

    public boolean isEmpty() {
        return line.isEmpty();
    }

    /** Finds a party by its one-based position. */
    private Party findByPosition(int position) {
        if (position < 1) {
            return null;
        }

        for (int index = 0; index < line.size(); index++) {
            Party party = line.get(index);
            if (party.getPosition() == position) {
                return party;
            }
        }
        return null;
    }

    /** Renumbers parties after an insertion or removal. */
    private void updatePositions() {
        for (int index = 0; index < line.size(); index++) {
            line.get(index).setPosition(index + 1);
        }
    }
}
