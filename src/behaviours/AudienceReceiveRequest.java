package behaviours;

import agents.Audience;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import util.LogUtil;

import java.time.LocalTime;
import java.util.HashMap;

public class AudienceReceiveRequest extends ReceiveMsgBehaviour {
    public AudienceReceiveRequest(Audience a) {
        super(a);
    }

    @Override
    protected void parseCompetitorMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        Audience p = (Audience) person;
        if (p.getCompatibility().containsKey(sender)) return;

        try {
            HashMap<String, Integer> map = (HashMap<String, Integer>) msg.getContentObject();
            p.logger.info("Audience " + p.getLocalName() + " RECEIVED request FROM agent: " + sender);
            p.checkCompetitor(sender, map);
        } catch (UnreadableException e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void parseAudienceMsg(ACLMessage msg) {
    }

    @Override
    protected boolean finishCondition() {
        Audience p = (Audience) person;
        return p.getCompatibility().size() == p.getCompetitor().length;
    }

    @Override
    protected void finish() {
    }
}