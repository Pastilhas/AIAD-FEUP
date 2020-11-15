package world;

import agents.Audience;
import agents.Competitor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class World extends jade.Boot {
    static final int MAX_PRICE = 15000;
    private final static Logger LOGGER = Logger.getLogger("world");

    private final ArrayList<Audience> audience;
    private final ArrayList<Competitor> competitors;
    private final HashMap<String, Integer> itemPrice;
    private final HashMap<String, Integer> winners;
    private final ArrayList<String> teams;
    private final int maxAudience;
    private final int maxCompetitors;
    private final int maxItems;
    private final Runtime rt;
    private final ContainerController cc;
    private final float selfConfidenceRate;
    private final long time;

    World(int nAudience, int nCompetitors, int nItems, float highConfidenceRate, long time) {
        // Set variables
        this.maxAudience = nAudience;
        this.maxCompetitors = nCompetitors;
        this.maxItems = nItems;
        this.selfConfidenceRate = highConfidenceRate;
        this.time = time;

        audience = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new HashMap<>();
        winners = new HashMap<>();
        teams = new ArrayList<>();

        rt = Runtime.instance();
        Profile p = new ProfileImpl("localhost", 9090, null, true);
        cc = rt.createMainContainer(p);

        // Generate items and people
        teams.addAll(Arrays.asList("Amarelo", "Azul", "Vermelho", "Verde"));
        generateItems();
        generatePersons();
    }

    private static void setupLogger(String path) {
        try {
            FileHandler handler = new FileHandler(path);
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            LOGGER.addHandler(handler);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Exception thrown while setting up world logger.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static long setupLogs() {
        long time = System.currentTimeMillis();

        File directory = new File("logs/" + time);
        if (!directory.exists()) {
            directory.getParentFile().mkdirs();
            directory.mkdir();
        }

        setupLogger("logs/" + time + "/" + "world.log");
        return time;
    }

    // int maxAudience, int maxCompetitors, int maxItems, float selfConfidenceRate, int tries, int rounds
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        int nAudience = Integer.parseInt(args[0]);
        int nCompetitors = Integer.parseInt(args[1]);
        int nItems = Integer.parseInt(args[2]);
        float highConfidenceRate = Float.parseFloat(args[3]);
        int nTries = Integer.parseInt(args[4]);
        int nRounds = Integer.parseInt(args[5]);

        int tries = 0;
        int round;
        World world;
        HashMap<Integer, Integer> teamWin = new HashMap<>();

        while (tries < nTries) {
            round = 0;
            long time = setupLogs();
            world = new World(nAudience, nCompetitors, nItems, highConfidenceRate, time);
            LOGGER.info("World created");

            while (round < nRounds) {
                LOGGER.info("Start round " + round);
                world.playRound();
                LOGGER.info("End round " + round);
                round++;
            }

            for (Competitor c : world.competitors) {
                Map.Entry<Integer, Integer> e = world.getInfo(c).entrySet().iterator().next();
                teamWin.merge(e.getKey(), e.getValue(), Integer::sum);
            }


            try {
                world.cc.kill();
                world.rt.shutDown();
            } catch (Exception e) {
                LOGGER.severe("Exception thrown while shutting down.");
                e.printStackTrace();
                System.exit(1);
            }

            tries++;
        }

        File f = new File("logs/csv");
        FileWriter writer = null;
        try {
            f.createNewFile();
            writer = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Integer, Integer> e : teamWin.entrySet()) {
            try {
                if (writer != null) writer.write(e.getKey() + "," + e.getValue() + "\n");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private HashMap<Integer, Integer> getInfo(Competitor c) {
        int avg = 0;
        for (Integer i : c.getTeam().values()) avg += i;
        avg /= c.getTeam().size();

        Integer w = winners.get(c.getLocalName());
        if (w == null) w = 0;

        HashMap<Integer, Integer> m = new HashMap<>();
        m.put(avg,w);

        return m;
    }

    private void generateItems() {
        for (int i = 0; i < maxItems; i++) {
            Random rnd = new Random();
            itemPrice.put(Integer.toString(i), rnd.nextInt(MAX_PRICE));
        }
    }

    private void generatePersons() {
        for (int i = 0; i < maxAudience; i++) {
            try {
                String id = "audience_" + i;
                Random rnd = new Random();
                Audience p;
                if (rnd.nextFloat() < selfConfidenceRate) {
                    p = new Audience(id, rnd.nextFloat(), time);
                } else {
                    p = new Audience(id, 1000, time);
                }


                int max = rnd.nextInt(maxItems / 2);
                for (int j = 0; j < max; j++) {
                    int id_item = rnd.nextInt(maxItems);
                    p.addItem(String.valueOf(id_item), itemPrice.get(String.valueOf(id_item)));
                }

                for (String t : teams) {
                    int ta = rnd.nextInt(101);
                    p.addTeam(t, ta);
                }

                audience.add(p);
                AgentController ac = this.cc.acceptNewAgent(id, p);
                ac.start();
            } catch (Exception e) {
                LOGGER.severe("Exception thrown while creating audience_" + i + ".");
                e.printStackTrace();
                System.exit(2);
            }
        }

        for (int i = 0; i < maxCompetitors; i++) {
            try {
                String id = "competitor_" + i;
                Random rnd = new Random();
                Competitor p = new Competitor(id, time);

                for (String t : teams) {
                    int ta = rnd.nextInt(101);
                    p.addTeam(t, ta);
                }

                competitors.add(p);
                AgentController ac = this.cc.acceptNewAgent(id, p);
                ac.start();
            } catch (Exception e) {
                LOGGER.severe("Exception thrown while creating competitor_" + i + ".");
                e.printStackTrace();
                System.exit(2);
            }
        }

        for (Competitor c : competitors) c.startConfidence();
        for (Audience a : audience) a.startConfidence();
    }

    private void playRound() {
        // 1. Select item
        Random rnd = new Random();
        String item_id = Integer.toString(rnd.nextInt(maxItems));

        LOGGER.info("Item selected: " + item_id);

        // 2. Tell item to audience
        for (Audience au : audience) {
            au.startRound(item_id);
        }

        // Wait audience to decide guesses
        HashSet<String> readyAudience = new HashSet<>();
        while (readyAudience.size() < audience.size()) {
            for (Audience au : audience) {
                if (au.ready) {
                    readyAudience.add(au.getLocalName());
                }
            }
        }

        LOGGER.info("Audience finished guessing.");

        // 3. Competitors send request
        for (Competitor cm : competitors) {
            cm.startRound();
        }

        // 4. Wait for competitors
        HashMap<String, Integer> guesses = new HashMap<>();
        while (guesses.size() < competitors.size()) {
            for (Competitor cm : competitors) {
                if (cm.ready) {
                    guesses.putIfAbsent(cm.getLocalName(), cm.getGuess());
                }
            }
        }

        LOGGER.info("Competitors finished guessing.");

        // 5. Declare winner
        String winner = declareWinner(item_id, guesses);

        LOGGER.info("The winner was: " + winner);

        // 6. End Round
        for (Audience au : audience) {
            au.endRound(itemPrice.get(item_id));
        }
        for (Competitor cm : competitors) {
            cm.endRound(itemPrice.get(item_id));
        }
    }

    private String declareWinner(String item_id, HashMap<String, Integer> guesses) {
        String winner = "FAILED";
        int guess = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : guesses.entrySet()) {
            if (Math.abs(itemPrice.get(item_id) - entry.getValue()) < Math.abs(itemPrice.get(item_id) - guess)) {
                winner = entry.getKey();
                guess = entry.getValue();
            }
        }

        Integer w = winners.get(winner);
        if (w == null) w = 1;
        else w += 1;
        winners.put(winner, w);

        return winner;
    }
}
