package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Missing parameters! You need '--config' AND '--betting-amount'");
        } else if (!args[0].equals("--config")) {
            System.out.println("The first parameter is NOT correct! It has to be '--config'");
        } else if (!args[2].equals("--betting-amount")) {
            System.out.println("The second parameter is NOT correct! It has to be '--betting-amount'");
        } else {
            String configPath = args[1];

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode jsonNode = objectMapper.readTree(new File("config.json"));

                int columns = jsonNode.get("columns").asInt();
                int rows = jsonNode.get("rows").asInt();
                JsonNode allSymbols = jsonNode.get("symbols");

                List<Symbol> standard = new ArrayList<>();
                List<Symbol> bonus = new ArrayList<>();

                if (allSymbols.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> symbols = allSymbols.fields();
                    while (symbols.hasNext()) {
                        Map.Entry<String, JsonNode> symbol = symbols.next();
                        String objectName = symbol.getKey();
                        JsonNode objectNode = symbol.getValue();

                        // Get values from the nested object
                        String type = objectNode.get("type").asText();
                        int multiplier;
                        JsonNode rewardMultiplier = objectNode.findValue("reward_multiplier");
                        if (rewardMultiplier == null) {
                            multiplier = 1;
                        } else {
                            multiplier = objectNode.get("reward_multiplier").asInt();
                        }

                        if (type.equals("standard")) {
                            Symbol tmp = new Symbol(objectName, type, multiplier);
                            standard.add(tmp);
                            System.out.println(tmp.toString());
                        }

                        if (type.equals("bonus")) {
                            Symbol tmp = new Symbol(objectName, type, multiplier);
                            bonus.add(tmp);
                            System.out.println(tmp.toString());
                        }
                    }
                }

                System.out.println("Creating a " + columns + "x" + rows + " matrix...");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}