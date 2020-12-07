package draw;

import java.awt.Color;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;

public class AgentNode extends DefaultDrawableNode {
    public static AgentNode getNewNode(String label, Color color, int x, int y) {
        OvalNetworkItem oval = new OvalNetworkItem(x,y);
        oval.allowResizing(false);
        oval.setHeight(50);
        oval.setWidth(50);

        AgentNode a = new AgentNode(label, oval);
        a.setColor(color);
        return a;
    }

    public AgentNode(String label, OvalNetworkItem oval) {
        super(label, oval);
    }

    public void addEdge(AgentNode to) {
        addOutEdge(new AgentEdge(this, to));
    }

    public void removeEdges() {
        removeEdgesFrom(this);
    }
}
