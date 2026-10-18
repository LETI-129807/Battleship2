		System.out.println("- " + RAJADA + ": Realiza uma rajada de 3 tiros (ex.: rajada A1 B3 C5).");
		System.out.println("- " + SIMULA + ": Simula um jogo completo.");
		System.out.println("- " + TIROS + ": Mostra os tiros realizados no tabuleiro.");
		System.out.println("- " + SCOREBOARD + ": Exibe a tabela de pontuacoes dos jogos passados.");
		System.out.println("- " + EXPORTPDF + ": Exporta o historico de jogadas para PDF.");
		System.out.println("- " + DESISTIR + ": Encerra o jogo.");
		System.out.println("===============================================================");
	}

	/**
	 * Constroi uma frota a partir dos dados introduzidos.
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
	 * Le uma posicao com coordenadas numericas.
	 */
	public static Position readPosition(Scanner in) {

		assert in != null;

		int row = in.nextInt();
		int column = in.nextInt();

		return new Position(row, column);
	}

	/**
	 * Le uma posicao no formato A3 ou A 3.
	 */
	public static IPosition readClassicPosition(@NotNull Scanner in) {

		if (!in.hasNext()) {
			throw new IllegalArgumentException(
					"Nenhuma posicao valida encontrada!"
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
					"Formato invalido. Use 'A3', 'A 3' ou similar."
			);
		}
	}