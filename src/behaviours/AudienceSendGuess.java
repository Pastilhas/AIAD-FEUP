package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class AudienceSendGuess extends SendMsgBehaviour {
    public AudienceSendGuess(Audience a) {
        super(a);
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Audience p = (Audience) person;
        int performative;
        if (p.getGuess(rcv.getLocalName()) == null) performative = ACLMessage.REFUSE;
        else performative = ACLMessage.AGREE;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getGuess(rcv.getLocalName()));
        msg.addReceiver(rcv);
        person.logger.info(String.format("AUDIENCE   %10s SENT GUESS     %7d TO   %10s", person.getLocalName(), p.getGuess(rcv.getLocalName()), rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return person.getCompetitor();
    }
}