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
import draw.AgentNode;
import agents.Person;
import agents.WorldAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;

public class WorldModel extends Repast3Launcher {
    private final static Logger LOGGER = Logger.getLogger("worldModel");

    private static final float HIGH_CONFIDENCE = 9999;
    private static final int MAX_TEAM = 100;
    public static final int MAX_PRICE = 15000;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private ContainerController cc;
    private final ArrayList<String> teams;
    protected final WorldAgent worldAgent;
    protected final WorldData worldData;
	private DisplaySurface dsurf;

    protected final ArrayList<Audience> audience;
    protected final ArrayList<Competitor> competitors;
    protected final HashMap<String, Integer> itemPrice;

    private int nAudience;
    private int nCompetitors;
    private int nItems;
    private int nRounds;
    private float highConfidenceRate;

    protected int round;
    private boolean batchMode;

    public WorldModel() {
        teams = new ArrayList<String>(Arrays.asList("Red", "Yellow", "Green", "Blue"));
        audience = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new HashMap<>();

        nAudience = 4;
        nCompetitors = 4;
        nItems = 20;
        nRounds = 5;
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
                a = new Audience(id + i, time, this, rnd.nextFloat(), Color.CYAN, rnd.nextInt(WIDTH/2), rnd.nextInt(HEIGHT));
            else
                a = new Audience(id + i, time, this, HIGH_CONFIDENCE, Color.CYAN, rnd.nextInt(WIDTH/2), rnd.nextInt(HEIGHT));
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
            c = new Competitor(id + i, time, this, Color.GREEN, WIDTH/2 + rnd.nextInt(WIDTH/2), rnd.nextInt(HEIGHT));

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

	public AgentNode getNode(String name) {
        if(name.startsWith("audience"))
            for(Audience a : audience)
                if(a.getLocalName().equals(name))
                    return a.getNode();

        if(name.startsWith("competitor"))
            for(Competitor c : competitors)
                if(c.getLocalName().equals(name))
                    return c.getNode();

        return null;
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

    @Override
    public void begin() {
        super.begin();
        if(!batchMode) setupDisplay();
    }

    private void setupDisplay() {
        ArrayList<AgentNode> nodes = new ArrayList<>();
        for(Audience a : audience) nodes.add(a.getNode());
        for(Competitor a : competitors) nodes.add(a.getNode());

		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Service Consumer/Provider Display");
		registerDisplaySurface("Service Consumer/Provider Display", dsurf);
		Network2DDisplay display = new Network2DDisplay(nodes,WIDTH,HEIGHT);
		dsurf.addDisplayableProbeable(display, "Network Display");
        dsurf.addZoomable(display);
        addSimEventListener(dsurf);
		dsurf.display();

		getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
        WorldModel model = new WorldModel();
        try {
            boolean x = Boolean.parseBoolean(args[0]);
            if(x) {
                model.parseParams(args);
                init.loadModel(model, null, true);
            }
        } catch (Exception e) {}
        init.loadModel(model, null, false);
    }

    private void parseParams(String[] args) {
        batchMode = true;
        try { int x = Integer.parseInt(args[1]); nAudience = x;  } catch (Exception e) { System.err.println("PARAMS: number of audience not defined"); }
        try { int x = Integer.parseInt(args[2]); nCompetitors = x;  } catch (Exception e) { System.err.println("PARAMS: number of competitors not defined"); }
        try { int x = Integer.parseInt(args[3]); nItems = x;  } catch (Exception e) { System.err.println("PARAMS: number of items not defined"); }
        try { float x = Float.parseFloat(args[5]); highConfidenceRate = x;  } catch (Exception e) { System.err.println("PARAMS: rate of high confidence not defined"); }
    }
}
