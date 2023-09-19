package org.example;

import lombok.Data;

import java.util.List;

public @Data class Probability {
    private int row;
    private int column;
    private List<SymbolProbability> symbols;
}
