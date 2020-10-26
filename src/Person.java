import java.util.HashMap;
import java.util.Random;

public class Person {
    private final boolean selfconfidence;
    private final HashMap<String, Integer> itemPriceMap;
    private final Random random;

    Person(boolean selfconfidence, Random random) {
        this.selfconfidence = selfconfidence;
        itemPriceMap = new HashMap<>();
        this.random  = random;
    }
}
