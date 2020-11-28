package agents;

import java.util.HashMap;

import agents.Person.Phase;
import behaviours.ReceiveMsgBehaviour;
import behaviours.WorldSendEnd;
import behaviours.WorldSendStart;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import sajas.domain.DFService;
import world.WorldModel;

public class WorldAgent extends MyAgent {
    private final WorldModel world;
    private String item_id;
    private Integer item_price;
    private final HashMap<String, Integer> comps;

    public WorldAgent(String id, long time, WorldModel world) {
        super(id, time);
        this.world = world;
        comps = new HashMap<>();
    }

    @Override
    protected void setup() {
        addBehaviour(new ReceiveMsgBehaviour(this));
        startRound();
    }

    private void startRound() {
        comps.clear();
        String[] a = world.startRound();
        item_id = a[0];
        item_price = Integer.valueOf(a[1]);
        addBehaviour(new WorldSendStart(this));
    }

    private void endRound() {
        world.endRound();
        addBehaviour(new WorldSendEnd(this));
    }

	public String getItem() {
		return item_id;
	}

	public Integer getPrice() {
		return item_price;
	}

    @Override
    public void parseAudienceMsg(ACLMessage msg) {
        logger.warning("Unexpected message from " + msg.getSender().getLocalName() + " to " + getLocalName());
    }

    @Override
    public void parseCompetitorMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        Integer content = Integer.valueOf(msg.getContent());
        comps.put(sender, content);

        if (getCompetitor().length <= comps.size()) {
            phase = Phase.SEND;
            endRound();
        }
    }

    @Override
    public void parseWorldMsg(ACLMessage msg) {
        logger.warning("Unexpected message from " + msg.getSender().getLocalName() + " to " + getLocalName());
    }
}
