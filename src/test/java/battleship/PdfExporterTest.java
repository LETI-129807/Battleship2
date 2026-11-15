
package battleship;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfExporterTest {

    private IGame game;

    @BeforeEach
    void setUp() {
        game = new Game(new Fleet());
    }

    @AfterEach
    void tearDown() {
        game = null;
    }

    // Metodo auxiliar para exportar e ler o PDF
    private String exportAndRead(IGame gameToExport)
            throws Exception {

        Path file = Files.createTempFile(
                "battleship-", ".pdf"
        );

        try {
            PdfExporter.export(
                    gameToExport, file.toString()
            );

            assertTrue(Files.size(file) > 0);

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(
                        document.getNumberOfPages() >= 1
                );

                return new PDFTextStripper()
                        .getText(document);
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    // Metodo auxiliar para criar uma jogada
    private IMove createMove(
            List<IPosition> shots,
            List<IGame.ShotResult> results) {

        IMove move = mock(IMove.class);

        when(move.getNumber()).thenReturn(1);
        when(move.getShots()).thenReturn(shots);
        when(move.getShotResults()).thenReturn(results);

        return move;
    }

    // Metodo auxiliar para criar um jogo simulado
    private IGame createGame(List<IMove> moves) {

        IGame gameMock = mock(IGame.class);

        when(gameMock.getAlienMoves())
                .thenReturn(moves);

        return gameMock;
    }

    @Test
    @DisplayName("Nao deve exportar quando o jogo e null")
    void export1() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> PdfExporter.export(
                                null, "teste.pdf"
                        )
                );

        assertEquals(
                "E necessario iniciar um jogo antes de exportar.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Nao deve exportar quando o nome e null")
    void export2() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(game, null)
        );
    }

    @Test
    @DisplayName("Nao deve exportar quando o nome esta vazio")
    void export3() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(game, "")
        );
    }

    @Test
    @DisplayName("Deve criar um ficheiro PDF valido")
    void export4() throws Exception {

        Path file = Files.createTempFile(
                "battleship-", ".pdf"
        );

        try {
            PdfExporter.export(game, file.toString());

            assertTrue(Files.size(file) > 0);

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(
                        document.getNumberOfPages() >= 1
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Nao deve exportar um nome com apenas espacos")
    void export5() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(game, "   ")
        );
    }

    @Test
    @DisplayName("Deve lancar excecao para historico null")
    void export6() throws Exception {

        IGame gameMock = createGame(null);

        Path file = Files.createTempFile(
                "battleship-", ".pdf"
        );

        try {
            IllegalStateException exception =
                    assertThrows(
                            IllegalStateException.class,
                            () -> PdfExporter.export(
                                    gameMock,
                                    file.toString()
                            )
                    );

            assertEquals(
                    "O historico de jogadas nao esta disponivel.",
                    exception.getMessage()
            );

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Deve ignorar jogadas nulas")
    void export7() throws Exception {

        List<IMove> moves =
                Collections.singletonList(null);

        IGame gameMock = createGame(moves);

        String content = exportAndRead(gameMock);

        assertTrue(
                content.contains(
                        "Total de tiros registados: 0"
                )
        );
    }

    @Test
    @DisplayName("Deve exportar uma jogada sem tiros")
    void export8() throws Exception {

        IMove move = createMove(
                List.of(),
                List.of()
        );

        IGame gameMock = createGame(
                List.of(move)
        );

        String content = exportAndRead(gameMock);

        assertTrue(
                content.contains(
                        "Sem tiros registados nesta jogada."
                )
        );
    }

    @Test
    @DisplayName("Deve identificar um tiro na agua")
    void export9() throws Exception {

        IPosition position = mock(IPosition.class);

        IGame.ShotResult result =
                new IGame.ShotResult(
                        true, false, null, false
                );

        IMove move = createMove(
                List.of(position),
                List.of(result)
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Agua")
        );
    }

    @Test
    @DisplayName("Deve identificar um tiro invalido")
    void export10() throws Exception {

        IPosition position = mock(IPosition.class);

        IGame.ShotResult result =
                new IGame.ShotResult(
                        false, false, null, false
                );

        IMove move = createMove(
                List.of(position),
                List.of(result)
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Tiro invalido")
        );
    }

    @Test
    @DisplayName("Deve identificar um tiro repetido")
    void export11() throws Exception {

        IPosition position = mock(IPosition.class);

        IGame.ShotResult result =
                new IGame.ShotResult(
                        true, true, null, false
                );

        IMove move = createMove(
                List.of(position),
                List.of(result)
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Tiro repetido")
        );
    }

    @Test
    @DisplayName("Deve identificar um acerto num navio")
    void export12() throws Exception {

        IPosition position = mock(IPosition.class);
        IShip ship = mock(IShip.class);

        IGame.ShotResult result =
                new IGame.ShotResult(
                        true, false, ship, false
                );

        IMove move = createMove(
                List.of(position),
                List.of(result)
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Acertou num navio")
        );
    }

    @Test
    @DisplayName("Deve identificar um navio afundado")
    void export13() throws Exception {

        IPosition position = mock(IPosition.class);
        IShip ship = mock(IShip.class);

        IGame.ShotResult result =
                new IGame.ShotResult(
                        true, false, ship, true
                );

        IMove move = createMove(
                List.of(position),
                List.of(result)
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Navio afundado")
        );
    }

    @Test
    @DisplayName("Deve identificar uma posicao desconhecida")
    void export14() throws Exception {

        List<IPosition> shots =
                Collections.singletonList(null);

        IMove move = createMove(
                shots,
                List.of()
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Posicao desconhecida")
        );

        assertTrue(
                content.contains("Sem resultado")
        );
    }

    @Test
    @DisplayName("Deve lidar com resultados vazios")
    void export15() throws Exception {

        IPosition position = mock(IPosition.class);

        IMove move = createMove(
                List.of(position),
                List.of()
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Sem resultado")
        );
    }

    @Test
    @DisplayName("Deve lidar com um resultado null")
    void export16() throws Exception {

        IPosition position = mock(IPosition.class);

        List<IGame.ShotResult> results =
                Collections.singletonList(null);

        IMove move = createMove(
                List.of(position),
                results
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Sem resultado")
        );
    }

    @Test
    @DisplayName("Deve criar varias paginas")
    void export17() throws Exception {

        List<IPosition> shots = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            shots.add(
                    new Position(
                            i % 10,
                            (i / 10) % 10
                    )
            );
        }

        IMove move = createMove(
                shots,
                List.of()
        );

        IGame gameMock = createGame(
                List.of(move)
        );

        Path file = Files.createTempFile(
                "battleship-", ".pdf"
        );

        try {
            PdfExporter.export(
                    gameMock, file.toString()
            );

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(
                        document.getNumberOfPages() > 1
                );

                String content =
                        new PDFTextStripper()
                                .getText(document);

                assertTrue(
                        content.contains(
                                "Total de tiros registados: 100"
                        )
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Deve exportar uma lista de tiros null")
    void export18() throws Exception {

        IMove move = createMove(
                null,
                List.of()
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains(
                        "Sem tiros registados nesta jogada."
                )
        );
    }

    @Test
    @DisplayName("Deve lidar com menos resultados do que tiros")
    void export19() throws Exception {

        IPosition position1 = mock(IPosition.class);
        IPosition position2 = mock(IPosition.class);

        IGame.ShotResult result =
                new IGame.ShotResult(
                        true, false, null, false
                );

        IMove move = createMove(
                List.of(position1, position2),
                List.of(result)
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Agua")
        );

        assertTrue(
                content.contains("Sem resultado")
        );

        assertTrue(
                content.contains(
                        "Total de tiros registados: 2"
                )
        );
    }

    @Test
    @DisplayName("Deve exportar um resultado de tiro null")
    void export20() throws Exception {

        IPosition position = mock(IPosition.class);

        List<IGame.ShotResult> results =
                Collections.singletonList(null);

        IMove move = createMove(
                List.of(position),
                results
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Sem resultado")
        );
    }

    @Test
    @DisplayName("Deve exportar uma lista de resultados null")
    void export21() throws Exception {

        IPosition position = mock(IPosition.class);

        IMove move = createMove(
                List.of(position),
                null
        );

        String content = exportAndRead(
                createGame(List.of(move))
        );

        assertTrue(
                content.contains("Sem resultado")
        );
    }
}
