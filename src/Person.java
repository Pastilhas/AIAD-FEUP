import java.util.HashMap;

public class Person {
    private final boolean selfconfidence;
    private final HashMap<String, Integer> itemPriceMap;
    private final World world;

    Person(boolean selfconfidence, World world) {
        this.selfconfidence = selfconfidence;
        itemPriceMap = new HashMap<>();
        this.world  = world;
    }
}
