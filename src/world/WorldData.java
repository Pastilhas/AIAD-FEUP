package world;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import agents.Competitor;

public class WorldData {
    private Integer lastAvg;
    private final HashMap<String, Integer> winners;
    private final HashMap<Integer, Integer[]> guesses;
    private World world;

    public WorldData(World world) {
        this.world = world;
        winners = new HashMap<>();
        guesses = new HashMap<>();
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
            if (g == null)
                continue;
            g = Math.abs(g - price);
            if (g < min)
                min = g;
            if (g > max)
                max = g;
        }
        lastAvg = (max + min) / 2;
        guesses.put(world.getRound(), new Integer[] { min, max });
    }

    private void putWinner() {
        String winner = null;
        Integer min = Integer.MAX_VALUE;
        int price = world.getPrice();
        for (Competitor c : world.getCompetitors()) {
            Integer g = c.getGuess();
            if (g == null)
                continue;
            g = g - price;
            if (g < min) {
                min = g;
                winner = c.getLocalName();
            }
        }

        winners.merge(winner, 1, Integer::sum);
    }

    public void writeData(long time) {
        File guessesF = new File("logs/" + time + "/guesses.csv");
        File winnersF = new File("logs/" + time + "/winners.csv");
        boolean done = false;

        try {
            done = guessesF.createNewFile();
            done = done && winnersF.createNewFile();
        } catch (IOException e1) {
            System.err.println("Error creating data files");
            return;
        }

        if (!done) {
            System.err.println("Error creating data files");
            return;
        }

        try (FileWriter guessesW = new FileWriter(guessesF); FileWriter winnersW = new FileWriter(winnersF);) {
            guessesW.write("key,min,max,avg\n");
            for (Entry<Integer, Integer[]> e : guesses.entrySet()) {
                Integer key = e.getKey(), v1 = e.getValue()[0], v2 = e.getValue()[1], avg = (v1+v2)/2;
                guessesW.write(key + "," + v1 + "," + v2 + "," + avg + "\n");
            }
            
            winnersW.write("id,wins\n");
            for (Entry<String, Integer> e : winners.entrySet()) {
                String key = e.getKey();
                Integer wins = e.getValue();
                winnersW.write(key + "," + wins + "\n");
            }
        } catch (Exception e) {
            System.err.println("Error writing to data files");
        }
    }

    Integer getLastAvg() {
        return lastAvg;
    }
}
