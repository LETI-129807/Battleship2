package battleship;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scoreboard {
    private static final String FILE_PATH = "data/scoreboard.json";
    private final Gson gson;
    private List<ScoreEntry> scores;

    public Scoreboard() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.scores = loadScores();
    }

    private List<ScoreEntry> loadScores() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<ScoreEntry>>() {}.getType();
            List<ScoreEntry> loaded = gson.fromJson(reader, listType);
            return (loaded != null) ? loaded : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar o Scoreboard: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void addScore(String playerName, int totalShots) {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        scores.add(new ScoreEntry(playerName, totalShots, LocalDate.now().toString()));
        saveScores();
    }

    private void saveScores() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(scores, writer);
        } catch (IOException e) {
            System.err.println("Erro ao guardar o Scoreboard: " + e.getMessage());
        }
    }

    public void displayScoreboard() {
        if (scores.isEmpty()) {
            System.out.println("\n=================== SCOREBOARD ===================");
            System.out.println("Nenhum jogo registado ainda.");
            System.out.println("=================================================\n");
            return;
        }

        scores.sort(Comparator.comparingInt(ScoreEntry::getTotalShots));

        System.out.println("\n=================== SCOREBOARD ===================");
        int rank = 1;
        for (ScoreEntry entry : scores) {
            System.out.println(rank + ". " + entry);
            rank++;
        }
        System.out.println("=================================================\n");
    }
}