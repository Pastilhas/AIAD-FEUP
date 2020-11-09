package world;

import agents.Person;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class World {
    static final int maxPrice = 15000;

    private final ArrayList<Person> audience;
    private final ArrayList<Person> competitors;
    private final ArrayList<Integer> itemPrice;
    private final ArrayList<String> teams;
    private final float selfconfidenceRate;
    private final int maxAudience;
    private final int maxCompetitors;
    private final int maxItems;
    private final Runtime rt;
    private final Profile p;
    private final ContainerController cc;

    World(float selfconfidenceRate, int maxAudience, int maxCompetitors, int maxItems) {
        // Set variables
        this.selfconfidenceRate = selfconfidenceRate;
        this.maxAudience = maxAudience;
        this.maxCompetitors = maxCompetitors;
        this.maxItems = maxItems;

        audience  = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new ArrayList<>();
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
            itemPrice.add(rnd.nextInt(maxPrice));
        }
    }

    private void generatePersons() {
        for (int i = 0; i < maxAudience; i++) {
            try {
                String id = "audience_" + i;
                Random rnd = new Random();
                Person p = new Person(id, rnd.nextFloat() < selfconfidenceRate);
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
                Person p = new Person(id, rnd.nextFloat() < selfconfidenceRate);
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

    // float selfconfidenceRate, int maxAudience, int maxCompetitors, int maxItems, int tries, int rounds
    public static void main(String[] args) {
        int tries = 0;
        int round = 0;
        World world;

        while(tries < Integer.parseInt(args[4])) {
            world = new World(Float.parseFloat(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));

            while(round < Integer.parseInt(args[5])) {
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
