package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
/**
 * IGameTest
 *
 * @author DanielSequeira
 * Date: 21/11/2026
 * Time: 18:45
 */
class ScoreboardTest {

    private static final File DATA_DIR = new File("data");
    private static final File SCORE_FILE = new File("data/scoreboard.json");
    private static final File BACKUP_FILE = new File("data/scoreboard_backup.json");
    private boolean hadOriginalFile;
    private boolean hadDataDir;

    @BeforeEach
    void setUp() throws IOException {
        hadDataDir = DATA_DIR.exists();
        hadOriginalFile = SCORE_FILE.exists();

        if (hadOriginalFile) {
            Files.copy(SCORE_FILE.toPath(), BACKUP_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
            assertTrue(SCORE_FILE.delete());
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteRecursively(SCORE_FILE);

        if (hadOriginalFile && BACKUP_FILE.exists()) {
            if (!DATA_DIR.exists()) {
                assertTrue(DATA_DIR.mkdirs());
            }
            Files.move(BACKUP_FILE.toPath(), SCORE_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else if (!hadDataDir && DATA_DIR.exists()) {
            deleteRecursively(DATA_DIR);
        }
    }

    private void deleteRecursively(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    @Test
    @DisplayName("Verificar inicialização do Scoreboard quando o ficheiro não existe")
    void testConstructorWhenFileDoesNotExist() {
        Scoreboard scoreboard = new Scoreboard();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            scoreboard.displayScoreboard();
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        assertTrue(output.contains("SCOREBOARD"));
        assertTrue(output.contains("Nenhum jogo registado ainda."));
    }

    @Test
    @DisplayName("Verificar inicialização do Scoreboard quando o ficheiro JSON está vazio (null)")
    void testConstructorWhenFileIsEmpty() throws IOException {
        if (!DATA_DIR.exists()) {
            assertTrue(DATA_DIR.mkdirs());
        }
        try (FileWriter writer = new FileWriter(SCORE_FILE)) {
            writer.write("");
        }

        Scoreboard scoreboard = new Scoreboard();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            scoreboard.displayScoreboard();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(out.toString().contains("Nenhum jogo registado ainda."));
    }

    @Test
    @DisplayName("Verificar tratamento de IOException ao carregar Scoreboard")
    void testLoadScoresIOException() {
        // Criar uma diretoria com o mesmo nome do ficheiro força FileNotFoundException / IOException no FileReader
        assertTrue(SCORE_FILE.mkdirs());

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(err));

        try {
            Scoreboard scoreboard = new Scoreboard();
            assertNotNull(scoreboard);
        } finally {
            System.setErr(originalErr);
        }

        assertTrue(err.toString().contains("Erro ao carregar o Scoreboard:"));
    }

    @Test
    @DisplayName("Verificar adição de pontuações e criação da diretoria data quando não existe")
    void testAddScoreCreatesDirectoryAndSaves() {
        deleteRecursively(DATA_DIR);
        assertFalse(DATA_DIR.exists());

        Scoreboard scoreboard = new Scoreboard();
        scoreboard.addScore("Alice", 25);

        assertTrue(DATA_DIR.exists());
        assertTrue(SCORE_FILE.exists());

        // Carregar numa nova instância para confirmar persistência em JSON
        Scoreboard reloaded = new Scoreboard();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            reloaded.displayScoreboard();
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        assertTrue(output.contains("1. "));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("25"));
    }

    @Test
    @DisplayName("Verificar ordenação crescente de pontuações ao exibir o Scoreboard")
    void testDisplayScoreboardSortsEntriesByTotalShots() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.addScore("JogadorLento", 40);
        scoreboard.addScore("JogadorRapido", 18);
        scoreboard.addScore("JogadorMedio", 27);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        try {
            scoreboard.displayScoreboard();
        } finally {
            System.setOut(originalOut);
        }

        String output = out.toString();
        int posRapido = output.indexOf("JogadorRapido");
        int posMedio = output.indexOf("JogadorMedio");
        int posLento = output.indexOf("JogadorLento");

        assertTrue(posRapido != -1 && posMedio != -1 && posLento != -1);
        assertTrue(posRapido < posMedio);
        assertTrue(posMedio < posLento);
        assertTrue(output.contains("1. "));
        assertTrue(output.contains("2. "));
        assertTrue(output.contains("3. "));
    }

    @Test
    @DisplayName("Verificar tratamento de IOException ao guardar o Scoreboard")
    void testSaveScoresIOException() {
        Scoreboard scoreboard = new Scoreboard();

        // Criar uma diretoria com o caminho do ficheiro força IOException no FileWriter
        assertTrue(SCORE_FILE.mkdirs());

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(err));

        try {
            scoreboard.addScore("Bob", 30);
        } finally {
            System.setErr(originalErr);
        }

        assertTrue(err.toString().contains("Erro ao guardar o Scoreboard:"));
    }
}