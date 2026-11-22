package battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * IGameTest
 *
 * @author DanielSequeira
 * Date: 21/11/2026
 * Time: 17:13
 */
class IGameTest {
    @Test
    @DisplayName("Verificar a criação de um ShotResult válido sem navio atingido")
    void testShotResultMiss() {
        IGame.ShotResult result = new IGame.ShotResult(true, false, null, false);

        assertTrue(result.valid());
        assertFalse(result.repeated());
        assertNull(result.ship());
        assertFalse(result.sunk());
    }

    @Test
    @DisplayName("Verificar a criação de um ShotResult com acerto e afundamento de navio")
    void testShotResultHitAndSunk() {
        IShip ship = new Barge(Compass.NORTH, new Position(0, 0));
        IGame.ShotResult result = new IGame.ShotResult(true, false, ship, true);

        assertTrue(result.valid());
        assertFalse(result.repeated());
        assertEquals(ship, result.ship());
        assertTrue(result.sunk());
    }

    @Test
    @DisplayName("Verificar a criação de um ShotResult inválido e repetido")
    void testShotResultInvalidAndRepeated() {
        IGame.ShotResult result = new IGame.ShotResult(false, true, null, false);

        assertFalse(result.valid());
        assertTrue(result.repeated());
        assertNull(result.ship());
        assertFalse(result.sunk());
    }

    @Test
    @DisplayName("Verificar igualdade, hashCode e toString do record ShotResult")
    void testShotResultEqualsHashCodeAndToString() {
        IShip ship = new Barge(Compass.EAST, new Position(2, 3));
        IGame.ShotResult first = new IGame.ShotResult(true, false, ship, false);
        IGame.ShotResult second = new IGame.ShotResult(true, false, ship, false);
        IGame.ShotResult different = new IGame.ShotResult(false, false, null, false);

        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, different);
        assertNotEquals(null, first);
        assertNotEquals("ShotResult", first);

        assertNotNull(first.toString());
        assertTrue(first.toString().contains("ShotResult"));
    }

    @Test
    @DisplayName("Verificar o contrato dos métodos da interface IGame")
    void testIGameContract() {
        IGame game = new IGame() {
            @Override
            public String randomEnemyFire() {
                return "Miss";
            }

            @Override
            public String readEnemyFire(Scanner in) {
                return in.hasNextLine() ? in.nextLine() : "";
            }

            @Override
            public void fireShots(List<IPosition> shots) {
            }

            @Override
            public ShotResult fireSingleShot(SingleShotParams singleShotParams) {
                return new ShotResult(true, singleShotParams.isRepeated(), null, false);
            }

            @Override
            public IFleet getMyFleet() {
                return null;
            }

            @Override
            public List<IMove> getAlienMoves() {
                return Collections.emptyList();
            }

            @Override
            public IFleet getAlienFleet() {
                return null;
            }

            @Override
            public List<IMove> getMyMoves() {
                return Collections.emptyList();
            }

            @Override
            public int getRepeatedShots() {
                return 0;
            }

            @Override
            public int getInvalidShots() {
                return 0;
            }

            @Override
            public int getHits() {
                return 0;
            }

            @Override
            public int getSunkShips() {
                return 0;
            }

            @Override
            public int getRemainingShips() {
                return 5;
            }

            @Override
            public void printMyBoard(boolean show_shots, boolean show_legend) {
            }

            @Override
            public void printAlienBoard(boolean show_shots, boolean show_legend) {
            }

            @Override
            public void over() {
            }
        };

        assertEquals("Miss", game.randomEnemyFire());
        assertEquals("Hit", game.readEnemyFire(new Scanner("Hit")));
        assertEquals("", game.readEnemyFire(new Scanner("")));
        assertDoesNotThrow(() -> game.fireShots(Collections.emptyList()));
        assertTrue(game.fireSingleShot(new SingleShotParams(new Position(1, 1), false)).valid());
        assertNull(game.getMyFleet());
        assertTrue(game.getAlienMoves().isEmpty());
        assertNull(game.getAlienFleet());
        assertTrue(game.getMyMoves().isEmpty());
        assertEquals(0, game.getRepeatedShots());
        assertEquals(0, game.getInvalidShots());
        assertEquals(0, game.getHits());
        assertEquals(0, game.getSunkShips());
        assertEquals(5, game.getRemainingShips());
        assertDoesNotThrow(() -> game.printMyBoard(true, false));
        assertDoesNotThrow(() -> game.printAlienBoard(false, true));
        assertDoesNotThrow(game::over);
    }
}