
package battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;


import java.util.ArrayList;



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

    @Test
    @DisplayName("Não deve exportar quando o jogo é null")
    void export1() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(null, "teste.pdf"),
                "Deveria lançar IllegalArgumentException quando o jogo é null"
        );

        assertEquals(
                "E necessario iniciar um jogo antes de exportar.",
                exception.getMessage(),
                "A mensagem de erro não corresponde à esperada"
        );
    }

    @Test
    @DisplayName("Não deve exportar quando o nome do ficheiro é null")
    void export2() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(game, null),
                "Deveria lançar IllegalArgumentException quando o nome é null"
        );
    }

    @Test
    @DisplayName("Não deve exportar quando o nome do ficheiro está vazio")
    void export3() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(game, ""),
                "Deveria lançar IllegalArgumentException quando o nome está vazio"
        );
    }

    @Test
    @DisplayName("Deve criar um ficheiro PDF válido")
    void export4() throws Exception {

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(game, file.toString());

            assertTrue(
                    Files.size(file) > 0,
                    "Erro: o ficheiro PDF deveria conter dados"
            );

            try (PDDocument document = Loader.loadPDF(file.toFile())) {
                assertTrue(
                        document.getNumberOfPages() >= 1,
                        "Erro: o PDF deveria ter pelo menos uma página"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Não deve exportar quando o nome contém apenas espaços")
    void export5() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PdfExporter.export(game, "   "),
                "Erro: deveria lançar IllegalArgumentException para um nome em branco"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando o histórico de jogadas é null")
    void export6() throws Exception {

        IGame gameMock = mock(IGame.class);

        when(gameMock.getAlienMoves()).thenReturn(null);

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> PdfExporter.export(gameMock, file.toString()),
                    "Erro: deveria lançar IllegalStateException quando o histórico é null"
            );

            assertEquals(
                    "O historico de jogadas nao esta disponivel.",
                    exception.getMessage(),
                    "Erro: a mensagem da exceção não corresponde à esperada"
            );

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Deve ignorar jogadas nulas no histórico")
    void export7() throws Exception {

        IGame gameMock = mock(IGame.class);

        when(gameMock.getAlienMoves()).thenReturn(
                Arrays.asList((IMove) null)
        );

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(
                    Files.size(file) > 0,
                    "Erro: o PDF deveria ser criado mesmo com uma jogada nula"
            );

            try (PDDocument document = Loader.loadPDF(file.toFile())) {
                assertEquals(
                        1,
                        document.getNumberOfPages(),
                        "Erro: deveria existir uma página no PDF"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Deve exportar uma jogada sem tiros registados")
    void export8() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of());
        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(
                    Files.size(file) > 0,
                    "Erro: o PDF deveria ser criado mesmo sem tiros"
            );

            try (PDDocument document = Loader.loadPDF(file.toFile())) {
                assertEquals(
                        1,
                        document.getNumberOfPages(),
                        "Erro: o PDF deveria conter uma página"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }



    @Test
    @DisplayName("Deve exportar uma jogada com um tiro na água")
    void export9() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, null, false);

        when(moveMock.getShotResults()).thenReturn(List.of(result));
        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(
                    Files.size(file) > 0,
                    "Erro: o PDF deveria conter dados"
            );

            try (PDDocument document = Loader.loadPDF(file.toFile())) {
                assertTrue(
                        document.getNumberOfPages() >= 1,
                        "Erro: o PDF deveria conter pelo menos uma página"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve identificar um tiro inválido no PDF")
    void export10() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));

        IGame.ShotResult result =
                new IGame.ShotResult(false, false, null, false);

        when(moveMock.getShotResults()).thenReturn(List.of(result));
        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Tiro invalido"),
                        "O PDF deveria identificar o tiro inválido"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve identificar um tiro repetido no PDF")
    void export11() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));

        IGame.ShotResult result =
                new IGame.ShotResult(true, true, null, false);

        when(moveMock.getShotResults()).thenReturn(List.of(result));
        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Tiro repetido"),
                        "O PDF deveria identificar o tiro repetido"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve identificar um acerto num navio no PDF")
    void export12() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);
        IShip shipMock = mock(IShip.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, shipMock, false);

        when(moveMock.getShotResults()).thenReturn(List.of(result));
        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Acertou num navio"),
                        "O PDF deveria identificar um acerto num navio"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Deve identificar um navio afundado no PDF")
    void export13() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);
        IShip shipMock = mock(IShip.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, shipMock, true);

        when(moveMock.getShotResults()).thenReturn(List.of(result));
        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Navio afundado"),
                        "O PDF deveria identificar um navio afundado"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    @DisplayName("Deve identificar uma posição desconhecida no PDF")
    void export14() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(
                java.util.Collections.singletonList(null)
        );

        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Posicao desconhecida"),
                        "O PDF deveria identificar uma posição desconhecida"
                );

                assertTrue(
                        content.contains("Sem resultado"),
                        "O PDF deveria indicar a ausência de resultado"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve lidar com uma lista de resultados vazia")
    void export15() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));
        when(moveMock.getShotResults()).thenReturn(List.of());

        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Sem resultado"),
                        "O PDF deveria indicar que o tiro não tem resultado"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve lidar com um resultado de tiro null")
    void export16() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition positionMock = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(List.of(positionMock));

        when(moveMock.getShotResults()).thenReturn(
                java.util.Collections.singletonList(null)
        );

        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Sem resultado"),
                        "O PDF deveria identificar um resultado nulo"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve criar várias páginas quando o PDF fica cheio")
    void export17() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);

        List<IPosition> shots = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            shots.add(new Position(i % 10, (i / 10) % 10));
        }

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(shots);
        when(moveMock.getShotResults()).thenReturn(List.of());

        when(gameMock.getAlienMoves()).thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            try (PDDocument document = Loader.loadPDF(file.toFile())) {

                assertTrue(
                        document.getNumberOfPages() > 1,
                        "O PDF deveria conter várias páginas"
                );

                String content = new PDFTextStripper().getText(document);

                assertTrue(
                        content.contains("Total de tiros registados: 100"),
                        "O PDF deveria registar os 100 tiros"
                );
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }
    @Test
    @DisplayName("Deve exportar uma jogada com lista de tiros null")
    void export18() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);

        when(moveMock.getNumber()).thenReturn(1);
        when(moveMock.getShots()).thenReturn(null);

        when(gameMock.getAlienMoves())
                .thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(Files.size(file) > 0);

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(document.getNumberOfPages() >= 1);
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }
    @Test
    @DisplayName("Deve exportar quando existem menos resultados do que tiros")
    void export19() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);

        IPosition position1 = mock(IPosition.class);
        IPosition position2 = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);

        when(moveMock.getShots())
                .thenReturn(List.of(position1, position2));

        when(moveMock.getShotResults())
                .thenReturn(List.of(
                        new IGame.ShotResult(true, false, null, false)
                ));

        when(gameMock.getAlienMoves())
                .thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(Files.size(file) > 0);

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(document.getNumberOfPages() >= 1);
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }


    @Test
    @DisplayName("Deve exportar quando um resultado de tiro é null")
    void export20() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);

        IPosition position = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);

        when(moveMock.getShots())
                .thenReturn(List.of(position));

        // Lista com um resultado null
        when(moveMock.getShotResults())
                .thenReturn(java.util.Arrays.asList(
                        (IGame.ShotResult) null
                ));

        when(gameMock.getAlienMoves())
                .thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(Files.size(file) > 0);

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(document.getNumberOfPages() >= 1);
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }



    @Test
    @DisplayName("Deve exportar quando a lista de resultados é null")
    void export21() throws Exception {

        IGame gameMock = mock(IGame.class);
        IMove moveMock = mock(IMove.class);
        IPosition position = mock(IPosition.class);

        when(moveMock.getNumber()).thenReturn(1);

        // Existe um tiro
        when(moveMock.getShots())
                .thenReturn(List.of(position));

        // Mas a lista de resultados é null
        when(moveMock.getShotResults())
                .thenReturn(null);

        when(gameMock.getAlienMoves())
                .thenReturn(List.of(moveMock));

        Path file = Files.createTempFile("battleship-", ".pdf");

        try {
            PdfExporter.export(gameMock, file.toString());

            assertTrue(Files.size(file) > 0);

            try (PDDocument document =
                         Loader.loadPDF(file.toFile())) {

                assertTrue(document.getNumberOfPages() >= 1);
            }

        } finally {
            Files.deleteIfExists(file);
        }
    }

}
