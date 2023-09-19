package org.example;

import java.util.List;
import java.util.Random;

public class Matrix {
    private final int columns;
    private final int rows;
    private final Symbol[][] gameMatrix;
    private final List<Symbol> standardSymbols;
    private final List<Symbol> bonusSymbols;
    private final List<Probability> standardProbabilities;
    private final List<Probability> bonusProbabilities;

    public Matrix(int columns, int rows, List<Symbol> standardSymbols, List<Symbol> bonusSymbols,
                  List<Probability> standardProbabilities, List<Probability> bonusProbabilities) {
        this.columns = columns;
        this.rows = rows;

        gameMatrix = new Symbol[rows][columns];

        this.standardSymbols = standardSymbols;
        this.bonusSymbols = bonusSymbols;
        this.standardProbabilities = standardProbabilities;
        this.bonusProbabilities = bonusProbabilities;

        populateMatrix();
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
                    Symbol bonusSymbol = null;

                    for (Symbol symbol : bonusSymbols) {

                        if (symbol.getLabel().equals(bonusLabel)) {
                            bonusSymbol = symbol;
                        }
                    }

                    this.setSymbol(row, column, bonusSymbol);
                } else {
                    String symbolLabel = selectStandardSymbol(row, column, standardProbabilities);
                    Symbol standardSymbol = null;

                    for (Symbol symbol : standardSymbols) {

                        if (symbol.getLabel().equals(symbolLabel)) {
                            standardSymbol = symbol;
                        }
                    }

                    this.setSymbol(row, column, standardSymbol);
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

    public Symbol getSymbol(int row, int column) {
        return gameMatrix[row][column];
    }

    public void setSymbol(int row, int column, Symbol symbol) {
        gameMatrix[row][column] = symbol;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                result.append("| ").append(this.getSymbol(row, column).getLabel()).append(" |");
            }

            result.append("\n");
        }

        return result.toString();
    }
}
