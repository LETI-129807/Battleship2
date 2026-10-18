
package battleship;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The type Tasks.
 */
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

    // Funcionalidades dos membros do grupo
    private static final String SCOREBOARD = "scoreboard";
    private static final String JANELA = "janela";
    private static final String EXPORTPDF = "exportpdf";

    private static final DateTimeFormatter PDF_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private static final Scoreboard scoreboardManager = new Scoreboard();

    /**
     * Menu principal do jogo.
     */
    public static void menu() {

        IFleet myFleet = null;
        IGame game = null;

        menuHelp();

        System.out.print("> ");

        Scanner in = new Scanner(System.in);
        String command = in.next();

        while (!command.equals(DESISTIR)) {

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
                    }
                    break;

                case MAPA:
                    if (game != null) {
                        game.printMyBoard(false, true);
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

                                scoreboardManager.addScore(
                                        "Jogador",
                                        game.getMyMoves().size()
                                );

                                scoreboardManager.displayScoreboard();

                                System.out.println(
                                        "Jogo terminado. Pode escrever exportpdf para guardar o historico."
                                );
                            }

                        } catch (IllegalArgumentException e) {
                            System.out.println(
                                    "Erro: " + e.getMessage()
                            );
                        }

                    } else {
                        System.out.println(
                                "Primeiro tem de criar uma frota."
                        );
                    }
                    break;

                case SIMULA:
                    if (game != null) {

                        while (game.getRemainingShips() > 0) {

                            game.randomEnemyFire();
                            myFleet.printStatus();
                            game.printMyBoard(true, false);

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }

                        if (game.getRemainingShips() == 0) {

                            game.over();

                            scoreboardManager.addScore(
                                    "Simulação",
                                    game.getAlienMoves().size()
                            );

                            scoreboardManager.displayScoreboard();

                            System.out.println(
                                    "Simulacao terminada. Pode escrever exportpdf para guardar o historico."
                            );
                        }

                    } else {
                        System.out.println(
                                "Primeiro tem de criar uma frota."
                        );
                    }
                    break;

                case TIROS:
                    if (game != null) {
                        game.printMyBoard(true, true);
                    }
                    break;

                // Funcionalidade Scoreboard
                case SCOREBOARD:
                    scoreboardManager.displayScoreboard();
                    break;

                // Funcionalidade Janela Grafica
                case JANELA:
                    if (game != null) {
                        battleship.gui.BattleshipGUI.show(game);
                    } else {
                        System.out.println(
                                "Gera ou carrega uma frota primeiro (usa 'gerafrota' ou 'lefrota')."
                        );
                    }
                    break;

                // Funcionalidade Exportar PDF
                case EXPORTPDF:
                    if (game != null) {

                        String fileName = "historico_"
                                + LocalDateTime.now().format(PDF_DATE_FORMAT)
                                + ".pdf";

                        try {

                            PdfExporter.export(game, fileName);

                            System.out.println(
                                    "Historico exportado para PDF: "
                                            + fileName
                            );

                        } catch (IOException e) {

                            System.out.println(
                                    "Erro ao exportar PDF: "
                                            + e.getMessage()
                            );
                        }

                    } else {
                        System.out.println(
                                "Nao existe nenhum jogo para exportar."
                        );
                    }
                    break;

                case AJUDA:
                    menuHelp();
                    break;

                default:
                    System.out.println(
                            "Que comando é esse??? Repete ..."
                    );
                    break;
            }

            System.out.print("> ");
            command = in.next();
        }

        System.out.println(GOODBYE_MESSAGE);
    }

    /**
     * Apresenta os comandos disponiveis.
     */
    public static void menuHelp() {

        System.out.println("======================= AJUDA DO MENU =========================");
        System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");

        System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
        System.out.println("- " + LEFROTA + ": Permite criar e carregar uma frota personalizada.");
        System.out.println("- " + STATUS + ": Mostra o status atual da frota.");
        System.out.println("- " + MAPA + ": Exibe o mapa da frota.");
        System.out.println("- " + RAJADA + ": Realiza uma rajada de disparos.");
        System.out.println("- " + SIMULA + ": Simula um jogo completo.");
        System.out.println("- " + TIROS + ": Lista os tiros válidos realizados (* = tiro em navio, o = tiro na água)");
        System.out.println("- " + SCOREBOARD + ": Exibe a tabela de pontuações dos jogos passados.");
        System.out.println("- " + JANELA + ": Abre a visualização gráfica dos tabuleiros.");
        System.out.println("- " + EXPORTPDF + ": Exporta o historico de jogadas para PDF.");
        System.out.println("- " + DESISTIR + ": Encerra o jogo.");

        System.out.println("===============================================================");
    }

    /**
     * Constroi uma frota a partir dos dados introduzidos.
     *
     * @param in Scanner de entrada
     * @return Frota construida
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
     *
     * @param in Scanner de entrada
     * @return Navio construido
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
     * Le uma posicao numerica.
     *
     * @param in Scanner de entrada
     * @return Posicao lida
     */
    public static Position readPosition(Scanner in) {

        assert in != null;

        int row = in.nextInt();
        int column = in.nextInt();

        return new Position(row, column);
    }

    /**
     * Le uma posicao no formato A3 ou A 3.
     *
     * @param in Scanner de entrada
     * @return Posicao lida
     */
    public static IPosition readClassicPosition(@NotNull Scanner in) {

        if (!in.hasNext()) {
            throw new IllegalArgumentException(
                    "Nenhuma posição válida encontrada!"
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
                    "Formato inválido. Use 'A3', 'A 3' ou similar."
            );
        }
    }
}
