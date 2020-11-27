package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import agents.Audience;
import agents.Competitor;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;

public class WorldModel extends Repast3Launcher {
    private final static Logger LOGGER = Logger.getLogger("world");
    static final int MAX_PRICE = 15000;
    static final int PORT = 9090;
    static final int HIGH_CONFIDENCE = 10000;
    static final int MAX_TEAM = 100;

    private ContainerController cc;
    private final ArrayList<String> teams;

    private final ArrayList<Audience> audience;
    private final ArrayList<Competitor> competitors;
    private final HashMap<String, Integer> itemPrice;

    private int nAudience;
    private int nCompetitors;
    private int nItems;
    private float highConfidenceRate;

    public WorldModel() {
        teams = new ArrayList<>();
        audience = new ArrayList<>();
        competitors = new ArrayList<>();
        itemPrice = new HashMap<>();

        nAudience = 20;
        nCompetitors = 20;
        nItems = 20;
        highConfidenceRate = 0.2f;

        generateItems();
        generatePersons();
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

    public static Logger getLogger() {
        return LOGGER;
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

    public HashMap<String, Integer> getItemPrice() {
        return itemPrice;
    }

    private void generateItems() {
    }

    private void generatePersons() {
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
    }

    @Override
    public void setup() {
        super.setup();
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.loadModel(new WorldModel(), null, true);
    }
}
