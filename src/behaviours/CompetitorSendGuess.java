package behaviours;

import java.io.IOException;

import agents.Competitor;
import agents.MyAgent.Phase;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class CompetitorSendGuess extends SendMsgBehaviour {
    private static final long serialVersionUID = 6001800050555085876L;

    public CompetitorSendGuess(Competitor c) {
        super(c);
    }

    @Override
    public void action() {
        if (agent.phase != Phase.READY)
            return;
        super.action();
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Competitor p = (Competitor) agent;
        int performative = ACLMessage.INFORM;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getGuess());
        msg.addReceiver(rcv);
        agent.logger.info(String.format("SENT GUESS %11d TO %12s", p.getGuess(), rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return agent.getWorld();
    }
}
