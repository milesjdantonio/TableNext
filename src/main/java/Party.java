import java.time.LocalDateTime;

public class Party {

    private final long id;
    private static long nextId = 0;
    private final String hostName;
    private int size;
    private int position;
    private boolean needAccessibility;
    private LocalDateTime reservationTime;
    private LinkedChain<MenuItem> order;

    public Party(String hostName, int size, boolean needAccessibility, LocalDateTime reservationTime) {
        if (hostName == null || hostName.isBlank()) {
            throw new IllegalArgumentException("Host name cannot be empty");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }

        this.id = nextId++;
        this.hostName = hostName;
        this.size = size;
        this.needAccessibility = needAccessibility;
        this.reservationTime = reservationTime;
        this.order = new LinkedChain<MenuItem>();
    }

    public long getId() {
        return this.id;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        this.size = size;
    }

    public boolean needAccessibility() {
        return this.needAccessibility;
    }

    public void setNeedAccessibility(boolean needAccessibility) {
        this.needAccessibility = needAccessibility;
    }

    public boolean hasReservation() {
        return this.reservationTime != null;
    }

    public LocalDateTime getReservationTime() {
        return this.reservationTime;
    }

    public boolean updateReservationTime(boolean moveLater) {
        if (reservationTime == null) {
            return false;
        }
        if (moveLater) {
            reservationTime = reservationTime.plusHours(1);
        } else {
            reservationTime = reservationTime.minusHours(1);
        }
        return true;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Position must be 1 or greater");
        }
        this.position = position;
    }

    public boolean addItem(MenuItem item) {
        if (item == null) {
            return false;
        }

        this.order.addToEnd(item);
        return true;
    }

    public boolean removeItem(MenuItem item) {
        if (item == null) {
            return false;
        }
        return this.order.remove(item);
    }

    public MenuItem[] getOrder() {
        return this.order.toArray(new MenuItem[this.order.size()]);
    }

    public double getOrderTotal() {
        double result = 0;
        for (MenuItem item : getOrder()) {
            result += item.getPrice();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Party)) {
            return false;
        }

        Party other = (Party) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Party:\n");
        sb.append("Host Name: ").append(hostName).append("\n");
        sb.append("Id: ").append(id).append("\n");
        sb.append("Size: ").append(size).append("\n");
        sb.append("Position: ").append(position).append("\n");
        sb.append("Reservation Time: ").append(reservationTime).append("\n");
        sb.append("Order: ").append(order.toString()).append("\n");
        return sb.toString();
    }
}
