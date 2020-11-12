package behaviours;

import agents.Person;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public abstract class ReceiveMsgBehaviour extends SimpleBehaviour {
    protected final Person person;
    private boolean finished = false;

    public ReceiveMsgBehaviour(Person p) {
        person = p;
    }

    @Override
    public void action() {
        ACLMessage msg = person.blockingReceive();
        if(msg != null) {
            if(msg.getSender().getLocalName().startsWith("audience")){
                parseAudienceMsg(msg);
            } else if(msg.getSender().getLocalName().startsWith("competitor")) {
                parseCompetitorMsg(msg);
            }
        } else {
            block();
        }

        if (finishCondition()) {
            finish();
            finished = true;
        }
    }

    protected abstract void parseCompetitorMsg(ACLMessage msg);
    protected abstract void parseAudienceMsg(ACLMessage msg);
    protected abstract boolean finishCondition();
    protected abstract void finish();

    @Override
    public boolean done() {
        return finished;
    }
}
