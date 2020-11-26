package world;

import java.io.File;
/* import java.io.FileWriter; */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import agents.Audience;
import agents.Competitor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;

public class World extends Repast3Launcher {
    public static final int MAX_PRICE = 15000;
    static final int PORT = 9090;
    static final int HIGH_CONFIDENCE = 10000;
    static final int MAX_TEAM = 100;
    private final static Logger LOGGER = Logger.getLogger("world");

    private final ArrayList<Audience> audience;
    private final ArrayList<Competitor> competitors;
    private final HashMap<String, Integer> itemPrice;
    private final ArrayList<String> teams;
    private final int nAudience;
    private final int nCompetitors;
    private final int nItems;
    private final Runtime rt;
    private ContainerController cc;
    private final float highConfidenceRate;
    private final long time;

    World(int nAudience, int nCompetitors, int nItems, float highConfidenceRate, long time) {
        // Set variables
        this.nAudience = nAudience;
        this.nCompetitors = nCompetitors;
        this.nItems = nItems;
        this.highConfidenceRate = highConfidenceRate;
        this.time = time;

        audience = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new HashMap<>();
        teams = new ArrayList<>();

        rt = Runtime.instance();

        // Generate items and people
        teams.addAll(Arrays.asList("Amarelo", "Azul", "Vermelho", "Verde"));
        generateItems();
    }

    private void generateItems() {
        for (int i = 0; i < nItems; i++) {
            Random rnd = new Random();
            itemPrice.put(Integer.toString(i), rnd.nextInt(MAX_PRICE));
        }
    }

    private void generatePersons() {
        for (int i = 0; i < nAudience; i++) {
            try {
                String id = "audience_" + i;
                Random rnd = new Random();
                Audience p;
                if (rnd.nextFloat() < highConfidenceRate) {
                    p = new Audience(id, rnd.nextFloat(), time);
                } else {
                    p = new Audience(id, HIGH_CONFIDENCE, time);
                }

                int max = rnd.nextInt(nItems / 2);
                for (int j = 0; j < max; j++) {
                    int id_item = rnd.nextInt(nItems);
                    p.addItem(String.valueOf(id_item), itemPrice.get(String.valueOf(id_item)));
                }

                for (String t : teams) {
                    int ta = rnd.nextInt(MAX_TEAM + 1);
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

        for (int i = 0; i < nCompetitors; i++) {
            try {
                String id = "competitor_" + i;
                Random rnd = new Random();
                Competitor p = new Competitor(id, time);

                for (String t : teams) {
                    int ta = rnd.nextInt(MAX_TEAM + 1);
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

        for (Competitor c : competitors)
            c.startConfidence();
        for (Audience a : audience)
            a.startConfidence();
    }

    private void playRound(int round) {
        // 1. Select item
        Random rnd = new Random();
        String item_id = Integer.toString(rnd.nextInt(nItems));

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

        /* updateRoundPrices(round, itemPrice.get(item_id), guesses); */

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
        return winner;
    }

    @Override
    public String[] getInitParam() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "World - SAJaS Project";
    }

    @Override
    protected void launchJADE() {
        Profile p = new ProfileImpl("localhost", PORT, null, true);
        cc = rt.createMainContainer(p);
		generatePersons();
    }

    private static void setupLogger(String path) {
        try {
            LOGGER.setUseParentHandlers(false);
            FileHandler handler = new FileHandler(path);
            handler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s%n";
                private final Date dat = new Date();

                @Override
                public String format(LogRecord record) {
                    dat.setTime(record.getMillis());
                    String message = formatMessage(record);
                    return String.format(format, dat, record.getLevel().getLocalizedName(), message);
                }
            });
            LOGGER.addHandler(handler);
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

    // int nAudience, int nCompetitors, int nItems, float highConfidenceRate, int tries, int rounds
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%s");

        int nAudience = Integer.parseInt(args[0]);
        int nCompetitors = Integer.parseInt(args[1]);
        int nItems = Integer.parseInt(args[2]);
        float highConfidenceRate = Float.parseFloat(args[3]);

        int nTries = Integer.parseInt(args[4]);
        int nRounds = Integer.parseInt(args[5]);

        int tries = 0;
        int round;
        World world;


        while (tries < nTries) {
            round = 0;
            long time = setupLogs();
            world = new World(nAudience, nCompetitors, nItems, highConfidenceRate, time);
            SimInit init = new SimInit();
            init.loadModel(world, null, false);
            LOGGER.info("World created");

            while (round < nRounds) {
                LOGGER.info("Start round " + round);
                world.playRound(round);
                LOGGER.info("End round " + round);
                round++;
            }
            tries++;
        }

        System.exit(0);
    }
}
