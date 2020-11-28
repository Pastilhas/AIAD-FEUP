package behaviours;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import agents.MyAgent;
import agents.WorldAgent;
import agents.Person.Phase;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class WorldSendEnd extends SendMsgBehaviour {
    public WorldSendEnd(MyAgent a) {
        super(a);
    }

    @Override
    public void action() {
        if (agent.phase != Phase.INIT)
            return;
        super.action();
        agent.phase = Phase.WAIT;
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        WorldAgent p = (WorldAgent) agent;
        int performative = ACLMessage.INFORM;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContent("end " + p.getPrice());
        msg.addReceiver(rcv);
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        List<DFAgentDescription> aud = Arrays.asList(agent.getAudience());
        List<DFAgentDescription> cmp = Arrays.asList(agent.getCompetitor());
        aud.addAll(cmp);
        return (DFAgentDescription[]) aud.toArray();
    }

}
