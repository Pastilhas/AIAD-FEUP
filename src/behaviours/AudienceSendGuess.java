package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

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
            if(audience.getLocalName().equals(rcv.getLocalName())) continue;

            try{
                int guess = audience.getGuess(rcv.getLocalName());
                msg.setContent(Integer.toString(guess));
                msg.addReceiver(rcv);
                System.out.println("Audience " + audience.getLocalName() + " SENT guess: " + guess + " TO agent: " + rcv.getLocalName());
                audience.send(msg);
            } catch (Exception e) {
                System.out.println("Audience " + audience.getLocalName() + " did NOT send a guess" );
            }

//            try{
//                int guess = audience.getGuess(rcv.getLocalName());
//                msg.setContent(Integer.toString(guess));
//                msg.addReceiver(rcv);
//                System.out.println("Audience " + audience.getLocalName() + " SENT guess: " + guess + " TO agent: " + rcv.getLocalName());
//                audience.send(msg);
//            } catch (Exception e) {
//                System.out.println("Audience " + audience.getLocalName() + " did NOT send a guess" );
//            }
        }

        finished = true;
    }

    @Override
    public boolean done() {
        return finished;
    }
}
