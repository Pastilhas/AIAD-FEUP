package behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import agents.MyAgent;
import agents.MyAgent.Phase;
import agents.WorldAgent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class WorldSendStart extends SendMsgBehaviour {
    private static final long serialVersionUID = -232672649672997121L;

    public WorldSendStart(MyAgent a) {
        super(a);
    }

    @Override
    public void action() {
        if (!((WorldAgent) agent).hasEnded()) return;
        super.action();
        agent.phase = Phase.WAIT;
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        WorldAgent p = (WorldAgent) agent;
        int performative = ACLMessage.INFORM;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContent("start " + p.getItem());
        msg.addReceiver(rcv);
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        ArrayList<DFAgentDescription> aud = new ArrayList<DFAgentDescription>(Arrays.asList(agent.getAudience()));
        ArrayList<DFAgentDescription> cmp = new ArrayList<DFAgentDescription>(Arrays.asList(agent.getCompetitor()));
        aud.addAll(cmp);
        DFAgentDescription[] arr = new DFAgentDescription[aud.size()];
        return aud.toArray(arr);
    }
}
