
package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoveTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static IGame.ShotResult result(
            boolean valid, boolean repeated,
            IShip ship, boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    private static IShip ship(String category) {
        IShip shipMock = mock(IShip.class);
        when(shipMock.getCategory()).thenReturn(category);
        return shipMock;
    }

    private static Move move(IGame.ShotResult... results) {
        return new Move(1, List.of(), List.of(results));
    }

    private static JsonNode json(Move move, boolean verbose)
            throws Exception {
        return MAPPER.readTree(move.processEnemyFire(verbose));
    }

    @Test
    @DisplayName("Verificar a representação textual da jogada")
    void testToString() {
        Move move = new Move(
                3,
                List.of(new Position(0, 0), new Position(1, 1)),
                List.of(result(true, false, null, false))
        );

        assertEquals(
                "Move{number=3, shots=2, results=1}",
                move.toString()
        );
    }

    @Test
    @DisplayName("Obter o número da jogada")
    void getNumber() {
        assertEquals(7, new Move(
                7, List.of(), List.of()).getNumber());
    }

    @Test
    @DisplayName("Obter as posições dos tiros da jogada")
    void getShots() {
        List<IPosition> shots = List.of(
                new Position(0, 0),
                new Position(1, 1)
        );

        Move move = new Move(1, shots, List.of());

        assertSame(shots, move.getShots());
    }

    @Test
    @DisplayName("Obter os resultados dos tiros da jogada")
    void getShotResults() {
        List<IGame.ShotResult> results = List.of(
                result(true, false, null, false)
        );

        Move move = new Move(1, List.of(), results);

        assertSame(results, move.getShotResults());
    }

    @Test
    @DisplayName("Processar um tiro válido que falha o navio")
    void processEnemyFire() throws Exception {
        JsonNode data = json(move(
                result(true, false, null, false)
        ), false);

        assertEquals(1, data.get("validShots").asInt());
        assertEquals(1, data.get("missedShots").asInt());
        assertEquals(0, data.get("repeatedShots").asInt());
        assertEquals(0, data.get("hitsOnBoats").size());
        assertEquals(0, data.get("sunkBoats").size());
    }

    @Test
    @DisplayName("Processar tiros inválidos fora do tabuleiro")
    void invalidShots() throws Exception {
        JsonNode data = json(move(
                result(false, false, null, false)
        ), false);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Processar um tiro repetido")
    void repeatedShots() throws Exception {
        JsonNode data = json(move(
                result(true, true, null, false)
        ), true);

        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(0, data.get("validShots").asInt());
    }

    @Test
    @DisplayName("Processar múltiplos tiros repetidos")
    void multipleRepeatedShots() throws Exception {
        JsonNode data = json(move(
                result(true, true, null, false),
                result(true, true, null, false)
        ), true);

        assertEquals(2, data.get("repeatedShots").asInt());
    }

    @Test
    @DisplayName("Processar um tiro que acerta num navio")
    void hitShip() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, false)
        ), true);

        assertEquals(1, data.get("validShots").asInt());
        assertEquals(1, data.get("hitsOnBoats").size());
        assertEquals("Fragata",
                data.get("hitsOnBoats").get(0)
                        .get("type").asText());
        assertEquals(1,
                data.get("hitsOnBoats").get(0)
                        .get("hits").asInt());
    }

    @Test
    @DisplayName("Processar múltiplos acertos em navios do mesmo tipo")
    void multipleHitsOnSameShipType() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, false),
                result(true, false, boat, false)
        ), true);

        assertEquals(2, data.get("validShots").asInt());
        assertEquals(2,
                data.get("hitsOnBoats").get(0)
                        .get("hits").asInt());
    }

    @Test
    @DisplayName("Processar um tiro que afunda um navio")
    void sunkShip() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, true)
        ), true);

        assertEquals(1, data.get("sunkBoats").size());
        assertEquals(1,
                data.get("sunkBoats").get(0)
                        .get("count").asInt());
        assertEquals(0, data.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Processar vários navios afundados do mesmo tipo")
    void multipleSunkShipsOfSameType() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, true),
                result(true, false, boat, true)
        ), true);

        assertEquals(2,
                data.get("sunkBoats").get(0)
                        .get("count").asInt());
    }

    @Test
    @DisplayName("Processar tiros em navios de tipos diferentes")
    void differentShipTypes() throws Exception {
        IShip frigate = ship("Fragata");
        IShip barge = ship("Barge");

        JsonNode data = json(move(
                result(true, false, frigate, false),
                result(true, false, barge, true)
        ), true);

        assertEquals(1, data.get("hitsOnBoats").size());
        assertEquals(1, data.get("sunkBoats").size());
    }

    @Test
    @DisplayName("Processar uma combinação de tiros diferentes")
    void mixedShots() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, null, false),
                result(true, false, boat, false),
                result(true, true, null, false),
                result(false, false, null, false)
        ), true);

        assertEquals(2, data.get("validShots").asInt());
        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(1, data.get("missedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS - 3,
                data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Processar múltiplos tiros na água")
    void multipleMissedShots() throws Exception {
        JsonNode data = json(move(
                result(true, false, null, false),
                result(true, false, null, false)
        ), true);

        assertEquals(2, data.get("missedShots").asInt());
    }

    @Test
    @DisplayName("Processar uma jogada sem resultados")
    void noResults() throws Exception {
        JsonNode data = json(move(), true);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(0, data.get("repeatedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Processar acertos e navios afundados na mesma jogada")
    void mixedSunkAndHits() throws Exception {
        IShip frigate = ship("Fragata");
        IShip barge = ship("Barge");

        JsonNode data = json(move(
                result(true, false, frigate, true),
                result(true, false, frigate, false),
                result(true, false, barge, false),
                result(true, false, null, false),
                result(true, true, null, false)
        ), true);

        assertEquals(4, data.get("validShots").asInt());
        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(1, data.get("sunkBoats").size());
        assertEquals(1, data.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Processar múltiplos resultados com verbose desativado")
    void verboseFalseWithMultipleResults() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, true),
                result(true, false, null, false),
                result(true, true, null, false)
        ), false);

        assertEquals(2, data.get("validShots").asInt());
        assertEquals(1, data.get("missedShots").asInt());
        assertEquals(1, data.get("repeatedShots").asInt());
    }

    @Test
    @DisplayName("Apresentar apenas acertos com verbose ativado")
    void verboseWithOnlyHits() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, false)
        ), true);

        assertEquals(1, data.get("hitsOnBoats").size());
        assertEquals(0, data.get("missedShots").asInt());
    }

    @Test
    @DisplayName("Apresentar apenas navios afundados com verbose ativado")
    void verboseWithOnlySunkShips() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, true)
        ), true);

        assertEquals(1, data.get("sunkBoats").size());
        assertEquals(0, data.get("missedShots").asInt());
    }

    @Test
    @DisplayName("Apresentar apenas tiros inválidos com verbose ativado")
    void verboseWithInvalidShotsOnly() throws Exception {
        JsonNode data = json(move(
                result(false, false, null, false)
        ), true);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                data.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar a contagem de dois tiros repetidos")
    void testMultipleRepeatedShots() throws Exception {

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(1, List.of(), results);

        JsonNode json = new ObjectMapper()
                .readTree(move.processEnemyFire(true));

        assertEquals(2, json.get("repeatedShots").asInt());
        assertEquals(0, json.get("validShots").asInt());
    }

    @Test
    @DisplayName("Verificar um tiro válido e um repetido com verbose ativado")
    void testOneValidAndOneRepeatedShotVerbose() throws Exception {

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(1, List.of(), results);

        JsonNode json = new ObjectMapper()
                .readTree(move.processEnemyFire(true));

        assertEquals(1, json.get("validShots").asInt());
        assertEquals(1, json.get("repeatedShots").asInt());
        assertEquals(1, json.get("missedShots").asInt());
    }
}
