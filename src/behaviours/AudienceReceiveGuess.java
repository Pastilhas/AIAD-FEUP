package behaviours;

import agents.Audience;
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
                String guess = msg.getContent();
                String sender = msg.getSender().getLocalName();
                if (!audience.getGuesses().containsKey(sender)) {
                    System.out.println("Audience " + audience.getLocalName() + " RECEIVED guess: " + guess + " FROM agent: " + sender);
                    audience.receiveGuess(sender, Integer.parseInt(guess));
                }
            }
        } else {
            block();
        }

        if (audience.getGuesses().size() == audience.getAudience().length - 1) {
            audience.finalGuess();
            finished = true;
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}
