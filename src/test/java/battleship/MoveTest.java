
package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    void getNumber() {
        assertEquals(7, new Move(
                7, List.of(), List.of()).getNumber());
    }

    @Test
    void getShots() {
        List<IPosition> shots = List.of(
                new Position(0, 0),
                new Position(1, 1)
        );

        Move move = new Move(1, shots, List.of());

        assertSame(shots, move.getShots());
    }

    @Test
    void getShotResults() {
        List<IGame.ShotResult> results = List.of(
                result(true, false, null, false)
        );

        Move move = new Move(1, List.of(), results);

        assertSame(results, move.getShotResults());
    }

    @Test
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
    void invalidShots() throws Exception {
        JsonNode data = json(move(
                result(false, false, null, false)
        ), false);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                data.get("outsideShots").asInt());
    }

    @Test
    void repeatedShots() throws Exception {
        JsonNode data = json(move(
                result(true, true, null, false)
        ), true);

        assertEquals(1, data.get("repeatedShots").asInt());
        assertEquals(0, data.get("validShots").asInt());
    }

    @Test
    void multipleRepeatedShots() throws Exception {
        JsonNode data = json(move(
                result(true, true, null, false),
                result(true, true, null, false)
        ), true);

        assertEquals(2, data.get("repeatedShots").asInt());
    }

    @Test
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
    void multipleMissedShots() throws Exception {
        JsonNode data = json(move(
                result(true, false, null, false),
                result(true, false, null, false)
        ), true);

        assertEquals(2, data.get("missedShots").asInt());
    }

    @Test
    void noResults() throws Exception {
        JsonNode data = json(move(), true);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(0, data.get("repeatedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                data.get("outsideShots").asInt());
    }

    @Test
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
    void verboseWithOnlyHits() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, false)
        ), true);

        assertEquals(1, data.get("hitsOnBoats").size());
        assertEquals(0, data.get("missedShots").asInt());
    }

    @Test
    void verboseWithOnlySunkShips() throws Exception {
        IShip boat = ship("Fragata");

        JsonNode data = json(move(
                result(true, false, boat, true)
        ), true);

        assertEquals(1, data.get("sunkBoats").size());
        assertEquals(0, data.get("missedShots").asInt());
    }

    @Test
    void verboseWithInvalidShotsOnly() throws Exception {
        JsonNode data = json(move(
                result(false, false, null, false)
        ), true);

        assertEquals(0, data.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                data.get("outsideShots").asInt());
    }
}
/////