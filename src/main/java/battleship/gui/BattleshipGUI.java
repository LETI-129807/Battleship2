package battleship.gui;

import battleship.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BattleshipGUI extends Application {

    private static final int TAMANHO = Game.BOARD_SIZE;
    private static final int LADO = 34;

    private static final Color COR_AGUA      = Color.web("#a8d5e8");
    private static final Color COR_NAVIO     = Color.web("#546e7a");
    private static final Color COR_ACERTO    = Color.web("#e53935");
    private static final Color COR_AGUA_TIRO = Color.web("#b0bec5");
    private static final Color COR_AFUNDADO  = Color.web("#212121");

    private static IGame sharedGame;
    private static Stage stage;
    private static BattleshipGUI instance;

    private Rectangle[][] celulasMeu;
    private Rectangle[][] celulasAlien;

    /**
     * Abre (ou reabre) a janela com o estado atual do jogo.
     * Chamado pelo menu do Tasks quando o utilizador escreve "janela".
     */
    public static void show(IGame game) {
        sharedGame = game;

        if (instance == null) {
            Platform.startup(() -> {
                instance = new BattleshipGUI();
                stage = new Stage();
                try {
                    instance.start(stage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            Platform.runLater(() -> {
                instance.atualizar();
                stage.toFront();
                stage.requestFocus();
            });
        }
    }

    @Override
    public void start(Stage stage) {
        BattleshipGUI.stage = stage;

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20; -fx-background-color: #263238;");

        Label titulo = new Label("Batalha Naval — Tabuleiros");
        titulo.setTextFill(Color.WHITE);
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Dois tabuleiros lado a lado
        VBox boxMeu = criarBloco("Meu Tabuleiro");
        VBox boxAlien = criarBloco("Tabuleiro do Inimigo");

        // Guarda as referências às matrizes de células
        celulasMeu   = (Rectangle[][]) boxMeu.getUserData();
        celulasAlien = (Rectangle[][]) boxAlien.getUserData();

        HBox tabuleiros = new HBox(30, boxMeu, boxAlien);
        tabuleiros.setAlignment(Pos.CENTER);

        // Legenda de cores
        HBox legenda = criarLegenda();

        root.getChildren().addAll(titulo, tabuleiros, legenda);

        Scene cena = new Scene(root);
        stage.setTitle("Battleship — GUI");
        stage.setScene(cena);
        stage.setOnCloseRequest(e -> {
            e.consume();     // impede o fecho "normal" da JVM
            stage.hide();    // esconde a janela
        });
        stage.show();

        atualizar();
    }

    /** Cria um bloco VBox com título + grelha, e guarda a matriz de células em userData. */
    private VBox criarBloco(String titulo) {
        Label lbl = new Label(titulo);
        lbl.setTextFill(Color.WHITE);
        lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grelha = new GridPane();
        grelha.setAlignment(Pos.CENTER);

        Rectangle[][] celulas = new Rectangle[TAMANHO][TAMANHO];

        // Cabeçalho de colunas (1 a 10)
        for (int c = 0; c < TAMANHO; c++) {
            Label cl = new Label(String.valueOf(c + 1));
            cl.setTextFill(Color.WHITE);
            cl.setMinWidth(LADO);
            cl.setAlignment(Pos.CENTER);
            cl.setStyle("-fx-font-size: 10px;");
            grelha.add(cl, c + 1, 0);
        }

        // Linhas (A a J)
        for (int r = 0; r < TAMANHO; r++) {
            char letra = (char) ('A' + r);
            Label rl = new Label(String.valueOf(letra));
            rl.setTextFill(Color.WHITE);
            rl.setMinWidth(18);
            rl.setAlignment(Pos.CENTER);
            rl.setStyle("-fx-font-size: 10px;");
            grelha.add(rl, 0, r + 1);

            for (int c = 0; c < TAMANHO; c++) {
                Rectangle celula = new Rectangle(LADO, LADO);
                celula.setFill(COR_AGUA);
                celula.setStroke(Color.web("#37474f"));
                Tooltip.install(celula, new Tooltip(letra + "" + (c + 1)));
                celulas[r][c] = celula;
                grelha.add(celula, c + 1, r + 1);
            }
        }

        VBox box = new VBox(8, lbl, grelha);
        box.setAlignment(Pos.CENTER);
        box.setUserData(celulas);
        return box;
    }

    private HBox criarLegenda() {
        HBox legenda = new HBox(20);
        legenda.setAlignment(Pos.CENTER);

        legenda.getChildren().addAll(
                itemLegenda("Água", COR_AGUA),
                itemLegenda("Navio", COR_NAVIO),
                itemLegenda("Tiro na água", COR_AGUA_TIRO),
                itemLegenda("Tiro certeiro", COR_ACERTO),
                itemLegenda("Afundado", COR_AFUNDADO)
        );
        return legenda;
    }

    private HBox itemLegenda(String texto, Color cor) {
        Rectangle quadrado = new Rectangle(16, 16, cor);
        quadrado.setStroke(Color.web("#37474f"));
        Label lbl = new Label(texto);
        lbl.setTextFill(Color.WHITE);
        lbl.setStyle("-fx-font-size: 11px;");
        HBox item = new HBox(6, quadrado, lbl);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    /** Redesenha ambos os tabuleiros com base no estado atual do jogo. */
    private void atualizar() {
        if (sharedGame == null) return;

        pintarFrota(celulasMeu, sharedGame.getMyFleet(), sharedGame.getAlienMoves());
        pintarFrota(celulasAlien, sharedGame.getAlienFleet(), sharedGame.getMyMoves());
    }

    private void pintarFrota(Rectangle[][] celulas, IFleet frota, java.util.List<IMove> tiros) {
        // 1. Reset
        for (int r = 0; r < TAMANHO; r++)
            for (int c = 0; c < TAMANHO; c++)
                celulas[r][c].setFill(COR_AGUA);

        // 2. Pinta navios
        for (IShip ship : frota.getShips()) {
            Color cor = ship.stillFloating() ? COR_NAVIO : COR_AFUNDADO;
            for (IPosition pos : ship.getPositions()) {
                if (pos.isInside())
                    celulas[pos.getRow()][pos.getColumn()].setFill(cor);
            }
        }

        // 3. Sobrepõe os tiros
        for (IMove move : tiros) {
            for (IPosition shot : move.getShots()) {
                if (!shot.isInside()) continue;
                Rectangle cel = celulas[shot.getRow()][shot.getColumn()];
                Color atual = (Color) cel.getFill();
                if (atual.equals(COR_NAVIO) || atual.equals(COR_AFUNDADO))
                    cel.setFill(COR_ACERTO);
                else if (atual.equals(COR_AGUA))
                    cel.setFill(COR_AGUA_TIRO);
            }
        }
    }

    public static void main(String[] args) {
        IFleet f = Fleet.createRandom();
        sharedGame = new Game(f);
        instance = new BattleshipGUI();
        launch(args);
    }
}