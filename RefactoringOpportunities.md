
# Refactoring Opportunities — Battleship

| Location | Code Smell | Refactoring | Student Number |
|---|---|---|---|
| Game.randomEnemyFire() | Long Method | Extract Method | 129807 |
| Fleet.printAllShips() | Unused Declaration | Safe Delete | 129807 |
| Ship.isInsideBoard() | Complex Conditional | Introduce Variable | 129807 |
| Scoreboard.loadScores() | Unnecessary Local Variable | Inline Variable | 129807 |
| PdfExporter.writeLine() | Magic Numbers | Introduce Parameter | 129807 |
| Tasks::menu() | Long Method / Switch Statements | Extract Method | 111591 |
| Tasks::readClassicPosition() | Complex Conditional | Decompose Conditional | 111591 |
| Scoreboard::displayScoreboard() | Duplicated Code | Extract Method | 111591 |
| ScoreEntry::toString() | Magic Strings | Extract Variable | 111591 |
| Tasks::buildFleet() | Deep Nesting | Guard Clauses (Replace Nested Conditional with Guard Clauses) | 111591 |

| Move::processEnemyFire() | Long Method | Extract Method | 122637 |
| Scoreboard::addScore() | Magic Strings | Introduce Constant | 122637 |
| Move::processEnemyFire() | Duplicated Code | Introduce Variable | 122637 |
| ScoreEntry::playerName | Data Class | Encapsulate Field | 122637 |
| Scoreboard::saveScores() | Magic Strings | Introduce Parameter | 122637 |