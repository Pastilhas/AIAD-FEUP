package behaviours;

import agents.Competitor;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class CompetitorReceiveGuess extends SimpleBehaviour {
    private boolean finished = false;
    private final Competitor competitor;

    public CompetitorReceiveGuess(Competitor competitor) {
        this.competitor = competitor;
    }

    @Override
    public void action() {
        ACLMessage msg = competitor.blockingReceive();

        if (msg != null) {
            if (msg.getSender().getLocalName().startsWith("audience")) {
                String guess = msg.getContent();
                String sender = msg.getSender().getLocalName();
                if (!competitor.getGuesses().containsKey(sender))
                    System.out.println("Competitor " + competitor.getLocalName() + " RECEIVED guess: " + guess + " FROM agent: " + sender);
                competitor.receiveGuess(sender, Integer.parseInt(guess));
            }
        } else {
            block();
        }

        if (competitor.getGuesses().size() == competitor.getAudience().length) {
            competitor.finalGuess();
            finished = true;
        }
    }

    @Override
    public boolean done() {
        return finished;
    }
}