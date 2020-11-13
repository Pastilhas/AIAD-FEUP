package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.io.Serializable;

public class AudienceSendGuess extends SendMsgBehaviour {
    public AudienceSendGuess(Audience a) {
        super(a);
    }

    @Override
    protected Serializable chooseContentObject(AID rcv) {
        Audience p = (Audience) person;
        return p.getGuess(rcv.getLocalName());
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getCompetitor();
    }
}