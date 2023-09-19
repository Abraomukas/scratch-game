package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Missing parameters! You need '--config' AND '--betting-amount'");
        } else if (!args[0].equals("--config")) {
            System.out.println("The first parameter is NOT correct! It has to be '--config'");
        } else if (!args[2].equals("--betting-amount")) {
            System.out.println("The second parameter is NOT correct! It has to be '--betting-amount'");
        } else {
            Scanner scanner = new Scanner(System.in);
            int bettingAmount = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.println("Please enter a betting amount: ");

                    if (scanner.hasNextInt()) {
                        bettingAmount = scanner.nextInt();
                        validInput = true;
                    } else {
                        scanner.nextLine();
                        System.out.println("Incorrect input!");
                        System.out.println("Please enter a betting amount: ");
                    }
                } catch (InputMismatchException exception) {
                    scanner.nextLine();
                    System.out.println("Incorrect input!");
                    System.out.println("Please enter a betting amount: ");
                }
            }

            scanner.close();

            ObjectMapper objectMapper = new ObjectMapper();
            String configPath = args[1];

            try {
                JsonNode jsonNode = objectMapper.readTree(new File(configPath));

                int rows = jsonNode.get("rows").asInt();
                int columns = jsonNode.get("columns").asInt();

                System.out.println("Loading configuration...");

                /* SYMBOLS */
                System.out.println("Loading symbols...");

                List<Symbol> standardSymbols = new ArrayList<>();
                List<Symbol> bonusSymbols = new ArrayList<>();

                JsonNode allSymbols = jsonNode.get("symbols");

                if (allSymbols.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> symbols = allSymbols.fields();

                    while (symbols.hasNext()) {
                        Map.Entry<String, JsonNode> symbol = symbols.next();
                        String label = symbol.getKey();
                        JsonNode objectNode = symbol.getValue();

                        String type = objectNode.get("type").asText();
                        int multiplier;
                        JsonNode rewardMultiplier = objectNode.findValue("reward_multiplier");

                        if (rewardMultiplier == null) {
                            multiplier = 1;
                        } else {
                            multiplier = objectNode.get("reward_multiplier").asInt();
                        }

                        Symbol tmpSymbol = new Symbol();
                        tmpSymbol.setLabel(label);
                        tmpSymbol.setType(type);
                        tmpSymbol.setMultiplier(multiplier);

                        if (type.equals("standard")) {
                            standardSymbols.add(tmpSymbol);
                        }

                        if (type.equals("bonus")) {
                            bonusSymbols.add(tmpSymbol);
                        }
                    }
                }

                /* PROBABILITIES */
                System.out.println("Loading probabilities...");

                List<Probability> standardProbabilities = new ArrayList<>();
                List<Probability> bonusProbabilities = new ArrayList<>();

                JsonNode allProbabilities = jsonNode.get("probabilities");

                if (allProbabilities.isObject()) {
                    JsonNode standardSymbolProbabilities = allProbabilities.get("standard_symbols");

                    if (standardSymbolProbabilities.isArray()) {
                        for (JsonNode standardSymbolProbability : standardSymbolProbabilities) {
                            int column = standardSymbolProbability.get("column").asInt();
                            int row = standardSymbolProbability.get("row").asInt();
                            JsonNode symbolObject = standardSymbolProbability.get("symbols");

                            Iterator<Map.Entry<String, JsonNode>> symbolFields = symbolObject.fields();
                            List<SymbolProbability> standardSymbolsProbabilities = new ArrayList<>();

                            while (symbolFields.hasNext()) {
                                Map.Entry<String, JsonNode> symbolField = symbolFields.next();
                                String label = symbolField.getKey();
                                int probability = symbolField.getValue().asInt();

                                SymbolProbability symbolProbability = new SymbolProbability();
                                symbolProbability.setLabel(label);
                                symbolProbability.setProbability(probability);

                                standardSymbolsProbabilities.add(symbolProbability);
                            }

                            Probability tmpProbability = new Probability();
                            tmpProbability.setColumn(column);
                            tmpProbability.setRow(row);
                            tmpProbability.setSymbols(standardSymbolsProbabilities);

                            standardProbabilities.add(tmpProbability);
                        }
                    }

                    JsonNode bonusSymbolProbabilities = allProbabilities.get("bonus_symbols");

                    if (bonusSymbolProbabilities.isObject()) {
                        JsonNode symbols = bonusSymbolProbabilities.get("symbols");

                        if (symbols.isObject()) {
                            Iterator<Map.Entry<String, JsonNode>> symbolFields = symbols.fields();
                            List<SymbolProbability> bonusSymbolsProbabilities = new ArrayList<>();

                            while (symbolFields.hasNext()) {
                                Map.Entry<String, JsonNode> bonusField = symbolFields.next();
                                String label = bonusField.getKey();
                                int probability = bonusField.getValue().asInt();

                                SymbolProbability symbolProbability = new SymbolProbability();
                                symbolProbability.setLabel(label);
                                symbolProbability.setProbability(probability);

                                bonusSymbolsProbabilities.add(symbolProbability);
                            }

                            Probability tmpProbability = new Probability();
                            tmpProbability.setColumn(-1);
                            tmpProbability.setRow(-1);
                            tmpProbability.setSymbols(bonusSymbolsProbabilities);

                            bonusProbabilities.add(tmpProbability);
                        }
                    }
                }

                /* WINNING COMBINATIONS */
                System.out.println("Loading winning combinations...");

                List<WinScenario> winScenarios = new ArrayList<>();

                JsonNode allWinCombinations = jsonNode.get("win_combination");

                if (allWinCombinations.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> winCombinations = allWinCombinations.fields();

                    while (winCombinations.hasNext()) {
                        Map.Entry<String, JsonNode> winCombination = winCombinations.next();
                        String label = winCombination.getKey();
                        JsonNode details = winCombination.getValue();

                        int multiplier = details.get("reward_multiplier").asInt();
                        int counter;
                        JsonNode count = details.findValue("count");

                        if (count == null) {
                            counter = 0;
                        } else {
                            counter = details.get("count").asInt();
                        }

                        String[] coveredArea = new String[0];
                        JsonNode areas = details.findValue("covered_areas");

                        if (areas != null && areas.isArray()) {
                            ArrayNode arrayNode = (ArrayNode) areas;
                            List<String> extractedAreas = new ArrayList<>();

                            for (JsonNode subArrayNode : arrayNode) {
                                if (subArrayNode.isArray()) {
                                    ArrayNode subArray = (ArrayNode) subArrayNode;

                                    for (JsonNode elementNode : subArray) {
                                        extractedAreas.add(elementNode.asText());
                                    }
                                }
                            }

                            coveredArea = extractedAreas.toArray(new String[0]);
                        }

                        WinScenario tmpScenario = new WinScenario();
                        tmpScenario.setLabel(label);
                        tmpScenario.setMultiplier(multiplier);
                        tmpScenario.setCounter(counter);
                        tmpScenario.setCoveredArea(coveredArea);

                        winScenarios.add(tmpScenario);
                    }
                }

                System.out.println("Configuration loaded!");

//                System.out.println("STANDARD SYMBOLS - " + standardSymbols.size());
//                for (Symbol standardS : standardSymbols) {
//                    System.out.println(standardS.toString());
//                }

//                System.out.println("BONUS SYMBOLS - " + bonusSymbols.size());
//                for (Symbol bonusS : bonusSymbols) {
//                    System.out.println(bonusS.toString());
//                }

                System.out.println("STANDARD PROBABILITIES - " + standardProbabilities.size());
                for (Probability standardP : standardProbabilities) {
                    System.out.println(standardP.toString());
                }

//                System.out.println("BONUS PROBABILITIES - " + bonusProbabilities.size());
//                for (Probability bonusP : bonusProbabilities) {
//                    System.out.println(bonusP.toString());
//                }

//                System.out.println("WIN SCENARIOS  - " + winScenarios.size());
//                for (WinScenario winS : winScenarios) {
//                    System.out.println(winS.toString());
//                }

                System.out.println("Creating a " + rows + "x" + columns + " matrix...");

                Matrix gameMatrix = new Matrix(rows, columns, standardSymbols, bonusSymbols,
                        standardProbabilities, bonusProbabilities);

                System.out.println("BETTING AMOUNT - " + bettingAmount);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}