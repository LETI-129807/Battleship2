
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
    private static final String SCOREBOARD = "scoreboard";

    // Comando de exportacao para PDF
    private static final String EXPORTPDF = "exportpdf";

    private static final DateTimeFormatter PDF_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private static final Scoreboard scoreboardManager = new Scoreboard();

    /**
     * Executa os comandos introduzidos pelo jogador.
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        Game game = new Game();

        System.out.println("Bem-vindo ao Batalha Naval!");
        menuHelp();

        boolean running = true;

        while (running && in.hasNextLine()) {

            String line = in.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            Scanner commandScanner = new Scanner(line);

            String command = commandScanner.next().toLowerCase();

            try {

                switch (command) {

                    case AJUDA:
                        menuHelp();
                        break;

                    case GERAFROTA:
                        Fleet fleet = buildFleet(commandScanner);
                        game.setMyFleet(fleet);
                        break;

                    case LEFROTA:
                        game.readMyFleet(commandScanner);
                        break;

                    case MAPA:
                        game.printMyBoard(true, false);
                        break;

                    case STATUS:
                        game.getMyFleet().printStatus();
                        break;

                    case TIROS:
                        game.printEnemyBoard();
                        break;

                    case RAJADA:
                        game.readEnemyFire(commandScanner);
                        game.getMyFleet().printStatus();
                        game.printMyBoard(true, false);

                        if (game.getMyFleet().getRemainingShips() == 0) {
                            game.over();
                            System.out.println(
                                    "Pode exportar o historico com o comando exportpdf."
                            );
                        }
                        break;

                    case SIMULA:
                        game.simulate();

                        if (game.getMyFleet().getRemainingShips() == 0) {
                            game.over();
                            System.out.println(
                                    "Pode exportar o historico com o comando exportpdf."
                            );
                        }
                        break;

                    case SCOREBOARD:
                        scoreboardManager.displayScores();
                        break;

                    case EXPORTPDF:
                        String fileName = "historico_"
                                + LocalDateTime.now().format(PDF_DATE_FORMAT)
                                + ".pdf";

                        Path outputPath = Path.of(fileName);

                        PdfExporter.export(game, outputPath);

                        System.out.println(
                                "Historico exportado para: "
                                        + outputPath.toAbsolutePath()
                        );
                        break;

                    case DESISTIR:
                        System.out.println(GOODBYE_MESSAGE);
                        running = false;
                        break;

                    default:
                        System.out.println(
                                "Comando desconhecido. Escreva ajuda."
                        );
                        break;
                }

            } catch (IllegalArgumentException e) {

                System.out.println("Erro: " + e.getMessage());

            } catch (IOException e) {

                System.out.println(
                        "Erro ao exportar PDF: " + e.getMessage()
                );
            }

            commandScanner.close();
        }

        in.close();
    }

    /**
     * Mostra os comandos disponiveis.
     */
    public static void menuHelp() {

        System.out.println("===============================================================");
        System.out.println("COMANDOS DISPONIVEIS");
        System.out.println("===============================================================");
        System.out.println("- " + AJUDA + ": Mostra esta ajuda.");
        System.out.println("- " + GERAFROTA + ": Gera uma frota.");
        System.out.println("- " + LEFROTA + ": Le uma frota.");
        System.out.println("- " + MAPA + ": Mostra o mapa.");
        System.out.println("- " + STATUS + ": Mostra o estado da frota.");
        System.out.println("- " + RAJADA + ": Realiza uma rajada de 3 tiros (ex.: rajada A1 B3 C5).");
        System.out.println("- " + SIMULA + ": Simula um jogo completo.");
        System.out.println("- " + TIROS + ": Mostra os tiros realizados no tabuleiro.");
        System.out.println("- " + SCOREBOARD + ": Exibe a tabela de pontuacoes dos jogos passados.");
        System.out.println("- " + EXPORTPDF + ": Exporta o historico de jogadas para PDF.");
        System.out.println("- " + DESISTIR + ": Encerra o jogo.");
        System.out.println("===============================================================");
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
