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
        if (person.phase != Phase.INIT)
            return;
        super.action();
        person.phase = Phase.WAIT;
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Competitor p = (Competitor) person;
        int performative = ACLMessage.REQUEST;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getTeam());
        msg.addReceiver(rcv);
        person.logger.info(String.format("COMPETITOR %10s SENT REQUEST           TO   %10s", person.getLocalName(),
                rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getAudience();
    }
}