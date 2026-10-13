public class TableNextApp {
   
    // Needed for Party.java move as needed
    private long nextId = 1; 

    private long createId() {
        return nextId++;
    }
}
