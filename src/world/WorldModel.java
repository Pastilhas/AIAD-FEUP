package world;

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
import agents.Person;
import agents.WorldAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;

public class WorldModel extends Repast3Launcher {
    private final static Logger LOGGER = Logger.getLogger("worldModel");

    private static final float HIGH_CONFIDENCE = 9999;
    private static final int MAX_TEAM = 100;
    public static final int MAX_PRICE = 15000;

    private ContainerController cc;
    private final ArrayList<String> teams;
    protected final WorldAgent worldAgent;
    protected final WorldData worldData;

    protected final ArrayList<Audience> audience;
    protected final ArrayList<Competitor> competitors;
    protected final HashMap<String, Integer> itemPrice;

    private int nAudience;
    private int nCompetitors;
    private int nItems;
    private float highConfidenceRate;

    protected int round;

    public WorldModel() {
        teams = new ArrayList<String>(Arrays.asList("Red", "Yellow", "Green", "Blue"));
        audience = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new HashMap<>();

        nAudience = 4;
        nCompetitors = 4;
        nItems = 20;
        highConfidenceRate = 0.2f;

        long time = System.currentTimeMillis();
        setupLogs(time);
        generateItems();
        generatePersons(time);
        worldAgent = new WorldAgent("world", time, this);
        worldData = new WorldData(this);
    }

    public float getHighConfidenceRate() {
        return highConfidenceRate;
    }

    public void setHighConfidenceRate(float highConfidenceRate) {
        this.highConfidenceRate = highConfidenceRate;
    }

    public int getnItems() {
        return nItems;
    }

    public void setnItems(int nItems) {
        this.nItems = nItems;
    }

    public int getnCompetitors() {
        return nCompetitors;
    }

    public void setnCompetitors(int nCompetitors) {
        this.nCompetitors = nCompetitors;
    }

    public int getnAudience() {
        return nAudience;
    }

    public void setnAudience(int nAudience) {
        this.nAudience = nAudience;
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    private void generateItems() {
        Random rnd = new Random();
        for (int i = 0; i < nItems; i++) {
            itemPrice.put(Integer.toString(i), rnd.nextInt(MAX_PRICE));
        }
    }

    private void setupLogs(long time) {
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

    private void generatePersons(long time) {
        Random rnd = new Random();
        String id = "audience_";
        Audience a;
        Competitor c;
        int max;

        for (int i = 0; i < nAudience; i++) {
            if (rnd.nextFloat() < highConfidenceRate)
                a = new Audience(id + i, rnd.nextFloat(), time);
            else
                a = new Audience(id + i, HIGH_CONFIDENCE, time);
            max = rnd.nextInt(nItems);

            for (int j = 0; j < max; j++) {
                String item = Integer.toString(rnd.nextInt(nItems));
                a.addItem(item, itemPrice.get(item));
            }

            for (String t : teams) {
                int ta = rnd.nextInt(MAX_TEAM + 1);
                a.addTeam(t, ta);
            }

            audience.add(a);
        }

        id = "competitor_";
        for (int i = 0; i < nCompetitors; i++) {
            c = new Competitor(id + i, time);

            for (String t : teams) {
                int ta = rnd.nextInt(MAX_TEAM + 1);
                c.addTeam(t, ta);
            }

            competitors.add(c);
        }
    }

    public String[] startRound() {
        Random rnd = new Random();
        String item_id = Integer.toString(rnd.nextInt(nItems));
        String item_price = Integer.toString(itemPrice.get(item_id));
        return new String[] { item_id, item_price };
    }

    public void endRound() {
        worldData.newRound();
        round++;
        worldAgent.startRound();
    }

    public boolean isEnd() {
        for(Person p : competitors) 
            if(p.phase != Phase.INIT)
                return false;
        for(Person p : audience) 
            if(p.phase != Phase.INIT)
                return false;
        return true;
    }

    @Override
    public String[] getInitParam() {
        return new String[] { "nAudience", "nCompetitors", "nItems", "highConfidenceRate" };
    }

    @Override
    public String getName() {
        return "World Model";
    }

    @Override
    protected void launchJADE() {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        cc = rt.createMainContainer(p);
        launchAgents();
    }

    private void launchAgents() {
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

    private void parseParams(String[] args) {
        try { int x = Integer.parseInt(args[1]); nAudience = x;  } catch (Exception e) { System.err.println("PARAMS: number of audience not defined"); }
        try { int x = Integer.parseInt(args[2]); nCompetitors = x;  } catch (Exception e) { System.err.println("PARAMS: number of competitors not defined"); }
        try { int x = Integer.parseInt(args[3]); nItems = x;  } catch (Exception e) { System.err.println("PARAMS: number of items not defined"); }
        try { float x = Float.parseFloat(args[5]); highConfidenceRate = x;  } catch (Exception e) { System.err.println("PARAMS: rate of high confidence not defined"); }
    }

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void begin() {
        super.begin();
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
        try {
            boolean x = Boolean.parseBoolean(args[0]);
            if(x) init.loadModel(new WorldModel(), null, true);
        } catch (Exception e) {}

        WorldModel model = new WorldModel();
        model.parseParams(args);
        init.loadModel(model, null, true);
    }
}
