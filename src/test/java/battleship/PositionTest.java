
package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

	@Test
	@DisplayName("Verificar a criação de uma posição com coordenadas inteiras")
	void testConstructorWithIntegers() {
		Position position = new Position(2, 3);

		assertEquals(2, position.getRow());
		assertEquals(3, position.getColumn());
		assertFalse(position.isOccupied());
		assertFalse(position.isHit());
	}

	@Test
	@DisplayName("Verificar a criação de uma posição com coordenadas clássicas")
	void testConstructorWithClassicCoordinates() {
		Position position = new Position('C', 4);

		assertEquals(2, position.getRow());
		assertEquals(3, position.getColumn());
		assertEquals('C', position.getClassicRow());
		assertEquals(4, position.getClassicColumn());
	}

	@Test
	@DisplayName("Verificar a criação de uma posição com letra minúscula")
	void testConstructorWithLowercaseRow() {
		Position position = new Position('c', 4);

		assertEquals(2, position.getRow());
		assertEquals(3, position.getColumn());
	}

	@Test
	@DisplayName("Verificar a geração de posições aleatórias dentro do tabuleiro")
	void testRandomPosition() {
		for (int i = 0; i < 100; i++) {
			Position position = Position.randomPosition();

			assertNotNull(position);
			assertTrue(position.isInside());
			assertTrue(position.getRow() >= 0);
			assertTrue(position.getRow() < Game.BOARD_SIZE);
			assertTrue(position.getColumn() >= 0);
			assertTrue(position.getColumn() < Game.BOARD_SIZE);
		}
	}

	@Test
	@DisplayName("Verificar se uma posição está dentro dos limites do tabuleiro")
	void testIsInside() {
		assertTrue(new Position(0, 0).isInside());
		assertTrue(new Position(
				Game.BOARD_SIZE - 1,
				Game.BOARD_SIZE - 1
		).isInside());

		assertFalse(new Position(-1, 0).isInside());
		assertFalse(new Position(0, -1).isInside());
		assertFalse(new Position(Game.BOARD_SIZE, 0).isInside());
		assertFalse(new Position(0, Game.BOARD_SIZE).isInside());
	}

	@Test
	@DisplayName("Verificar se duas posições são adjacentes")
	void testIsAdjacentTo() {
		Position center = new Position(4, 4);

		assertTrue(center.isAdjacentTo(new Position(3, 4)));
		assertTrue(center.isAdjacentTo(new Position(5, 4)));
		assertTrue(center.isAdjacentTo(new Position(4, 3)));
		assertTrue(center.isAdjacentTo(new Position(4, 5)));

		assertTrue(center.isAdjacentTo(new Position(3, 3)));
		assertTrue(center.isAdjacentTo(new Position(3, 5)));
		assertTrue(center.isAdjacentTo(new Position(5, 3)));
		assertTrue(center.isAdjacentTo(new Position(5, 5)));

		assertFalse(center.isAdjacentTo(new Position(6, 4)));
		assertFalse(center.isAdjacentTo(new Position(4, 6)));
		assertFalse(center.isAdjacentTo(new Position(6, 6)));

		// A implementacao atual considera a propria
		// posicao adjacente.
		assertTrue(center.isAdjacentTo(new Position(4, 4)));
	}

	@Test
	@DisplayName("Verificar as oito posições adjacentes no centro do tabuleiro")
	void testAdjacentPositionsAtCenter() {
		Position position = new Position(4, 4);

		List<IPosition> adjacent = position.adjacentPositions();

		assertEquals(8, adjacent.size());

		Set<IPosition> expected = Set.of(
				new Position(3, 4),
				new Position(4, 5),
				new Position(5, 4),
				new Position(4, 3),
				new Position(5, 5),
				new Position(5, 3),
				new Position(3, 5),
				new Position(3, 3)
		);

		assertEquals(expected, new HashSet<>(adjacent));
	}

	@Test
	@DisplayName("Verificar as três posições adjacentes nos cantos do tabuleiro")
	void testAdjacentPositionsAtCorners() {
		int last = Game.BOARD_SIZE - 1;

		assertEquals(3,
				new Position(0, 0).adjacentPositions().size());

		assertEquals(3,
				new Position(0, last).adjacentPositions().size());

		assertEquals(3,
				new Position(last, 0).adjacentPositions().size());

		assertEquals(3,
				new Position(last, last).adjacentPositions().size());
	}

	@Test
	@DisplayName("Verificar as cinco posições adjacentes nas bordas do tabuleiro")
	void testAdjacentPositionsAtEdges() {
		int last = Game.BOARD_SIZE - 1;

		assertEquals(5,
				new Position(0, 4).adjacentPositions().size());

		assertEquals(5,
				new Position(last, 4).adjacentPositions().size());

		assertEquals(5,
				new Position(4, 0).adjacentPositions().size());

		assertEquals(5,
				new Position(4, last).adjacentPositions().size());
	}

	@Test
	@DisplayName("Verificar a ocupação de uma posição")
	void testOccupy() {
		Position position = new Position(2, 3);

		assertFalse(position.isOccupied());

		position.occupy();

		assertTrue(position.isOccupied());

		position.occupy();

		assertTrue(position.isOccupied());
	}

	@Test
	@DisplayName("Verificar a marcação de uma posição atingida por um tiro")
	void testShoot() {
		Position position = new Position(2, 3);

		assertFalse(position.isHit());

		position.shoot();

		assertTrue(position.isHit());

		position.shoot();

		assertTrue(position.isHit());
	}

	@Test
	@DisplayName("Verificar a igualdade entre posições")
	void testEquals() {
		Position position = new Position(2, 3);
		Position equal = new Position(2, 3);

		assertEquals(position, position);
		assertEquals(position, equal);

		assertNotEquals(position, new Position(3, 3));
		assertNotEquals(position, new Position(2, 4));
		assertNotEquals(position, new Position(3, 4));

		assertNotEquals(null, position);
		assertNotEquals("C4", position);
	}

	@Test
	@DisplayName("Verificar a igualdade entre Position e IPosition")
	void testEqualsWithIPosition() {
		Position position = new Position(2, 3);

		IPosition other = new Position(2, 3);

		assertEquals(position, other);
	}

	@Test
	@DisplayName("Verificar o hashCode e a representação textual das posições")
	void testHashCodeAndToString() {
		Position first = new Position(2, 3);
		Position second = new Position(2, 3);

		assertEquals(first.hashCode(), second.hashCode());
		assertEquals("C4", first.toString());

		Position another = new Position(0, 0);

		assertEquals("A1", another.toString());
	}

	@Test
	@DisplayName("Verificar a comparação de uma posição com objetos diferentes")
	void testEqualsWithDifferentObject() {
		Position position = new Position(2, 3);

		assertFalse(position.equals("C4"));
		assertFalse(position.equals(null));
		assertFalse(position.equals(123));
	}
}
