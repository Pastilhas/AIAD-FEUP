package behaviours;

import java.io.IOException;

import agents.MyAgent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import sajas.core.behaviours.SimpleBehaviour;

abstract class SendMsgBehaviour extends SimpleBehaviour {
    private static final long serialVersionUID = -723156319697606158L;

    protected final MyAgent agent;
    private boolean finished = false;

    SendMsgBehaviour(MyAgent a) {
        agent = a;
    }

    @Override
    public void action() {
        DFAgentDescription[] res = chooseReceivers();

        for (DFAgentDescription re : res) {
            try {
                AID rcv = re.getName();
                if (agent.getLocalName().equals(rcv.getLocalName()))
                    continue;
                ACLMessage msg = getMessage(rcv);
                agent.send(msg);
            } catch (IOException e) {
                System.err.println(
                        "Agent " + agent.getLocalName() + " failed to send message to " + re.getName().getLocalName());
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
