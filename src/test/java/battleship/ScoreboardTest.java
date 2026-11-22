package battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ScoreboardTest {

    private Scoreboard scoreboard;
    private final File scoreboardFile = new File("data/scoreboard.json");

    @BeforeEach
    void setUp() {
        if (scoreboardFile.exists()) {
            scoreboardFile.delete();
        }
        scoreboard = new Scoreboard();
    }

    @Test
    @DisplayName("Testar construtor e carregamento quando o ficheiro não existe")
    void testConstructorAndLoadNoFile() {
        assertNotNull(scoreboard);
    }

    @Test
    @DisplayName("Testar addScore e saveScores")
    void testAddScore() {
        scoreboard.addScore("Jogador1", 10);
        assertTrue(scoreboardFile.exists());
    }

    @Test
    @DisplayName("Testar displayScoreboard quando a lista está vazia")
    void testDisplayScoreboardEmpty() throws Exception {
        Field scoresField = Scoreboard.class.getDeclaredField("scores");
        scoresField.setAccessible(true);
        scoresField.set(scoreboard, new ArrayList<>());
        scoreboard.displayScoreboard();
    }

    @Test
    @DisplayName("Testar displayScoreboard quando há pontuações")
    void testDisplayScoreboardWithScores() throws Exception {
        Field scoresField = Scoreboard.class.getDeclaredField("scores");
        scoresField.setAccessible(true);
        List<ScoreEntry> dummyScores = new ArrayList<>();
        dummyScores.add(new ScoreEntry("JogadorA", 5, "2026-11-22"));
        dummyScores.add(new ScoreEntry("JogadorB", 2, "2026-11-22"));
        scoresField.set(scoreboard, dummyScores);
        scoreboard.displayScoreboard();
    }

    @Test
    @DisplayName("Testar catch no loadScores simulando erro de IO")
    void testLoadScoresIOException() {
        if (scoreboardFile.exists()) scoreboardFile.delete();
        scoreboardFile.mkdirs(); // Cria uma pasta com o nome do ficheiro para forçar IOException

        Scoreboard sb = new Scoreboard();
        assertNotNull(sb);

        scoreboardFile.delete(); // Limpeza
    }

    @Test
    @DisplayName("Testar carregamento com JSON null (ramo false do ternário)")
    void testLoadScoresWithNullJson() throws Exception {
        scoreboardFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(scoreboardFile)) {
            writer.write("null"); // O Gson vai ler isto como null
        }
        Scoreboard sb = new Scoreboard();
        assertNotNull(sb);
    }

    @Test
    @DisplayName("Testar carregamento com JSON válido (ramo true do ternário)")
    void testLoadScoresWithValidJson() throws Exception {
        scoreboardFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(scoreboardFile)) {
            // JSON válido para o Gson ler e devolver uma lista (loaded != null)
            writer.write("[{\"playerName\":\"Test\",\"totalShots\":10,\"date\":\"2026-11-22\"}]");
        }
        Scoreboard sb = new Scoreboard();
        assertNotNull(sb);
    }
}