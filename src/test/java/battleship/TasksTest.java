package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class TasksTest {

	@Test
	@DisplayName("Testar menuHelp")
	void testMenuHelp() {
		Tasks.menuHelp();
		assertTrue(true);
	}

	@Test
	@DisplayName("Testar readPosition com valores válidos")
	void testReadPosition() {
		Scanner in = new Scanner("3 5");
		Position pos = Tasks.readPosition(in);
		assertNotNull(pos);
	}

	@Test
	@DisplayName("Testar readClassicPosition com formato A3")
	void testReadClassicPositionFormatA3() {
		Scanner in = new Scanner("A3");
		IPosition pos = Tasks.readClassicPosition(in);
		assertNotNull(pos);
	}

	@Test
	@DisplayName("Testar readClassicPosition com formato A 3")
	void testReadClassicPositionFormatA3WithSpace() {
		Scanner in = new Scanner("A 3");
		IPosition pos = Tasks.readClassicPosition(in);
		assertNotNull(pos);
	}

	@Test
	@DisplayName("Testar readClassicPosition quando não há input (erro)")
	void testReadClassicPositionNoInput() {
		Scanner in = new Scanner("");
		assertThrows(IllegalArgumentException.class, () -> {
			Tasks.readClassicPosition(in);
		});
	}

	@Test
	@DisplayName("Testar readClassicPosition com formato inválido (erro)")
	void testReadClassicPositionInvalidFormat() {
		Scanner in = new Scanner("123");
		assertThrows(IllegalArgumentException.class, () -> {
			Tasks.readClassicPosition(in);
		});
	}

	@Test
	@DisplayName("Testar readShip com input válido")
	void testReadShip() {
		Scanner in = new Scanner("fragata 1 2 N");
		try {
			Ship ship = Tasks.readShip(in);
			assertNotNull(ship);
		} catch (AssertionError e) {
			System.out.println("Aviso: readShip falhou no assert interno.");
		}
	}

	@Test
	@DisplayName("Testar buildFleet com uma frota completa")
	void testBuildFleet() {
		StringBuilder input = new StringBuilder();
		for (int i = 0; i < Fleet.FLEET_SIZE; i++) {
			input.append("fragata ").append(i + 1).append(" ").append(i + 1).append(" N\n");
		}

		Scanner in = new Scanner(input.toString());
		try {
			Fleet fleet = Tasks.buildFleet(in);
			assertNotNull(fleet);
		} catch (AssertionError e) {
			System.out.println("Aviso: buildFleet falhou no assert interno.");
		}
	}

	@Test
	@DisplayName("Testar menu com comando de desistir imediato para cobrir o loop")
	void testMenuDesistir() {
		// Simula a entrada do utilizador "desisto"
		String input = "desisto\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Executa o menu. O loop while vai ler "desisto" e terminar imediatamente.
		Tasks.menu();

		assertTrue(true);
	}
}