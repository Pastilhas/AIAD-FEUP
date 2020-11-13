package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.io.Serializable;

public class AudienceShareGuess extends SendMsgBehaviour {
    public AudienceShareGuess(Audience a) {
        super(a);
    }

    @Override
    protected Serializable chooseContentObject(AID rcv) {
        Audience p = (Audience) person;
        person.logger.info(String.format("AUDIENCE   %10s SENT GUESS     %7d TO %10s", person.getLocalName(), p.getGuess(null), rcv.getLocalName()));
        return p.getGuess(null);
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getAudience();
    }
}