package org.example;

import lombok.Getter;

import java.util.*;

public class Game {
    private final @Getter int columns;
    private final @Getter int rows;
    private final @Getter int bettingAmount;
    private final List<Symbol> standardSymbols;
    private final List<Symbol> bonusSymbols;
    private final List<Probability> standardProbabilities;
    private final List<Probability> bonusProbabilities;
    private final List<WinScenario> winScenarios;
    private final @Getter double reward;
    private @Getter String[][] gameMatrix;
    private @Getter Map<String, Integer> symbolOccurrences;
    private @Getter Map<String, String[]> appliedWinScenarios;
    private @Getter String appliedBonusSymbol;

    public Game(int columns, int rows, int bettingAmount, List<Symbol> standardSymbols, List<Symbol> bonusSymbols,
                List<Probability> standardProbabilities, List<Probability> bonusProbabilities, List<WinScenario> winScenarios) {
        this.columns = columns;
        this.rows = rows;
        this.bettingAmount = bettingAmount;

        gameMatrix = new String[rows][columns];

        this.standardSymbols = standardSymbols;
        this.bonusSymbols = bonusSymbols;
        this.standardProbabilities = standardProbabilities;
        this.bonusProbabilities = bonusProbabilities;
        this.winScenarios = winScenarios;

        populateMatrix();

        countSymbols();

        verifyWinScenarios();

        reward = calculateReward(bettingAmount, appliedWinScenarios, appliedBonusSymbol);
    }

    private void populateMatrix() {
        System.out.println("Creating a " + rows + "x" + columns + " matrix...");
        System.out.println();

        Random random = new Random();
        int bonusLocation = random.nextInt(rows * columns) + 1;
        int bonusRow = (bonusLocation - 1) / 3;
        int bonusColumn = (bonusLocation - 1) % 3;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {

                if (row == bonusRow && column == bonusColumn) {
                    String bonusLabel = selectBonusSymbol(bonusProbabilities);
                    Symbol bonusSymbol = new Symbol();

                    for (Symbol symbol : bonusSymbols) {

                        if (symbol.getLabel().equals(bonusLabel)) {
                            bonusSymbol = symbol;
                        }
                    }

                    appliedBonusSymbol = bonusSymbol.getLabel();
                    setSymbol(row, column, bonusSymbol);
                } else {
                    String symbolLabel = selectStandardSymbol(row, column, standardProbabilities);
                    Symbol standardSymbol = new Symbol();

                    for (Symbol symbol : standardSymbols) {

                        if (symbol.getLabel().equals(symbolLabel)) {
                            standardSymbol = symbol;
                        }
                    }

                    setSymbol(row, column, standardSymbol);
                }
            }
        }
    }

    private String selectStandardSymbol(int row, int column, List<Probability> standardProbabilities) {
        int combinedProbabilities = 0;

        for (Probability probability : standardProbabilities) {

            if (probability.getRow() == row && probability.getColumn() == column) {
                List<SymbolProbability> symbols = probability.getSymbols();

                for (SymbolProbability symbol : symbols) {
                    combinedProbabilities += symbol.getProbability();
                }

                Random random = new Random();
                int randomValue = random.nextInt(combinedProbabilities) + 1;

                int currentProbabilitySum = 0;
                for (SymbolProbability symbol : symbols) {
                    currentProbabilitySum += symbol.getProbability();
                    if (randomValue <= currentProbabilitySum) {
                        return symbol.getLabel();
                    }
                }
            }
        }

        return null;
    }

    private String selectBonusSymbol(List<Probability> bonusProbabilities) {
        int combinedProbabilities = 0;

        List<SymbolProbability> symbols = bonusProbabilities.get(0).getSymbols();
        for (SymbolProbability symbol : symbols) {
            combinedProbabilities += symbol.getProbability();
        }

        Random random = new Random();
        int randomValue = random.nextInt(combinedProbabilities) + 1;

        int currentProbabilitySum = 0;
        for (SymbolProbability symbol : symbols) {
            currentProbabilitySum += symbol.getProbability();
            if (randomValue <= currentProbabilitySum) {
                return symbol.getLabel();
            }
        }

        return null;
    }

    public void setSymbol(int row, int column, Symbol symbol) {
        gameMatrix[row][column] = symbol.getLabel();
    }

    private void countSymbols() {
        symbolOccurrences = new HashMap<>();

        int rows = gameMatrix.length;
        int cols = gameMatrix[0].length;

        for (String[] matrix : gameMatrix) {
            for (int j = 0; j < cols; j++) {
                String symbol = matrix[j];
                symbolOccurrences.put(symbol, symbolOccurrences.getOrDefault(symbol, 0) + 1);
            }
        }
    }

    public void verifyWinScenarios() {
        appliedWinScenarios = new HashMap<>();

        for (Map.Entry<String, Integer> entry : symbolOccurrences.entrySet()) {
            String label = entry.getKey();
            Integer occurrences = entry.getValue();

            if (occurrences > 2) {
                /* OCCURRENCES*/
                appliedWinScenarios.put(label, sameSymbolXTimes(occurrences));

                String[] existingArray = appliedWinScenarios.get(label);

                /* HORIZONTAL*/
                if (sameSymbolHorizontally(label)) {
                    String[] horizontalBonus = new String[]{"same_symbols_horizontally"};

                    String[] combinedArray = Arrays.copyOf(existingArray, existingArray.length + horizontalBonus.length);
                    System.arraycopy(horizontalBonus, 0, combinedArray, existingArray.length, horizontalBonus.length);

                    appliedWinScenarios.put(label, combinedArray);
                }

                existingArray = appliedWinScenarios.get(label);

                /* VERTICAL */
                if (sameSymbolVertically(label)) {
                    String[] verticalBonus = new String[]{"same_symbols_vertically"};

                    String[] combinedArray = Arrays.copyOf(existingArray, existingArray.length + verticalBonus.length);
                    System.arraycopy(verticalBonus, 0, combinedArray, existingArray.length, verticalBonus.length);

                    appliedWinScenarios.put(label, combinedArray);
                }

                existingArray = appliedWinScenarios.get(label);

                /* LTR DIAGONAL */
                if (ltrDiagonal(label)) {
                    String[] ltrDiagonalBonus = new String[]{"same_symbols_diagonally_left_to_right"};

                    String[] combinedArray = Arrays.copyOf(existingArray, existingArray.length + ltrDiagonalBonus.length);
                    System.arraycopy(ltrDiagonalBonus, 0, combinedArray, existingArray.length, ltrDiagonalBonus.length);

                    appliedWinScenarios.put(label, combinedArray);
                }

                existingArray = appliedWinScenarios.get(label);

                /* RTL DIAGONAL */
                if (rtlDiagonal(label)) {
                    String[] rtlDiagonalBonus = new String[]{"same_symbols_diagonally_right_to_left"};

                    String[] combinedArray = Arrays.copyOf(existingArray, existingArray.length + rtlDiagonalBonus.length);
                    System.arraycopy(rtlDiagonalBonus, 0, combinedArray, existingArray.length, rtlDiagonalBonus.length);

                    appliedWinScenarios.put(label, combinedArray);
                }
            }
        }
    }

    private String[] sameSymbolXTimes(Integer occurrences) {
        String[] result = new String[occurrences - 2];

        for (int i = occurrences, j = 0; i >= 3; i--, j++) {
            String label = "same_symbol_" + i + "_times";

            for (WinScenario winScenario : winScenarios) {
                if (winScenario.getLabel().equals(label)) {
                    result[j] = winScenario.getLabel();
                }
            }
        }

        return result;
    }

    private boolean sameSymbolHorizontally(String label) {
        for (String[] row : gameMatrix) {
            boolean sameSymbol = true;

            for (String cell : row) {

                if (!cell.equals(label)) {
                    sameSymbol = false;
                    break;
                }
            }

            if (sameSymbol) {
                return true;
            }
        }

        return false;
    }

    private boolean sameSymbolVertically(String label) {
        for (int col = 0; col < gameMatrix[0].length; col++) {
            boolean sameSymbol = true;

            for (String[] matrix : gameMatrix) {

                if (!matrix[col].equals(label)) {
                    sameSymbol = false;
                    break;
                }
            }

            if (sameSymbol) {
                return true;
            }
        }

        return false;
    }

    private boolean ltrDiagonal(String label) {
        if (rows != columns) {
            return false;
        } else {
            for (int i = 0; i < gameMatrix[0].length; i++) {
                if (!gameMatrix[i][i].equals(label)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean rtlDiagonal(String label) {
        if (rows != columns) {
            return false;
        } else {
            for (int i = 0; i < gameMatrix.length; i++) {
                if (!gameMatrix[i][gameMatrix.length - 1 - i].equals(label)) {
                    return false;
                }
            }
        }

        return true;
    }

    private double calculateReward(int bettingAmount, Map<String, String[]> appliedWinScenarios, String appliedBonusSymbol) {
        double reward = 0.0;

        if (appliedWinScenarios.isEmpty()) {
            return reward;
        }

        for (String label : appliedWinScenarios.keySet()) {
            Symbol symbol = new Symbol();

            for (Symbol s : standardSymbols) {
                if (s.getLabel().equals(label)) {
                    symbol = s;
                    break;
                }
            }

            reward += bettingAmount * symbol.getMultiplier();

            String[] winScenarioLabels = appliedWinScenarios.get(label);
            for (String winS : winScenarioLabels) {
                WinScenario scenario;

                for (WinScenario winScenario : winScenarios) {

                    if (winScenario.getLabel().equals(winS)) {
                        scenario = winScenario;
                        reward *= scenario.getMultiplier();
                        break;
                    }
                }
            }
        }

        Symbol bonus;

        for (Symbol bonusS : bonusSymbols) {

            if (bonusS.getLabel().equals(appliedBonusSymbol)) {
                bonus = bonusS;

                if (bonus.getImpact().equals("multiply_reward")) {
                    reward *= bonus.getMultiplier();
                    break;
                }

                if (bonus.getImpact().equals("extra_bonus")) {
                    reward += bonus.getMultiplier();
                    break;
                }
            }
        }

        return reward;
    }
}
