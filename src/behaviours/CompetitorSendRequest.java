package behaviours;

import agents.Competitor;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.io.Serializable;

public class CompetitorSendRequest extends SendMsgBehaviour {
    public CompetitorSendRequest(Competitor c) {
        super(c);
    }

    @Override
    protected Serializable chooseContentObject(AID rcv) {
        Competitor p = (Competitor) person;
        person.logger.info(String.format("COMPETITOR %10s SENT REQUEST           TO %10s", person.getLocalName(), rcv.getLocalName()));
        return p.getTeam();
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getAudience();
    }
}