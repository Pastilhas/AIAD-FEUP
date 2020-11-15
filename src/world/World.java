package world;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import agents.Audience;
import agents.Competitor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class World extends jade.Boot {
    static final int MAX_PRICE = 15000;
    private final static Logger LOGGER = Logger.getLogger("world");

    private final ArrayList<Audience> audience;
    private final ArrayList<Competitor> competitors;
    private final HashMap<String, Integer> itemPrice;
    private final HashMap<String, Integer> winners;
    private final HashMap<Integer, Integer[]> roundPrices;
    private final ArrayList<String> teams;
    private final int nAudience;
    private final int nCompetitors;
    private final int nItems;
    private final Runtime rt;
    private final ContainerController cc;
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
        winners = new HashMap<>();
        roundPrices = new HashMap<>();
        teams = new ArrayList<>();

        rt = Runtime.instance();
        Profile p = new ProfileImpl("localhost", 9090, null, true);
        cc = rt.createMainContainer(p);

        // Generate items and people
        teams.addAll(Arrays.asList("Amarelo", "Azul", "Vermelho", "Verde"));
        generateItems();
        generatePersons();

        for (Competitor c : competitors)
            winners.put(c.getLocalName(), 0);
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
        System.setProperty("java.util.logging.SimpleFormatter.format", "");

        int nAudience = Integer.parseInt(args[0]);
        int nCompetitors = Integer.parseInt(args[1]);
        int nItems = Integer.parseInt(args[2]);
        float highConfidenceRate = Float.parseFloat(args[3]);
        int nTries = Integer.parseInt(args[4]);
        int nRounds = Integer.parseInt(args[5]);

        int tries = 0;
        int round;
        World world;
        HashMap<String, Integer[]> compWin = new HashMap<>();
        HashMap<Integer, Integer[]> avgRoundPrices = new HashMap<>();

        while (tries < nTries) {
            round = 0;
            long time = setupLogs();
            world = new World(nAudience, nCompetitors, nItems, highConfidenceRate, time);
            LOGGER.info("World created");

            while (round < nRounds) {
                LOGGER.info("Start round " + round);
                world.playRound(round);
                LOGGER.info("End round " + round);
                round++;
            }

            HashMap<String, Integer[]> m = world.getCompWin();
            for (Map.Entry<String, Integer[]> e : m.entrySet()) {
                String key = e.getKey();
                Integer[] avg = new Integer[2];
                avg[0] = e.getValue()[0];
                avg[1] = e.getValue()[1];
                if (compWin.get(key) != null) {
                    avg[0] = (avg[0] + compWin.get(key)[0]) / 2;
                    avg[1] = (avg[1] + compWin.get(key)[1]) / 2;
                }
                compWin.put(key, avg);
            }

            for (Map.Entry<Integer, Integer[]> e : world.roundPrices.entrySet()) {
                Integer key = e.getKey();
                Integer[] avg = new Integer[2];
                avg[0] = e.getValue()[0];
                avg[1] = e.getValue()[1];
                if (avgRoundPrices.get(key) != null) {
                    avg[0] = (avg[0] + avgRoundPrices.get(key)[0]) / 2;
                    avg[1] = (avg[1] + avgRoundPrices.get(key)[1]) / 2;
                }
                avgRoundPrices.put(key, avg);
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

        writeCompWins(compWin);
        writeRoundPrice(avgRoundPrices);

        System.exit(0);
    }

    private HashMap<String, Integer[]> getCompWin() {
        HashMap<String, Integer[]> compWin = new HashMap<>();

        for (Audience a : audience) {
            for (Competitor c : competitors)
                a.checkCompetitor(c.getLocalName(), c.getTeam());
        }

        for (Competitor c : competitors) {
            String id = c.getLocalName();
            Integer[] a = new Integer[2];
            a[0] = getWins(c);
            a[1] = getHelp(c);
            compWin.put(id, a);
        }

        return compWin;
    }

    private Integer getHelp(Competitor c) {
        Integer helpers = 0;
        for (Audience a : audience) {
            if (a.getCompatibility().get(c.getLocalName()))
                helpers++;
        }
        return helpers;
    }

    private Integer getWins(Competitor c) {
        return winners.get(c.getLocalName());
    }

    private static void writeRoundPrice(HashMap<Integer, Integer[]> avgRoundPrices) {
        File f = new File("logs/roundprice.csv");
        FileWriter writer = null;
        try {
            f.createNewFile();
            writer = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Integer, Integer[]> e : avgRoundPrices.entrySet()) {
            try {
                if (writer != null)
                    writer.write(e.getKey() + "," + e.getValue()[0] + "," + e.getValue()[1] + "\n");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCompWins(HashMap<String, Integer[]> compWin) {
        File f = new File("logs/competitor_wins.csv");
        FileWriter writer = null;
        try {
            f.createNewFile();
            writer = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writer != null)
            for (Map.Entry<String, Integer[]> e : compWin.entrySet()) {
                try {
                    writer.write(e.getKey() + "," + e.getValue()[0] + "," + e.getValue()[1] + "\n");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    p = new Audience(id, 1000, time);
                }

                int max = rnd.nextInt(nItems / 2);
                for (int j = 0; j < max; j++) {
                    int id_item = rnd.nextInt(nItems);
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

        for (int i = 0; i < nCompetitors; i++) {
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

        updateRoundPrices(round, itemPrice.get(item_id), guesses);

        // 6. End Round
        for (Audience au : audience) {
            au.endRound(itemPrice.get(item_id));
        }
        for (Competitor cm : competitors) {
            cm.endRound(itemPrice.get(item_id));
        }
    }

    private void updateRoundPrices(int round, int price, HashMap<String, Integer> guesses) {
        guesses.values().removeIf(v -> v == null);
        ArrayList<Integer> diffs = new ArrayList<>();
        for (Integer i : guesses.values())
            diffs.add(Math.abs(price - i));

        Integer[] a = new Integer[2];
        a[0] = Collections.min(diffs);
        a[1] = Collections.max(diffs);

        roundPrices.put(round, a);
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

        winners.merge(winner, 1, Integer::sum);
        return winner;
    }
}
