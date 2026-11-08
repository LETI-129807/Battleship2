
package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.List;

public class PdfExporter {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 18;

    private final PDDocument document;
    private PDPageContentStream contentStream;
    private float currentY;

    private PdfExporter(PDDocument document) {
        this.document = document;
    }

    /**
     * Exporta o historico de jogadas e as estatisticas do jogo.
     *
     * @param game jogo a exportar
     * @param fileName caminho do ficheiro PDF
     * @throws IOException se ocorrer um erro ao criar o PDF
     */
    public static void export(IGame game, String fileName)
            throws IOException {

        if (game == null) {
            throw new IllegalArgumentException(
                    "E necessario iniciar um jogo antes de exportar."
            );
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do ficheiro PDF nao pode estar vazio."
            );
        }

        try (PDDocument document = new PDDocument()) {

            PdfExporter exporter = new PdfExporter(document);

            try {
                exporter.createPage();

                exporter.writeLine(
                        "BATTLESHIP - RELATORIO DE JOGADAS", true
                );
                exporter.writeLine("", false);

                List<IMove> moves = game.getAlienMoves();

                if (moves == null) {
                    throw new IllegalStateException(
                            "O historico de jogadas nao esta disponivel."
                    );
                }

                // Estatisticas
                exporter.writeLine("RESUMO DA PARTIDA", true);
                exporter.writeLine(
                        "Total de jogadas: " + moves.size(), false
                );
                exporter.writeLine(
                        "Acertos: " + game.getHits(), false
                );
                exporter.writeLine(
                        "Tiros repetidos: " + game.getRepeatedShots(), false
                );
                exporter.writeLine(
                        "Tiros invalidos: " + game.getInvalidShots(), false
                );
                exporter.writeLine(
                        "Navios afundados: " + game.getSunkShips(), false
                );
                exporter.writeLine(
                        "Navios restantes: " + game.getRemainingShips(), false
                );

                exporter.writeLine("", false);
                exporter.writeLine("HISTORICO DE TIROS", true);
                exporter.writeLine("", false);

                if (moves.isEmpty()) {
                    exporter.writeLine(
                            "Ainda nao existem jogadas registadas.", false
                    );
                }

                int totalShots = 0;

                for (IMove move : moves) {

                    if (move == null) {
                        continue;
                    }

                    exporter.writeLine(
                            "Jogada numero " + move.getNumber(), true
                    );

                    List<IPosition> shots = move.getShots();
                    List<IGame.ShotResult> results =
                            move.getShotResults();

                    if (shots == null || shots.isEmpty()) {
                        exporter.writeLine(
                                "Sem tiros registados nesta jogada.", false
                        );
                        exporter.writeLine("", false);
                        continue;
                    }

                    totalShots += shots.size();

                    for (int i = 0; i < shots.size(); i++) {

                        IPosition position = shots.get(i);

                        String positionText = position == null
                                ? "Posicao desconhecida"
                                : position.toString();

                        String resultText = "Sem resultado";

                        if (results != null
                                && i < results.size()
                                && results.get(i) != null) {

                            resultText = describeResult(results.get(i));
                        }

                        exporter.writeLine(
                                "Tiro " + (i + 1)
                                        + " | " + positionText
                                        + " | " + resultText,
                                false
                        );
                    }

                    exporter.writeLine("", false);
                }

                exporter.writeLine(
                        "Total de tiros registados: " + totalShots, true
                );

            } finally {
                exporter.closePage();
            }

            document.save(fileName);
        }
    }

    /**
     * Identifica o resultado de cada tiro.
     */
    private static String describeResult(
            IGame.ShotResult result) {

        if (!result.valid()) {
            return "Tiro invalido";
        }

        if (result.repeated()) {
            return "Tiro repetido";
        }

        if (result.ship() == null) {
            return "Agua";
        }

        if (result.sunk()) {
            return "Navio afundado";
        }

        return "Acertou num navio";
    }

    /**
     * Cria uma nova pagina A4.
     */
    private void createPage() throws IOException {

        closePage();

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        contentStream = new PDPageContentStream(document, page);

        currentY = page.getMediaBox().getHeight() - MARGIN;
    }

    /**
     * Escreve uma linha no PDF.
     */
    private void writeLine(String text, boolean bold)
            throws IOException {

        if (currentY < MARGIN + LINE_HEIGHT) {
            createPage();
        }

        PDType1Font font = new PDType1Font(
                bold
                        ? Standard14Fonts.FontName.HELVETICA_BOLD
                        : Standard14Fonts.FontName.HELVETICA
        );

        contentStream.beginText();
        contentStream.setFont(font, bold ? 13 : 11);
        contentStream.newLineAtOffset(MARGIN, currentY);
        contentStream.showText(text);
        contentStream.endText();

        currentY -= LINE_HEIGHT;
    }

    /**
     * Fecha a pagina atual.
     */
    private void closePage() throws IOException {

        if (contentStream != null) {
            contentStream.close();
            contentStream = null;
        }
    }
}
