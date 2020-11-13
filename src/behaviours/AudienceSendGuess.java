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
        person.logger.info(String.format("AUDIENCE   %10s SENT GUESS     %7d TO %10s", person.getLocalName(), p.getGuess(rcv.getLocalName()), rcv.getLocalName()));
        return p.getGuess(rcv.getLocalName());
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getCompetitor();
    }
}