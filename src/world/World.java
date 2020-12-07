package world;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import agents.Audience;
import agents.Competitor;
import agents.MyAgent.Phase;
import agents.WorldAgent;
import draw.AgentNode;
import jade.wrapper.StaleProxyException;
import sajas.wrapper.ContainerController;

public class World {
    private static final Logger LOGGER = Logger.getLogger("worldModel");
    private static final float HIGH_CONFIDENCE = 9999;
    private static final int MAX_TEAM = 100;
    public static final int MAX_PRICE = 15000;

    private WorldAgent worldAgent;
    private final WorldData worldData;
    private final long time;

    private final ArrayList<String> teams = new ArrayList<>(Arrays.asList("Green", "Yellow", "Blue", "Red"));
    private final ArrayList<Audience> audience = new ArrayList<>();
    private final ArrayList<Competitor> competitors = new ArrayList<>();
    private final ArrayList<AgentNode> nodes = new ArrayList<>();
    private final HashMap<String, Integer> itemPrice = new HashMap<>();

    private int round = 0;

    World(long time) {
        this.time = time;
        setupLogs();
        worldData = new WorldData(this);
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    public ArrayList<Audience> getAudience() {
        return audience;
    }

    public ArrayList<Competitor> getCompetitors() {
        return competitors;
    }

    public ArrayList<AgentNode> getNodes() {
        return nodes;
    }

    public HashMap<String, Integer> getItemPrice() {
        return itemPrice;
    }

    public int getRound() {
        return round;
    }

    private void setupLogs() {
        File directory = new File("logs/" + time);
        if (!directory.exists()) {
            directory.getParentFile().mkdirs();
            directory.mkdir();
        }

        setupLogger("logs/" + time + "/worldModel.log");
    }

    private void setupLogger(String path) {
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

    void generateItems(int maxItem) {
        Random rnd = new Random();
        for (int i = 0; i < maxItem; i++) {
            itemPrice.put(Integer.toString(i), rnd.nextInt(MAX_PRICE));
        }
    }

    void generateAgents(int maxAudi, int maxComp, int maxItem, float confRate, int w, int h) {
        Random rnd = new Random();
        String id = "audience_";
        Audience a;
        Competitor c;
        int max;

        for (int i = 0; i < maxAudi; i++) {
            if (rnd.nextFloat() < confRate)
                a = new Audience(id + i, time, this, rnd.nextFloat(), Color.CYAN, rnd.nextInt(w / 2), rnd.nextInt(h));
            else
                a = new Audience(id + i, time, this, HIGH_CONFIDENCE, Color.CYAN, rnd.nextInt(w / 2), rnd.nextInt(h));
            max = rnd.nextInt(maxItem);

            for (int j = 0; j < max; j++) {
                String item = Integer.toString(rnd.nextInt(maxItem));
                a.addItem(item, itemPrice.get(item));
            }

            for (String t : teams) {
                int ta = rnd.nextInt(MAX_TEAM + 1);
                a.addTeam(t, ta);
            }

            nodes.add(a.getNode());
            audience.add(a);
        }

        id = "competitor_";
        for (int i = 0; i < maxComp; i++) {
            c = new Competitor(id + i, time, this, Color.GREEN, w / 2 + rnd.nextInt(w / 2), rnd.nextInt(h));

            for (String t : teams) {
                int ta = rnd.nextInt(MAX_TEAM + 1);
                c.addTeam(t, ta);
            }

            nodes.add(c.getNode());
            competitors.add(c);
        }

        worldAgent = new WorldAgent("world", time, this);
    }

    public String[] startRound() {
        Random rnd = new Random();
        String item_id = Integer.toString(rnd.nextInt(itemPrice.size()));
        String item_price = Integer.toString(itemPrice.get(item_id));
        return new String[] { item_id, item_price };
    }

    public void endRound() {
        worldData.newRound();
        System.out.println("Ended round " + round);
        round++;
        System.out.println("Start round " + round);
        worldAgent.startRound();
    }

    public boolean isEnd() {
        for (Competitor p : competitors)
            if (p.phase != Phase.INIT)
                return false;
        for (Audience p : audience)
            if (p.phase != Phase.INIT)
                return false;
        return true;
    }

    public AgentNode getNode(String name) {
        if (name.startsWith("audience"))
            for (Audience a : audience)
                if (a.getLocalName().equals(name))
                    return a.getNode();

        if (name.startsWith("competitor"))
            for (Competitor c : competitors)
                if (c.getLocalName().equals(name))
                    return c.getNode();

        return null;
    }

    void launch(int maxAudi, int maxComp, int maxItem, float confRate, int w, int h) {
        setupLogs();
        generateItems(maxItem);
        generateAgents(maxAudi, maxComp, maxItem, confRate, w, h);
    }

    void startAgents(ContainerController cc) {
        for (Competitor c : competitors) {
            try {
                cc.acceptNewAgent(c.getId(), c).start();
            } catch (StaleProxyException e) {
                LOGGER.severe("Exception thrown while creating " + c.getId());
                e.printStackTrace();
                System.exit(2);
            }
        }

        for (Audience a : audience) {
            try {
                cc.acceptNewAgent(a.getId(), a).start();
            } catch (StaleProxyException e) {
                LOGGER.severe("Exception thrown while creating " + a.getId());
                e.printStackTrace();
                System.exit(2);
            }
        }

        for (Competitor c : competitors) {
            c.startConfidence();
        }

        for (Audience a : audience) {
            a.startConfidence();
        }

        try {
            cc.acceptNewAgent("world", worldAgent).start();
        } catch (StaleProxyException e) {
            LOGGER.severe("Adding WorldAgent");
            e.printStackTrace();
            System.exit(2);
        }
    }

    public Integer getPrice() {
        return worldAgent.getPrice();
    }
}
