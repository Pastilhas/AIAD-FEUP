package behaviours;

import agents.Audience;
import agents.Competitor;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

public class CompetitorSendRequest extends SendMsgBehaviour {
    public CompetitorSendRequest(Competitor c) {
        super(c);
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Competitor p = (Competitor) person;
        int performative = ACLMessage.REQUEST;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getTeam());
        msg.addReceiver(rcv);
        person.logger.info(String.format("COMPETITOR %10s SENT REQUEST           TO   %10s", person.getLocalName(), rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getAudience();
    }
}