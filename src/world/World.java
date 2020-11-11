package world;

import agents.Audience;
import agents.Competitor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class World {
    static final int maxPrice = 15000;

    private final ArrayList<Audience> audience;
    private final ArrayList<Competitor> competitors;
    private final HashMap<String, Integer> itemPrice;
    private final ArrayList<String> teams;
    private final int maxAudience;
    private final int maxCompetitors;
    private final int maxItems;
    private final Runtime rt;
    private final Profile p;
    private final ContainerController cc;
    private final float selfConfidenceRate;

    World(int maxAudience, int maxCompetitors, int maxItems, float selfConfidenceRate) {
        // Set variables
        this.maxAudience = maxAudience;
        this.maxCompetitors = maxCompetitors;
        this.maxItems = maxItems;
        this.selfConfidenceRate = selfConfidenceRate;

        audience = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new HashMap<>();
        teams = new ArrayList<>();

        rt = Runtime.instance();
        p = new ProfileImpl(true);
        cc = rt.createMainContainer(p);

        // Generate items and people
        teams.addAll(Arrays.asList("Amarelo", "Azul", "Vermelho", "Verde"));
        generateItems();
        generatePersons();
    }

    private void generateItems() {
        for (int i = 0; i < maxItems; i++) {
            Random rnd = new Random();
            itemPrice.put(Integer.toString(i), rnd.nextInt(maxPrice));
        }
    }

    private void generatePersons() {
        for (int i = 0; i < maxAudience; i++) {
            try {
                String id = "audience_" + i;
                Random rnd = new Random();
                Audience p;
                if (rnd.nextFloat() < selfConfidenceRate) {
                    p = new Audience(id, rnd.nextFloat());
                } else {
                    p = new Audience(id, 1000);
                }


                int max = rnd.nextInt(maxItems / 2);
                for (int j = 0; j < max; j++) {
                    int id_item = rnd.nextInt(maxItems);
                    p.addItem(id_item, itemPrice.get(String.valueOf(id_item)));
                }

                for (String t : teams) {
                    int ta = rnd.nextInt(101);
                    p.addTeam(t, ta);
                }

                audience.add(p);
                AgentController ac = this.cc.acceptNewAgent(id, p);
                ac.start();
            } catch (Exception e) {
                System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
            }
        }

        for (int i = 0; i < maxCompetitors; i++) {
            try {
                String id = "competitor_" + i;
                Random rnd = new Random();
                Competitor p = new Competitor(id);

                for (String t : teams) {
                    int ta = rnd.nextInt(101);
                    p.addTeam(t, ta);
                }

                competitors.add(p);
                AgentController ac = this.cc.acceptNewAgent(id, p);
                ac.start();
            } catch (Exception e) {
                System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
            }
        }
    }

    private void playRound() {
    }

    // int maxAudience, int maxCompetitors, int maxItems, int tries, int rounds
    public static void main(String[] args) {
        int tries = 0;
        int round = 0;
        World world;

        while (tries < Integer.parseInt(args[4])) {
            world = new World(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Float.parseFloat(args[3]));

            while (round < Integer.parseInt(args[5])) {
                world.playRound();
                round++;
            }

            try {
                world.cc.kill();
                world.rt.shutDown();
            } catch (Exception e) {
                System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
            }

            tries++;
        }

    }
}
