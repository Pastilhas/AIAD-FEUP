package agents;

import java.awt.Color;

import behaviours.CompetitorSendGuess;
import behaviours.CompetitorSendRequest;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import world.World;

public class Competitor extends Person {

    public Competitor(String id, long time, World world, Color color, int x, int y) {
        super(id, time, world, color, x, y);
    }

    @Override
    protected void setup() {
        setupAgent("competitor");
    }

    @Override
    public void finalGuess() {
        float[] a = finalGuessCalc();
        guess = Math.round(a[0] / a[1]);
    }

    @Override
    public void parseAudienceMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        try {
            Integer content = (Integer) msg.getContentObject();
            logger.info(String.format("RECEIVED GUESS %7d FROM %10s", content, sender));
            receiveGuess(sender, content);
        } catch (UnreadableException e) {
            System.err.println("Exception thrown while " + getLocalName() + " was receiving guess from " + sender);
            e.printStackTrace();
        }
        if (getAudience().length <= guesses.size()) {
            finalGuess();
            phase = Phase.READY;
            addBehaviour(new CompetitorSendGuess(this));
        }
    }

    @Override
    public void parseCompetitorMsg(ACLMessage msg) {
        System.err.println("Unexpected message from " + msg.getSender().getLocalName() + " to " + getLocalName());
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
        removeEdges();
        phase = Phase.INIT;
    }
}