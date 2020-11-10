package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.HashMap;

public class AudienceReceiveRequest extends SimpleBehaviour {
    private boolean finished = false;
    private final Audience audience;

    public AudienceReceiveRequest(Audience audience) {
        this.audience = audience;
    }

    @Override
    public void action() {
        ACLMessage msg = audience.blockingReceive();

        if(msg != null) {
            if(msg.getSender().getLocalName().substring(0, 10).equals("competitor")) {
                try {
                    HashMap<String, Integer> map = (HashMap<String, Integer>) msg.getContentObject();
                    AID competitor = msg.getSender();

                    // check compatibility with competitor
                    audience.checkCompetitor(competitor, map);
                } catch (UnreadableException e) {
                    System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
                }
            }
        } else {
            block();
        }

        if(audience.getCompatibility().size() == audience.getCompetitorLength()) {
            finished = true;
        }
    }

    @Override
    public boolean done() { return finished; }
}
