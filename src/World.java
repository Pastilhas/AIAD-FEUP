import jade.core.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class World extends Agent {
    private final ArrayList<Person> audience;
    private final ArrayList<Person> competitors;
    private final HashMap<String, Integer> itemPriceMap;
    private final Random random;
    private final float selfconfidenceRate;
    private final int audienceSize;
    private final int competitorsSize;
    private final int maxItems;
    private WorldState state;

    World(float selfconfidenceRate, int audienceSize, int competitorsSize, int maxItems){
        audience  = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPriceMap = new HashMap<>();
        random = new Random();
        this.selfconfidenceRate = selfconfidenceRate;
        this.audienceSize = audienceSize;
        this.competitorsSize = competitorsSize;
        this.maxItems = maxItems;
        state = WorldState.GENERATE;
    }

}
