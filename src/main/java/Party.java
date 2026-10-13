import java.time.LocalDateTime;

public class Party {

    private final long id;
    private static long nextId = 0;
    private final String hostName;
    private int size;
    private int position;
    private LocalDateTime reservationTime;
    private LinkedChain<MenuItem> order;

    public Party(String hostName, int size, LocalDateTime reservationTime) {
            this.id = nextId++;
            this.hostName = hostName;
            this.size = size;
            this.reservationTime = reservationTime;
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
        this.size = size;
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
            return true;
        }
        reservationTime = reservationTime.minusHours(1);
        return false;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean addItem(MenuItem item) {
        if (item == null) {
            return false;
        }
        
        this.order.add(item);
        return true;
    }

    public boolean removeItem(MenuItem item) {
        if (item == null) {
            return false;
        }

        for (int i = 0; i < order.size(); i++) {
            if (order.get(i).equals(item)) {
                order.remove(item);
                return true;
            }
        }
        return false;
    }

    public MenuItem[] getOrder() {
        MenuItem[] result = new MenuItem[order.size()];
        for (int i = 0; i < order.size(); i++) {
            result[i] = order.get(i);
        }
        return result; 
    }

    public double getOrderTotal() {
        double result = 0;
        for (int i = 0; i < order.size(); i++) {
            result += order.get(i).getPrice();
        }
        return result;
    }

    public boolean equal(Party obj) {
        return this.id == obj.id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Party:\n");
        sb.append("Hostname: ").append(hostName).append("\n");
        sb.append("Id: ").append(id).append("\n");
        sb.append("Size: ").append(size).append("\n");
        sb.append("Position: ").append(position).append("\n");
        sb.append("Reservation Time: ").append(reservationTime).append("\n");
        sb.append("Order: ").append(order.toString()).append("\n"); //Needs Menu Item To String
        return sb.toString();
    }
}
