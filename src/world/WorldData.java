package world;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import agents.Audience;
import agents.Competitor;

public class WorldData {
    private Integer avg = 0;
    private Integer diff = 0;
    private final HashMap<String, Integer> winners = new HashMap<>();
    private final HashMap<String, Integer> helpers = new HashMap<>();
    private final HashMap<Integer, Integer[]> guesses = new HashMap<>();
    private World world;

    public WorldData(World world) {
        this.world = world;
    }

    public void newRound() {
        putGuesses();
        putWinner();
    }

    private void putGuesses() {
        Integer min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int price = world.getPrice();
        for (Competitor c : world.getCompetitors()) {
            Integer g = c.getGuess();
            if (g == null) continue;
            g = Math.abs(g - price);
            if (g < min) min = g;
            if (g > max) max = g;
        }
        avg = (max + min) / 2;
        diff = max - min;
        guesses.put(world.getRound(), new Integer[] { min, max, avg, diff });
    }

    private void putWinner() {
        Competitor winner = null;
        Integer min = Integer.MAX_VALUE;
        int price = world.getPrice();
        for (Competitor c : world.getCompetitors()) {
            Integer g = c.getGuess();
            if (g == null) continue;
            g = g - price;
            if (g > min) continue;
            min = g;
            winner = c;
        }
        winners.merge(winner.getLocalName(), 1, Integer::sum);
        int helper = 0;
        for(Audience a : world.getAudience()) { 
            a.checkCompetitor(winner.getLocalName(), winner.getTeam());
            if(a.getCompatibility().get(winner.getLocalName())) helper++;
        }
        helpers.putIfAbsent(winner.getLocalName(), helper);
    }

    public void writeData(long time) {
        File guessesF = new File("logs/" + time + "/guesses.csv");
        File winnersF = new File("logs/" + time + "/winners.csv");
        boolean done = false;
        try {
            done = guessesF.createNewFile();
            done = done && winnersF.createNewFile();
            if (!done) { throw new IOException(); }
        } catch (IOException e1) {
            System.err.println("Error creating data files");
            return;
        }
        try (FileWriter guessesW = new FileWriter(guessesF); FileWriter winnersW = new FileWriter(winnersF);) {
            guessesW.write("round,min,max,avg,diff\n");
            for (Entry<Integer, Integer[]> e : guesses.entrySet()) {
                Integer key = e.getKey(), v1 = e.getValue()[0], v2 = e.getValue()[1], v3 = e.getValue()[2], v4 = e.getValue()[3];
                guessesW.write(String.format("%d,%d,%d,%d,%d%n", key, v1, v2, v3, v4));
            }
            winnersW.write("id,wins,help\n");
            for (Entry<String, Integer> e : winners.entrySet()) {
                String key = e.getKey();
                Integer wins = e.getValue();
                Integer help = helpers.get(key);
                winnersW.write(String.format("%s,%d,%d%n", key, wins, help));
            }
        } catch (Exception e) {
            System.err.println("Error writing to data files");
        }
    }

    Integer getLastAvg() {
        return avg;
    }

	public double getLastDiff() {
		return diff;
	}
}
