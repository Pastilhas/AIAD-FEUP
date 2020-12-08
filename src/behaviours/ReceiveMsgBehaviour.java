package behaviours;

import agents.MyAgent;
import jade.lang.acl.ACLMessage;
import sajas.core.behaviours.CyclicBehaviour;

public class ReceiveMsgBehaviour extends CyclicBehaviour {
    private static final long serialVersionUID = -7489744352564982942L;

    protected final MyAgent agent;

    public ReceiveMsgBehaviour(MyAgent a) {
        agent = a;
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive();
        if (msg != null) {
            String sender = msg.getSender().getLocalName();
            if (sender.startsWith("audience")) agent.parseAudienceMsg(msg);
            else if (sender.startsWith("competitor")) agent.parseCompetitorMsg(msg);
            else if (sender.startsWith("world")) agent.parseWorldMsg(msg);
        } else {
            block();
        }
    }
}
