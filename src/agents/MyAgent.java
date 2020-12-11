package agents;

import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import behaviours.ReceiveMsgBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import sajas.core.Agent;
import sajas.domain.DFService;

public abstract class MyAgent extends Agent {
    public enum Phase {
        INIT, WAIT, READY, SHARE, WAIT2, SEND
    };

    public final Logger logger;
    protected final DFAgentDescription dfd;
    protected final String id;
    public Phase phase;

    MyAgent(String id, long time) {
        logger = Logger.getLogger(id);
        dfd = new DFAgentDescription();
        this.id = id;
        setupLogger(time);
        phase = Phase.INIT;
    }

    public String getId() {
        return id;
    }

    private void setupLogger(long time) {
        try {
            FileHandler handler = new FileHandler("logs/" + time + "/" + id + ".log");
            handler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s%n";
                private final Date dat = new Date();

                @Override
                public String format(LogRecord record) {
                    dat.setTime(record.getMillis());
                    String message = formatMessage(record);
                    return String.format(format, dat, record.getLevel().getLocalizedName(), message);
                }
            });
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Exception thrown while setting up " + id + " logger.");
            e.printStackTrace();
            System.exit(3);
        }
    }

    public DFAgentDescription[] getCompetitor() {
        return getService("competitor");
    }

    public DFAgentDescription[] getAudience() {
        return getService("audience");
    }

    public DFAgentDescription[] getWorld() {
        return getService("world");
    }

    private DFAgentDescription[] getService(String type) {
        DFAgentDescription[] res = null;
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);
            res = DFService.search(this, dfd);
        } catch (FIPAException e) {
            System.err.println("Exception thrown while getting service " + type);
            e.printStackTrace();
            System.exit(3);
        }
        return res;
    }

    protected void setupAgent(String type) {
        addBehaviour(new ReceiveMsgBehaviour(this));
        try {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            sd.setName(getLocalName());
            dfd.setName(getAID());
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.err.println("Exception thrown while setting up " + id);
            e.printStackTrace();
            System.exit(3);
        }
    }

    public abstract void parseAudienceMsg(ACLMessage msg);

    public abstract void parseCompetitorMsg(ACLMessage msg);

    public abstract void parseWorldMsg(ACLMessage msg);
}
