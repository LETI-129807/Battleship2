
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
| Tasks::menuHelp() | Magic Strings | Extract Constant | 129789 |
| Tasks::readShip() | Message Chains (Law of Demeter) | Introduce Variable | 129789 |
| Tasks::readPosition() | Unnecessary Local Variable | Inline Variable | 129789 |
| Game::printBoard() | Long Method | Extract Method | 129789 |
| Game::readEnemyFire() | Complex Conditional | Decompose Conditional | 129789 |
| Game::jsonShots() | Magic Strings | Replace Magic Number with Symbolic Constant | 129789 |
| Game::fireSingleShot() | Complex Conditional | Introduce Explaining Variable | 129789 |