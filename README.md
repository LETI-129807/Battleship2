# ⚓ Battleship 2.0

![Java](https://img.shields.io/badge/Java-21-blue)
![Maven](https://img.shields.io/badge/Build-Maven-orange)

A Java-based Battleship simulation project developed as part of the Software Engineering coursework at ISCTE-IUL.

---

## 📖 Table of Contents

- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [PDF Export](#-pdf-export)
- [Technical Stack](#-technical-stack)
- [Installation & Setup](#-installation--setup)
- [Documentation](#-documentation)
- [Testing](#-testing)
- [Contributing](#-contributing)

---

## 🎯 Project Overview

Battleship 2.0 is a Java application that simulates naval battles on a 10×10 board.

Players can generate a fleet, fire shots at selected positions and inspect the results of each move.

The simulation ends when all ships have been sunk.

The project uses Maven for dependency management and GitHub for collaborative development.

---

## ✨ Key Features

| Feature | Description | Status |
|---|---|---|
| Fleet generation | Randomly generates a fleet of ships | ✅ Implemented |
| Custom fleet | Allows users to configure a fleet | ✅ Implemented |
| Shot simulation | Processes shots and displays their results | ✅ Implemented |
| Game statistics | Displays hits, misses and fleet status | ✅ Implemented |
| PDF export | Exports the game history to a PDF report | 🔄 Pull Request #2 |
| AI opponent | Planned for a future sprint | 🚧 Planned |

---

## 📄 PDF Export

### Description

A new feature was developed to export the history of a Battleship game to a PDF document.

This feature uses **Apache PDFBox 3.0.5**, an external Java library available through Maven Central.

The implementation is associated with:

- [Issue #1 — Exportar histórico de jogadas para PDF](https://github.com/LETI-129807/Battleship2/issues/1)
- [Pull Request #2 — Implement PDF game history export](https://github.com/LETI-129807/Battleship2/pull/2)

### How to use

Run the application and enter the following commands:

```text
gerafrota
rajada A1 B3 C5
rajada D2 E4 F6
exportpdf
desisto
```

The `exportpdf` command generates a PDF file in the application's working directory.

The report includes the recorded moves, shot coordinates, shot results and game statistics.

### PDF report information

The generated report contains:

- Total number of moves.
- Shot positions and results.
- Successful hits.
- Repeated and invalid shots.
- Sunk and remaining ships.
- Total recorded shots.

The generated filename includes a timestamp, allowing multiple reports to be saved.

### Implementation

The feature is implemented primarily in:

- `PdfExporter.java` — Generates the PDF report using Apache PDFBox.
- `Tasks.java` — Provides the `exportpdf` command.
- `pom.xml` — Declares the Apache PDFBox dependency.

---

## 🛠 Technical Stack

- **Language:** Java 21
- **Build Tool:** Apache Maven
- **Testing:** JUnit
- **Logging:** Log4j2
- **PDF Generation:** Apache PDFBox 3.0.5
- **Version Control:** Git and GitHub
- **IDE:** IntelliJ IDEA

---

## 🚀 Installation & Setup

### Prerequisites

- Java Development Kit (JDK) 21 or later
- Apache Maven
- Git
- IntelliJ IDEA (recommended)

### Clone the repository

```bash
git clone https://github.com/LETI-129807/Battleship2.git
```

### Navigate to the project

```bash
cd Battleship2
```

### Compile the project

```bash
mvn clean compile
```

### Run the application

Open the project in IntelliJ IDEA and execute the `main` method in `Main.java`.

### Available commands

| Command | Description |
|---|---|
| `ajuda` | Displays the available commands |
| `gerafrota` | Generates a random fleet |
| `lefrota` | Creates a custom fleet |
| `rajada` | Fires a sequence of shots |
| `mapa` | Displays the fleet board |
| `estado` | Displays fleet status |
| `tiros` | Displays the shot board |
| `simula` | Simulates a complete game |
| `exportpdf` | Exports the game history to PDF |
| `desisto` | Exits the application |

**Note:** The `exportpdf` command is available in the `feature/129807-export-pdf` branch until its Pull Request is merged into `main`.

---

## 📚 Documentation

The project's generated Javadoc is available through GitHub Pages:

👉 [Battleship2 API Documentation](https://leti-129807.github.io/Battleship2/)

To generate Javadoc locally:

```bash
mvn javadoc:javadoc
```

---

## 🧪 Testing

The project uses Maven to compile and run automated tests.

Run:

```bash
mvn test
```

The PDF export functionality has also been tested manually by generating fleets, executing multiple moves and opening the resulting PDF documents.

The Maven CI workflows associated with Pull Request #2 have completed successfully.

---

## 🤖 AI-Assisted Development

ChatGPT was used as a development support tool during the implementation of the PDF export feature.

Its use included:

- Understanding Maven dependencies and Apache PDFBox.
- Supporting the implementation of PDF generation.
- Explaining Java code and project structure.
- Helping identify errors in GitHub Actions workflows.
- Supporting testing and documentation.

The generated suggestions were checked through compilation, program execution and manual inspection of the exported PDF reports.

---

## 🗺 Roadmap

- [x] Fleet generation and shot simulation
- [x] Maven project configuration
- [x] GitHub repository setup
- [x] GitHub Pages documentation
- [x] PDF export implementation in feature branch
- [ ] Peer review and merge of PDF export
- [ ] Integration of other group members' features
- [ ] AI opponent in the next sprint
- [ ] Final demonstration video

---

## 🤝 Contributing

Each group member implements a different feature using an external Java library from Maven Central.

The development workflow consists of creating a GitHub Issue, implementing the feature in a dedicated branch, opening a Pull Request and requesting a review from another group member.

Feature branches must include the student's identification number.

---

## 🎥 Demonstration Video

[video](https://youtu.be/_ATpNrVDUMQ?si=DKjEVv5iCcnSjwPZ)

---

**Project:** Battleship2  
**Institution:** ISCTE-IUL  
**Repository:** https://github.com/LETI-129807/Battleship2
