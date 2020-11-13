package behaviours;

import agents.Person;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

public abstract class SendMsgBehaviour extends SimpleBehaviour {
    protected final Person person;
    private boolean finished = false;

    public SendMsgBehaviour(Person p) {
        person = p;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        DFAgentDescription[] res = chooseReceivers();

        for (DFAgentDescription re : res) {
            try {
                AID rcv = re.getName();
                if (person.getLocalName().equals(rcv.getLocalName())) continue;
                msg.setContentObject(chooseContentObject(rcv));
                msg.addReceiver(rcv);
                person.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        finished = true;
    }

    protected abstract Serializable chooseContentObject(AID rcv);

    protected abstract DFAgentDescription[] chooseReceivers();

    @Override
    public boolean done() {
        return finished;
    }
}
