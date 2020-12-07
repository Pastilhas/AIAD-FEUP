package draw;

import java.awt.Color;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableEdge;

public class AgentEdge extends DefaultDrawableEdge {
    private Color color = Color.WHITE;
    private String string = "";
    
    public AgentEdge(AgentNode from, AgentNode to) {
        super(from, to);
    }

    @Override
    public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY) {
        g.drawDirectedLink(color, fromX, toX, fromY, toY);
        g.getGraphics().drawString(string, (toX-fromX)/2, (toY-fromY)/2);
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    public void setString(String string) {
        this.string = string;
    }
}
