package behaviours;

import java.io.IOException;

import agents.Competitor;
import agents.Person.Phase;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class CompetitorSendRequest extends SendMsgBehaviour {
    public CompetitorSendRequest(Competitor c) {
        super(c);
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
        Competitor p = (Competitor) agent;
        int performative = ACLMessage.REQUEST;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getTeam());
        msg.addReceiver(rcv);
        agent.logger.info(String.format("COMPETITOR %10s SENT REQUEST           TO   %10s", agent.getLocalName(),
                rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return agent.getAudience();
    }
}