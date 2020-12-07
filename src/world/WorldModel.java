package world;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import draw.AgentNode;
import jade.core.Profile;
import jade.core.ProfileImpl;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;

public class WorldModel extends Repast3Launcher {
    public static final int DISPLAY_WIDTH = 1280;
    public static final int DISPLAY_HEIGHT = 720;

    private ContainerController cc;
    private DisplaySurface dsurf;
    private OpenSequenceGraph plot;
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
        if (world != null)
            world.exiting();
        long time = System.currentTimeMillis();
        world = new World(time);
        paramsToFile(time);
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

        if (plot != null)
            plot.dispose();
        plot = new OpenSequenceGraph("Accuracy Plot", this);
        plot.setXRange(0, 5000);
        plot.setYRange(0, World.MAX_PRICE);
        plot.setAxisTitles("time", "Average difference between guess and target price");
        plot.addSequence("N", new Sequence() {
            @Override
            public double getSValue() {
                return world.getPlotValue();
            }
        });
        plot.display();

        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plot, "step", Schedule.LAST);
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
        WorldModel model = new WorldModel();

        java.lang.Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Exiting simulation.");
                model.world.exiting();
            }
        }));

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

    private void paramsToFile(long time) {
        File file = new File("logs/" + time + "/params.log");

        try {
            if (!file.createNewFile())
                return;
        } catch (IOException e1) {
            System.err.println("Error creating params file");
        }

        try (FileWriter writer = new FileWriter(file);) {
            writer.write("batchMode=" + batchMode + "\n");
            writer.write("nAudience=" + nAudience + "\n");
            writer.write("nCompetitors=" + nCompetitors + "\n");
            writer.write("nItems=" + nItems + "\n");
            writer.write("highConfidenceRate=" + highConfidenceRate + "\n");
        } catch (Exception e) {
            System.err.println("Error writing params file");
        }
    }
}
