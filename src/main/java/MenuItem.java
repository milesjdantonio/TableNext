public class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.name = name;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price; 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this == obj) {
            return this == obj;
        }  
        if (obj.getClass() != MenuItem.class) {
            return false;
        }

        MenuItem compare = (MenuItem) obj;

        return (this.getName().equalsIgnoreCase(compare.getName())) 
        && (this.getPrice() == compare.getPrice());
    }

    @Override
    public int hashCode() {
        return 31 * this.name.toLowerCase().hashCode() + Double.hashCode(this.price);
    }

    @Override
    public String toString() {
        return String.format("%s $%.2f", this.getName(), this.getPrice());
    }
}
