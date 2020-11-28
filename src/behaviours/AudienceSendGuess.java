package behaviours;

import java.io.IOException;

import agents.Audience;
import agents.Person.Phase;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class AudienceSendGuess extends SendMsgBehaviour {
    public AudienceSendGuess(Audience a) {
        super(a);
    }

    @Override
    public void action() {
        if (agent.phase != Phase.SEND)
            return;
        super.action();
        agent.phase = Phase.READY;
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Audience p = (Audience) agent;
        int performative;
        if (p.getGuess(rcv.getLocalName()) == null)
            performative = ACLMessage.REFUSE;
        else
            performative = ACLMessage.AGREE;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getGuess(rcv.getLocalName()));
        msg.addReceiver(rcv);
        agent.logger.info(String.format("AUDIENCE   %10s SENT GUESS     %7d TO   %10s", agent.getLocalName(),
                p.getGuess(rcv.getLocalName()), rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return agent.getCompetitor();
    }
}