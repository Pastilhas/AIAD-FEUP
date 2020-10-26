import jade.core.Agent;

import java.util.ArrayList;
import java.util.Random;

public class World extends Agent {
    static final int maxPrice = 15000;

    private final ArrayList<Person> audience;
    private final ArrayList<Person> competitors;
    private final ArrayList<Integer> itemPrice;
    private final Random random;
    private final float selfconfidenceRate;
    private final int maxAudience;
    private final int maxCompetitors;
    private final int maxItems;
    private WorldState state;

    World(float selfconfidenceRate, int maxAudience, int maxCompetitors, int maxItems) {
        audience  = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new ArrayList<>();
        random = new Random();
        this.selfconfidenceRate = selfconfidenceRate;
        this.maxAudience = maxAudience;
        this.maxCompetitors = maxCompetitors;
        this.maxItems = maxItems;
        state = WorldState.GENERATE;
    }

    private Integer generateItem() {
        return random.nextInt(maxPrice);
    }

    private void generateItems() {
        for (int i = 0; i < maxItems; i++) {
            itemPrice.add(generateItem());
        }
    }

    private Person generatePerson() {
        return new Person(random.nextFloat() < selfconfidenceRate, this);
    }

    private void generatePersons() {
        for (int i = 0; i < maxAudience; i++) {
            audience.add(generatePerson());
        }

        for (int i = 0; i < maxCompetitors; i++) {
            competitors.add(generatePerson());
        }
    }


}
