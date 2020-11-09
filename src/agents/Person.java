package agents;

import jade.core.Agent;

import java.util.HashMap;

public class Person extends Agent {
    private final String id;
    private final boolean selfconfidence;
    private final HashMap<String, Integer> itemPriceMap;

    public Person(String id, boolean selfconfidence) {
        this.id = id;
        this.selfconfidence = selfconfidence;
        itemPriceMap = new HashMap<>();
    }
}
