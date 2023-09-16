package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
//        if (args.length != 4) {
//            System.out.println("Missing parameters! You need '--config' AND '--betting-amount'");
//        } else if (!args[0].equals("--config")) {
//            System.out.println("The first parameter is NOT correct! It has to be '--config'");
//        } else if (!args[2].equals("--betting-amount")) {
//            System.out.println("The second parameter is NOT correct! It has to be '--betting-amount'");
//        } else {
//        String configPath = args[1];
        String configPath = "config.json";

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject config = (JSONObject) jsonParser.parse(new FileReader(configPath));
            Long columns = (Long) config.get("columns");
            Long rows = (Long) config.get("rows");

            System.out.println("Hello, world!");
            System.out.println(columns);
            System.out.println(rows);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
//    }
}