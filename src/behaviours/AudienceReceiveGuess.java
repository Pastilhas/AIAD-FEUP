package behaviours;

import agents.Audience;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

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

        try {
            Integer guess = (Integer) msg.getContentObject();
            person.logger.info(String.format("AUDIENCE   %10s RECEIVED GUESS %7d FROM %10s", person.getLocalName(), guess, sender));
            person.receiveGuess(sender, guess);
        } catch (UnreadableException e) {
            person.logger.severe("Exception thrown while " + person.getLocalName() + " was receiving guess from " + sender + ".");
            e.printStackTrace();
            System.exit(4);
        }
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