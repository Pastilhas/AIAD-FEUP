package behaviours;

import agents.Competitor;
import jade.lang.acl.ACLMessage;

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

        Competitor p = (Competitor) person;
        String guess = msg.getContent();
        p.logger.info("Competitor " + p.getLocalName() + " RECEIVED guess: " + guess + " FROM agent: " + sender);
        if(guess.equals("null")) p.receiveGuess(sender, null);
        else p.receiveGuess(sender, Integer.valueOf(guess));
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