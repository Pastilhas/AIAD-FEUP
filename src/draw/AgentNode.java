package draw;

import java.awt.Color;
import java.util.Random;

import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;

public class AgentNode extends DefaultDrawableNode {
    private Color outColor;

    public static AgentNode getNewNode(String label, Color color, int x, int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x, y);
        oval.allowResizing(false);
        oval.setHeight(50);
        oval.setWidth(50);

        AgentNode a = new AgentNode(label, oval);
        a.setColor(color);
        return a;
    }

    public AgentNode(String label, OvalNetworkItem oval) {
        super(label, oval);
        Random rnd = new Random();
        outColor = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
    }

    public void addEdge(AgentEdge to) {
        to.setColor(outColor);
        addOutEdge(to);
    }

    public void addEdge(AgentNode to) {
        AgentEdge e = new AgentEdge(this, to);
        e.setColor(outColor);
        addOutEdge(e);
    }

    public void removeEdges() {
        this.outEdges.clear();
    }
}
