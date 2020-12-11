package agents;

import java.util.HashMap;

import behaviours.WorldSendEnd;
import behaviours.WorldSendStart;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import world.World;

public class WorldAgent extends MyAgent {
    private final World world;
    private final HashMap<String, Integer> comps = new HashMap<>();

    private String item_id;
    private Integer item_price;

    public WorldAgent(String id, long time, World world) {
        super(id, time);
        this.world = world;
    }

    @Override
    protected void setup() {
        setupAgent("world");
        startRound();
    }

    public void startRound() {
        comps.clear();
        String[] a = world.startRound();
        item_id = a[0];
        item_price = Integer.valueOf(a[1]);
        addBehaviour(new WorldSendStart(this));
    }

    private void endRound() {
        addBehaviour(new WorldSendEnd(this));
        world.endRound();
    }

    public String getItem() {
        return item_id;
    }

    public Integer getPrice() {
        return item_price;
    }

    public boolean hasEnded() {
        return phase == Phase.INIT && world.isEnd();
    }

    @Override
    public void parseAudienceMsg(ACLMessage msg) {
        System.err.println("Unexpected message from " + msg.getSender().getLocalName() + " to " + getLocalName());
    }

    @Override
    public void parseCompetitorMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        try {
            Integer content = (Integer) msg.getContentObject();
            logger.info(String.format("RECEIVED GUESS %7d FROM %10s", content, sender));
            comps.put(sender, content);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        if (getCompetitor().length <= comps.size()) {
            phase = Phase.SEND;
            endRound();
        }
    }

    @Override
    public void parseWorldMsg(ACLMessage msg) {
        System.err.println("Unexpected message from " + msg.getSender().getLocalName() + " to " + getLocalName());
    }
}
