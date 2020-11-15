package agents;

import behaviours.AudienceReceiveGuess;
import behaviours.AudienceReceiveRequest;
import behaviours.AudienceSendGuess;
import behaviours.AudienceShareGuess;
import jade.core.behaviours.SequentialBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Audience extends Person {
    private final HashMap<String, Integer> itemPrice;
    private final HashMap<String, Integer> compatibility;
    private final float selfconfidence;

    public Audience(String id, float selfconfidence, long time) {
        super(id, time);
        this.selfconfidence = selfconfidence;
        itemPrice = new HashMap<>();
        compatibility = new HashMap<>();
    }

    @Override
    protected void setup() {
        setupAgent("audience");
    }

    @Override
    void behaviours() {
        SequentialBehaviour sb = new SequentialBehaviour();
        sb.addSubBehaviour(new AudienceShareGuess(this));
        sb.addSubBehaviour(new AudienceReceiveGuess(this));
        sb.addSubBehaviour(new AudienceReceiveRequest(this));
        sb.addSubBehaviour(new AudienceSendGuess(this));
        addBehaviour(sb);
    }

    @Override
    public void finalGuess() {
        float[] a = finalGuessCalc();
        float currentGuess = a[0];
        float maxConfidence = a[1];

        currentGuess += guess * selfconfidence;
        maxConfidence += selfconfidence;

        guess = Math.round(currentGuess / maxConfidence);
        ready = true;
    }

    public Integer getGuess(String id) {
        if (id == null || compatibility.get(id) > 80) return getGuess();
        else return null;
    }

    public void startRound(String id) {
        initialGuess(id);
        super.startRound();
    }

    @Override
	public
    void endRound(int price) {
        super.endRound(price);
        compatibility.clear();
    }

    void initialGuess(String item) {
        Random rnd = new Random();
        if (itemPrice.get(item) != null) {
            int p = itemPrice.get(item);
            guess = p + rnd.nextInt((int) (p * 0.2f)) - (int) (p * 0.1f);
        } else {
            guess = rnd.nextInt(15001);
        }
    }

    public void addItem(String id, Integer value) {
        if (value != null) itemPrice.put(id, value);
    }

    public HashMap<String, Integer> getCompatibility() {
        return compatibility;
    }

    public void checkCompetitor(String id, HashMap<String, Integer> map) {
        int comp = 0;
        for (Map.Entry<String, Integer> entry : teamAffinity.entrySet()) {
            comp += Math.abs(entry.getValue() - map.get(entry.getKey()));
        }
        compatibility.put(id, comp);
    }
}