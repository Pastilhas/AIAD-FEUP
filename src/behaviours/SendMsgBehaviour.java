package behaviours;

import java.io.IOException;

import agents.Person;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import sajas.core.behaviours.SimpleBehaviour;

abstract class SendMsgBehaviour extends SimpleBehaviour {
    protected final Person person;
    private boolean finished = false;

    SendMsgBehaviour(Person p) {
        person = p;
    }

    @Override
    public void action() {
        DFAgentDescription[] res = chooseReceivers();

        for (DFAgentDescription re : res) {
            try {
                AID rcv = re.getName();
                if (person.getLocalName().equals(rcv.getLocalName()))
                    continue;
                ACLMessage msg = getMessage(rcv);
                person.send(msg);
            } catch (IOException e) {
                person.logger.warning(
                        "Agent " + person.getLocalName() + " failed to send message to " + re.getName().getLocalName());
            }
        }

        finished = true;
    }

    protected abstract ACLMessage getMessage(AID rcv) throws IOException;

    protected abstract DFAgentDescription[] chooseReceivers();

    @Override
    public boolean done() {
        return finished;
    }
}
