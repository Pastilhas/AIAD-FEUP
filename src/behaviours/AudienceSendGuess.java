package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import util.LogUtil;

import java.time.LocalTime;

public class AudienceSendGuess extends SimpleBehaviour {
    private boolean finished = false;
    private final Audience audience;

    public AudienceSendGuess(Audience audience) {
        this.audience = audience;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        DFAgentDescription[] res = audience.getCompetitor();
        for (DFAgentDescription re : res) {
            AID rcv = re.getName();
            if (audience.getLocalName().equals(rcv.getLocalName())) continue;
            Integer guess = audience.getGuess(rcv.getLocalName());
            if(guess == null) continue;
            msg.setContent(Integer.toString(guess));
            msg.addReceiver(rcv);
            String logMsg = "Audience " + audience.getLocalName() + " SENT guess: " + guess + " TO agent: " + rcv.getLocalName();
            System.out.println(logMsg);
            LogUtil.writeToLog(logMsg, LocalTime.now());
            audience.send(msg);
        }

        finished = true;
    }

    @Override
    public boolean done() {
        return finished;
    }
}
