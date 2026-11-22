
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
| Tasks::menuHelp() | Magic Strings | Introduce Constant | 129789 |
| Tasks::readShip() | Message Chains (Law of Demeter) | Introduce Variable | 129789 |
| Tasks::readPosition() | Unnecessary Local Variable | Inline Variable | 129789 |
| Game::printBoard() | Long Method | Extract Method | 129789 |
| Game::jsonShots() | Magic Strings | Introduce Field | 129789 |
| Game::fireSingleShot() | Primitive Obsession / Long Parameter List | Introduce Parameter Object | 129789 |
| Game::getRemainingShips() | Redundant Local Variable | Inline Method | 129789 |
