package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class AudienceReceiveGuess extends SimpleBehaviour {
    private boolean finished = false;
    private final Audience audience;

    public AudienceReceiveGuess(Audience audience) {
        this.audience = audience;
    }

    @Override
    public void action() {
        ACLMessage msg = audience.blockingReceive();

        if (msg != null) {
            if (msg.getSender().getLocalName().startsWith("audience")) {
                try {
                    String guess = (String) msg.getContentObject();
                    AID sender = msg.getSender();
                    audience.receiveGuess(sender, Integer.parseInt(guess));
                } catch (UnreadableException e) {
                    System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
                }
            }
        } else {
            block();
        }

        if (audience.getGuesses().size() == audience.getAudience().length) {
            audience.finalGuess();
            finished = true;
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}
