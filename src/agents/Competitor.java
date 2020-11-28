package agents;

import behaviours.CompetitorSendRequest;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class Competitor extends Person {

    public Competitor(String id, long time) {
        super(id, time);
    }

    @Override
    protected void setup() {
        setupAgent("competitor");
    }

    @Override
    public void finalGuess() {
        float[] a = finalGuessCalc();
        float currentGuess = a[0];
        float maxConfidence = a[1];

        guess = Math.round(currentGuess / maxConfidence);
    }

    @Override
    public void parseAudienceMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        try {
            Integer content = (Integer) msg.getContentObject();
            receiveGuess(sender, content);

        } catch (UnreadableException e) {
            logger.severe("Exception thrown while " + getLocalName() + " was receiving guess from " + sender);
            e.printStackTrace();
        }

        if (getAudience().length <= guesses.size()) {
            finalGuess();
            phase = Phase.READY;
        }
    }

    @Override
    public void parseCompetitorMsg(ACLMessage msg) {
        logger.warning("Unexpected message from " + msg.getSender().getLocalName() + " to " + getLocalName());
    }

    @Override
    protected void startRound(String item_id) {
        addBehaviour(new CompetitorSendRequest(this));
    }

    @Override
    protected void endRound(Integer item_price) {
        updateConfidence(item_price);
        guess = null;
        guesses.clear();
        phase = Phase.INIT;
    }
}