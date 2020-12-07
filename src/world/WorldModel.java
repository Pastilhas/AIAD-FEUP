package world;

import java.util.ArrayList;

import draw.AgentNode;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;

public class WorldModel extends Repast3Launcher {
    public static final int DISPLAY_WIDTH = 1280;
    public static final int DISPLAY_HEIGHT = 720;

    private ContainerController cc;
    private DisplaySurface dsurf;
    private World world;

    private boolean batchMode;
    private int nAudience = 4;
    private int nCompetitors = 4;
    private int nItems = 10;
    private float highConfidenceRate = 0.2f;

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

        world.launch(nAudience, nCompetitors, nItems, highConfidenceRate, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        world.startAgents(cc);
    }

    @Override
    public void begin() {
        world = new World(System.currentTimeMillis());
        super.begin();
        if (!batchMode)
            setupDisplay();
    }

    private void setupDisplay() {
        ArrayList<AgentNode> nodes = world.getNodes();

        if (dsurf != null)
            dsurf.dispose();
        dsurf = new DisplaySurface(this, "Agents Display");
        registerDisplaySurface("Agents Display", dsurf);
        Network2DDisplay display = new Network2DDisplay(nodes, DISPLAY_WIDTH, DISPLAY_HEIGHT);
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
            if (x) {
                model.parseParams(args);
                init.loadModel(model, null, true);
            }
        } catch (Exception e) {
        }
        init.loadModel(model, null, false);
    }

    private void parseParams(String[] args) {
        batchMode = true;
        try {
            nAudience = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("PARAMS: number of audience not defined");
        }
        try {
            nCompetitors = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.err.println("PARAMS: number of competitors not defined");
        }
        try {
            nItems = Integer.parseInt(args[3]);
        } catch (Exception e) {
            System.err.println("PARAMS: number of items not defined");
        }
        try {
            highConfidenceRate = Float.parseFloat(args[5]);
        } catch (Exception e) {
            System.err.println("PARAMS: rate of high confidence not defined");
        }
    }
}
