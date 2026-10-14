# Treino do Gemini — Battleship2

**Aluno:** LETI-129807  
**LLM:** Google Gemini  
**Objetivo:** Testar a comunicação JSON e a estratégia de jogo da IA.

## Resultados dos testes

| Jogada | Coordenadas | Resultado |
|---|---|---|
| 1 | A1 C3 E5 | 3 tiros na água |
| 2 | B2 E5 I3 | 1 Barca afundada, 1 repetido, 1 água |
| 3 | D5 F5 I5 | 1 Barca afundada, 2 água |
| 4 | A7 E9 H2 | 1 acerto numa Caravela, 2 água |
| 5 | H1 H3 G2 | 3 tiros na água |
| 6 | I2 A2 D8 | 1 Caravela afundada, 1 acerto no Galeão, 1 água |

## Problemas encontrados

### Formato JSON

Inicialmente, o Gemini enviou o campo `column` como texto, em vez de número. Foi corrigido através de prompting.

### Repetição de coordenadas

Na segunda jogada, o Gemini repetiu E5, já utilizada anteriormente. Foi reforçada a utilização de um Diário de Bordo para memorizar as jogadas.

## Melhoria da estratégia

Após atingir uma Caravela em H2, o Gemini investigou as posições adjacentes H1, H3 e G2.

Como estas posições eram água, escolheu I2 na jogada seguinte, conseguindo afundar a Caravela.

Na mesma rajada, atingiu o Galeão em D8.

## Limitações

O JSON devolvido pelo Battleship2 contém resultados agregados por rajada. Em alguns testes, foram fornecidos resultados individuais por coordenada, obtidos a partir do tabuleiro, para avaliar a estratégia da IA.

## Conclusão

O Gemini demonstrou capacidade para interpretar o protocolo JSON e adaptar as suas decisões com base nos resultados das jogadas. Foram identificados e corrigidos erros através de prompts adicionais.
