package agents;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import behaviours.AudienceSendGuess;
import behaviours.AudienceShareGuess;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import world.WorldModel;

public class Audience extends Person {
    private final HashMap<String, Integer> itemPrice;
    private final HashMap<String, Boolean> compatibility;
    private final float selfconfidence;

    public Audience(String id, long time, WorldModel world, float selfconfidence, Color color, int x, int y) {
        super(id, time, world, color, x, y);
        this.selfconfidence = selfconfidence;
        itemPrice = new HashMap<>();
        compatibility = new HashMap<>();
    }

    @Override
    protected void setup() {
        setupAgent("audience");
    }

    @Override
    public void finalGuess() {
        float[] a = finalGuessCalc();
        float currentGuess = a[0];
        float maxConfidence = a[1];

        currentGuess += guess * selfconfidence;
        maxConfidence += selfconfidence;

        guess = Math.round(currentGuess / maxConfidence);
    }

    public Integer getGuess(String id) {
        if (id == null || compatibility.get(id))
            return getGuess();
        else
            return null;
    }

    void initialGuess(String item) {
        Random rnd = new Random();
        if (itemPrice.get(item) != null) {
            int p = itemPrice.get(item);
            guess = p + rnd.nextInt((int) (p * 0.2f)) - (int) (p * 0.1f);
        } else {
            guess = rnd.nextInt(world.WorldModel.MAX_PRICE);
        }
        phase = Phase.SHARE;
    }

    public void addItem(String id, Integer value) {
        if (value != null)
            itemPrice.put(id, value);
    }

    public HashMap<String, Boolean> getCompatibility() {
        return compatibility;
    }

    public void checkCompetitor(String id, HashMap<String, Integer> map) {
        int times = 0;
        for (Map.Entry<String, Integer> entry : teamAffinity.entrySet()) {
            if (Math.abs(entry.getValue() - map.get(entry.getKey())) > 20) {
                times++;
            }
            if (times >= 2) {
                compatibility.put(id, false);
                return;
            }
        }
        compatibility.put(id, true);
    }

    @Override
    public void parseAudienceMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        try {
            Integer content = (Integer) msg.getContentObject();
            logger.info(String.format("RECEIVED GUESS %7s FROM %10s", content, sender));
            receiveGuess(sender, content);
        } catch (UnreadableException e) {
            System.err.println("Exception thrown while " + getLocalName() + " was receiving guess from " + sender);
            e.printStackTrace();
        }

        if (getAudience().length - 1 <= guesses.size()) {
            finalGuess();
            if (getCompetitor().length <= compatibility.size()) {
                phase = Phase.SEND;
                addBehaviour(new AudienceSendGuess(this));
            } else
                phase = Phase.WAIT2;
        }
    }

    @Override
    public void parseCompetitorMsg(ACLMessage msg) {
        String sender = msg.getSender().getLocalName();
        try {
            HashMap<String, Integer> map = (HashMap<String, Integer>) msg.getContentObject();
            logger.info(String.format("RECEIVED REQUEST FROM %10s", sender));
            checkCompetitor(sender, map);
        } catch (UnreadableException e) {
            System.err.println("Exception thrown while " + getLocalName() + " was receiving request from " + sender + ".");
            e.printStackTrace();
        }
        if (phase == Phase.WAIT2 && getCompetitor().length <= compatibility.size()) {
            phase = Phase.SEND;
            addBehaviour(new AudienceSendGuess(this));
        }
    }

    @Override
    protected void startRound(String item_id) {
        initialGuess(item_id);
        addBehaviour(new AudienceShareGuess(this));
    }

    @Override
    protected void endRound(Integer item_price) {
        updateConfidence(item_price);
        guess = null;
        guesses.clear();
        compatibility.clear();
        removeEdges();
        phase = Phase.INIT;
    }
}