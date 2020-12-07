package draw;

import java.awt.Color;
import java.util.Random;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableEdge;

public class AgentEdge extends DefaultDrawableEdge {
    private Color color;
    private String string = "";
    private int x, y;
    
    public AgentEdge(AgentNode from, AgentNode to) {
        super(from, to);
        Random rnd = new Random();
        x = (int) ((to.getX() + from.getX()) / 2 + rnd.nextInt(20) - 10);
        y = (int) ((to.getY() + from.getY()) / 2 + rnd.nextInt(20) - 10);
        color = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
    }

    @Override
    public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY) {
        g.drawDirectedLink(color, fromX, toX, fromY, toY);
        g.getGraphics().drawString(string, x, y);
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    public void setString(String string) {
        this.string = string;
    }
}
