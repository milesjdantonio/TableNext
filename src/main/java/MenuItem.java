public class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        // Prices must be finite so order totals remain valid numbers.
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                "Price must be finite and nonnegative");
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
        // Validate before assignment to preserve the previous valid price.
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                "Price must be finite and nonnegative");
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
        // Fold case independently of the system's default locale.
        int nameHash = 0;
        for (int codePoint : name.codePoints().toArray()) {
            int folded = Character.toLowerCase(
                Character.toUpperCase(codePoint));
            nameHash = 31 * nameHash + folded;
        }

        // equals treats positive and negative zero as equal.
        double normalizedPrice = price == 0.0 ? 0.0 : price;
        return 31 * nameHash + Double.hashCode(normalizedPrice);
    }
    
    @Override
    public String toString() {
        return String.format("%s $%.2f", this.getName(), this.getPrice());
    }
}
