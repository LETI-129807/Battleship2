package battleship;

public class ScoreEntry {
    private String playerName;
    private int totalShots;
    private String date;

    public ScoreEntry(String playerName, int totalShots, String date) {
        this.playerName = playerName;
        this.totalShots = totalShots;
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTotalShots() {
        return totalShots;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("Jogador: %-15s | Tiros Efetuados: %-3d | Data: %s", playerName, totalShots, date);
    }
}