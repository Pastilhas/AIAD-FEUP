package behaviours;

import agents.Person;
import jade.lang.acl.ACLMessage;
import sajas.core.behaviours.CyclicBehaviour;

public class ReceiveMsgBehaviour extends CyclicBehaviour {
    protected final Person person;

    public ReceiveMsgBehaviour(Person p) {
        person = p;
    }

    @Override
    public void action() {
        ACLMessage msg = person.receive();
        if (msg != null) {
            if (msg.getSender().getLocalName().startsWith("audience")) {
                person.parseAudienceMsg(msg);
            } else if (msg.getSender().getLocalName().startsWith("competitor")) {
                person.parseCompetitorMsg(msg);
            }
        } else {
            block();
        }
    }
}
