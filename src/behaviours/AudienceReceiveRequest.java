package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
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

        if (msg != null) {
            if (msg.getSender().getLocalName().startsWith("competitor")) {
                try {
                    HashMap<String, Integer> map = (HashMap<String, Integer>) msg.getContentObject();
                    String sender = msg.getSender().getLocalName();
                    System.out.println("Audience " + audience.getLocalName() + " RECEIVED request FROM agent: " + sender);
                    audience.checkCompetitor(sender, map);
                } catch (UnreadableException e) {
                    System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
                }
            }
        } else {
            block();
        }

        if (audience.getCompatibility().size() == audience.getCompetitor().length) {
            finished = true;
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}
