package behaviours;

import agents.Competitor;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class CompetitorReceiveGuess extends ReceiveMsgBehaviour {
    public CompetitorReceiveGuess(Competitor c) {
        super(c);
    }

    @Override
    protected void parseCompetitorMsg(ACLMessage msg) {}

    @Override
    protected void parseAudienceMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        if(person.getGuesses().containsKey(sender)) return;

        try {
            Integer guess = (Integer) msg.getContentObject();
            person.logger.info("Competitor " + person.getLocalName() + " RECEIVED guess: " + guess + " FROM agent: " + sender);
            person.receiveGuess(sender, guess);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean finishCondition() {
        return person.getGuesses().size() == person.getAudience().length;
    }

    @Override
    protected void finish() {
        person.finalGuess();
    }
}