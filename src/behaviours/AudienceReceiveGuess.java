package behaviours;

import agents.Audience;
import jade.lang.acl.ACLMessage;

public class AudienceReceiveGuess extends ReceiveMsgBehaviour {
    public AudienceReceiveGuess(Audience a) {
        super(a);
    }

    @Override
    protected void parseCompetitorMsg(ACLMessage msg) {
    }

    @Override
    protected void parseAudienceMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        if (person.getGuesses().containsKey(sender)) return;

        Audience p = (Audience) person;
        String guess = msg.getContent();
        p.logger.info("Audience " + p.getLocalName() + " RECEIVED guess: " + guess + " FROM agent: " + sender);
        if (guess.equals("null")) p.receiveGuess(sender, null);
        else p.receiveGuess(sender, Integer.valueOf(guess));


    }

    @Override
    protected boolean finishCondition() {
        return person.getGuesses().size() == person.getAudience().length - 1;
    }

    @Override
    protected void finish() {
        person.finalGuess();
    }
}
