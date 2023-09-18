package org.example;

import lombok.Data;

import java.util.List;

public @Data class Probability {
    private int column;
    private int row;
    private List<SymbolProbability> symbols;
}
