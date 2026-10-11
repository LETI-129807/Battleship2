
package battleship;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Tasks {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String GOODBYE_MESSAGE = "Bons ventos!";

	// Comandos do jogo
	private static final String AJUDA = "ajuda";
	private static final String GERAFROTA = "gerafrota";
	private static final String LEFROTA = "lefrota";
	private static final String DESISTIR = "desisto";
	private static final String RAJADA = "rajada";
	private static final String TIROS = "tiros";
	private static final String MAPA = "mapa";
	private static final String STATUS = "estado";
	private static final String SIMULA = "simula";

	// Comando de exportacao para PDF
	private static final String EXPORTPDF = "exportpdf";

	private static final DateTimeFormatter PDF_DATE_FORMAT =
			DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

	/**
	 * Menu principal do jogo.
	 */
	public static void menu() {

		IFleet myFleet = null;
		IGame game = null;

		menuHelp();

		Scanner in = new Scanner(System.in);

		System.out.print("> ");

		while (in.hasNext()) {

			String command = in.next().toLowerCase();

			if (command.equals(DESISTIR)) {
				break;
			}

			switch (command) {

				case GERAFROTA:
					myFleet = Fleet.createRandom();
					game = new Game(myFleet);
					game.printMyBoard(false, true);
					break;

				case LEFROTA:
					myFleet = buildFleet(in);
					game = new Game(myFleet);
					game.printMyBoard(false, true);
					break;

				case STATUS:
					if (myFleet != null) {
						myFleet.printStatus();
					} else {
						System.out.println(
								"Primeiro tens de criar uma frota."
						);
					}
					break;

				case MAPA:
					if (game != null) {
						game.printMyBoard(false, true);
					} else {
						System.out.println(
								"Primeiro tens de iniciar um jogo."
						);
					}
					break;

				case RAJADA:
					if (game != null) {

						try {
							game.readEnemyFire(in);

							myFleet.printStatus();
							game.printMyBoard(true, false);

							if (game.getRemainingShips() == 0) {
								game.over();

								System.out.println(
										"A partida terminou. "
												+ "Podes utilizar exportpdf "
												+ "para guardar o historico."
								);
							}

						} catch (IllegalArgumentException e) {
							System.out.println(
									"Erro na rajada: " + e.getMessage()
							);
						}

					} else {
						System.out.println(
								"Primeiro tens de criar uma frota."
						);
					}
					break;

				case SIMULA:
					if (game != null) {

						if (game.getRemainingShips() == 0) {
							System.out.println(
									"A partida ja terminou."
							);
							break;
						}

						while (game.getRemainingShips() > 0) {

							game.randomEnemyFire();
							myFleet.printStatus();
							game.printMyBoard(true, false);

							if (game.getRemainingShips() == 0) {
								break;
							}

							try {
								Thread.sleep(3000);

							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								System.out.println(
										"Simulacao interrompida."
								);
								break;
							}
						}

						if (game.getRemainingShips() == 0) {
							game.over();

							System.out.println(
									"A partida terminou. "
											+ "Escreve exportpdf "
											+ "para guardar o relatorio."
							);
						}

					} else {
						System.out.println(
								"Primeiro tens de criar uma frota."
						);
					}
					break;

				case TIROS:
					if (game != null) {
						game.printMyBoard(true, true);
					} else {
						System.out.println(
								"Primeiro tens de iniciar um jogo."
						);
					}
					break;

				// Nova funcionalidade: exportar historico para PDF
				case EXPORTPDF:

					if (game == null) {

						System.out.println(
								"Primeiro tens de criar uma frota "
										+ "com gerafrota ou lefrota."
						);

					} else {

						try {
							String timestamp = LocalDateTime.now()
									.format(PDF_DATE_FORMAT);

							String fileName =
									"relatorio_battleship_"
											+ timestamp
											+ ".pdf";

							PdfExporter.export(game, fileName);

							Path pdfPath = Path.of(fileName)
									.toAbsolutePath();

							System.out.println(
									"PDF criado com sucesso!"
							);

							System.out.println(
									"Localizacao: " + pdfPath
							);

							System.out.println(
									"Total de jogadas exportadas: "
											+ game.getAlienMoves().size()
							);

						} catch (IOException
								 | IllegalArgumentException
								 | IllegalStateException e) {

							System.out.println(
									"Erro ao gerar PDF: "
											+ e.getMessage()
							);
						}
					}
					break;

				case AJUDA:
					menuHelp();
					break;

				default:
					System.out.println(
							"Comando desconhecido. "
									+ "Escreve ajuda para ver os comandos."
					);
			}

			System.out.print("> ");
		}

		System.out.println(GOODBYE_MESSAGE);
	}

	/**
	 * Apresenta os comandos disponiveis.
	 */
	public static void menuHelp() {

		System.out.println(
				"======================= AJUDA DO MENU ========================="
		);

		System.out.println(
				"Digite um dos comandos abaixo para interagir com o jogo:"
		);

		System.out.println(
				"- " + GERAFROTA
						+ ": Gera uma frota aleatoria de navios."
		);

		System.out.println(
				"- " + LEFROTA
						+ ": Permite criar e carregar uma frota personalizada."
		);

		System.out.println(
				"- " + STATUS
						+ ": Mostra o estado atual da frota."
		);

		System.out.println(
				"- " + MAPA
						+ ": Exibe o mapa da frota."
		);

		System.out.println(
				"- " + RAJADA
						+ ": Realiza uma rajada de 3 tiros (ex.: rajada A1 B3 C5)."
		);

		System.out.println(
				"- " + SIMULA
						+ ": Simula um jogo completo."
		);

		System.out.println(
				"- " + TIROS
						+ ": Mostra os tiros realizados no tabuleiro."
		);

		System.out.println(
				"- " + EXPORTPDF
						+ ": Exporta o historico de jogadas para PDF."
		);

		System.out.println(
				"- " + DESISTIR
						+ ": Encerra o jogo."
		);

		System.out.println(
				"==============================================================="
		);
	}

	/**
	 * Constroi uma frota a partir dos dados introduzidos.
	 */
	public static Fleet buildFleet(Scanner in) {

		assert in != null;

		Fleet fleet = new Fleet();
		int i = 0;

		while (i < Fleet.FLEET_SIZE) {

			IShip s = readShip(in);

			if (s != null) {

				boolean success = fleet.addShip(s);

				if (success) {
					i++;
				} else {
					LOGGER.info(
							"Falha na criacao de {} {} {}",
							s.getCategory(),
							s.getBearing(),
							s.getPosition()
					);
				}

			} else {
				LOGGER.info("Navio desconhecido!");
			}
		}

		LOGGER.info("{} navios adicionados com sucesso!", i);

		return fleet;
	}

	/**
	 * Le os dados de um navio.
	 */
	public static Ship readShip(Scanner in) {

		assert in != null;

		String shipKind = in.next();

		Position pos = readPosition(in);

		char c = in.next().charAt(0);

		Compass bearing = Compass.charToCompass(c);

		return Ship.buildShip(shipKind, bearing, pos);
	}

	/**
	 * Le uma posicao com coordenadas numericas.
	 */
	public static Position readPosition(Scanner in) {

		assert in != null;

		int row = in.nextInt();
		int column = in.nextInt();

		return new Position(row, column);
	}

	/**
	 * Le uma posicao no formato A3 ou A 3.
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {

		if (!in.hasNext()) {
			throw new IllegalArgumentException(
					"Nenhuma posicao valida encontrada!"
			);
		}

		String part1 = in.next();
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next();
		}

		String input = (part2 != null)
				? part1 + part2
				: part1;

		input = input.toUpperCase();

		if (input.matches("[A-Z]\\d+")) {

			char column = input.charAt(0);
			int row = Integer.parseInt(input.substring(1));

			return new Position(column, row);

		} else if (
				part2 != null
						&& part1.toUpperCase().matches("[A-Z]")
						&& part2.matches("\\d+")
		) {

			char column = part1.toUpperCase().charAt(0);
			int row = Integer.parseInt(part2);

			return new Position(column, row);

		} else {

			throw new IllegalArgumentException(
					"Formato invalido. Use 'A3', 'A 3' ou similar."
			);
		}
	}
}
