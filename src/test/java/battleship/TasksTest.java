package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
/*import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
*/
import static org.junit.jupiter.api.Assertions.*;

/**
 * TasksTest
 *
 * @author Your Name
 * Date: 07/01/2026
 * Time: 00:53
 */
class TasksTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void taskA() {
	}

	@Test
	void taskB() {
	}

	@Test
	void taskC() {
	}

	@Test
	void taskD() {
	}

	@Test
	void buildFleet() {
	}

	@Test
	void readShip() {
	}

	@Test
	void readPosition() {
	}

	@Test
	void firingRound() {
	}

	@Test
	void testMenuExecutionWithGeraFrota() {
		// Simula o utilizador a digitar "DESISTIR" na consola para entrar e sair do menu
		String simulatedInput = "scoreboard\nexportpdf\ngerafrota\nestado\nmapa\nscoreboard\nsimula\ntiros\njanela\nexportpdf\ndesisto\n";
		ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
		System.setIn(in);

		// Executa o menu e garante que inicializa o turnWatch sem exceções
		assertDoesNotThrow(Tasks::menu);
	}

	@Test
	void testGameOverBySinkingAllShips() {
		String simulatedInput = String.join("\n",
				"rajada A1 A2 A3",
				"lefrota",
				"barco 0 0 n",
				"galeao 0 0 e",
				"galeao 0 6 s",
				"nau 0 0 e",
				"nau 2 0 e",
				"fragata 4 0 e",
				"caravela 6 0 e",
				"caravela 8 0 e",
				"caravela 4 6 e",
				"barca 6 6 n",
				"barca 6 8 n",
				"barca 8 6 n",
				"barca 8 8 n",
				"rajada A1 A2 A3",
				"rajada A7 B7 C7",
				"rajada C6 C8 E1",
				"rajada C1 C2 C3",
				"rajada C4 E7 E2",
				"rajada E3 E4 E8",
				"rajada G1 G2 G7",
				"rajada G9 I1 I2",
				"rajada I7 I8 I9\n"
		) + "desisto\n";

		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
		assertDoesNotThrow(Tasks::menu);
	}

	@Test
	void testRajadaWithInvalidArgument() {
		String simulatedInput = "simula\njanela\ngerafrota\nrajada AA BB CC\najuda\ndesisto\n";
		ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
		System.setIn(in);

		// Executa o menu e garante que inicializa o turnWatch sem exceções
		assertDoesNotThrow(Tasks::menu);
	}
}
