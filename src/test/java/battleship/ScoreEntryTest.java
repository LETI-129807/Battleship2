package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoreEntryTest {

    @Test
    @DisplayName("Testar se os getters devolvem os valores corretos após a criação")
    void testGetters() {
        // Arrange: Criar o objeto com os parâmetros exatos do construtor (String, int, String)
        ScoreEntry entry = new ScoreEntry("Jogador1", 15, "2026-11-22");

        // Assert: Verificar se os getters devolvem os valores que passámos
        assertEquals("Jogador1", entry.getPlayerName());
        assertEquals(15, entry.getTotalShots());
        assertEquals("2026-11-22", entry.getDate());
    }

    @Test
    @DisplayName("Testar se o toString devolve a string formatada corretamente")
    void testToString() {
        // Arrange
        ScoreEntry entry = new ScoreEntry("Jogador1", 15, "2026-11-22");

        // Act
        String resultado = entry.toString();

        // Assert
        // Em vez de comparar a string exata (que é sensível a espaços),
        // verificamos se contém as partes essenciais.
        assertTrue(resultado.contains("Jogador1"));
        assertTrue(resultado.contains("15"));
        assertTrue(resultado.contains("2026-11-22"));
        assertTrue(resultado.contains("Jogador:"));
        assertTrue(resultado.contains("Tiros Efetuados:"));
        assertTrue(resultado.contains("Data:"));
    }
}