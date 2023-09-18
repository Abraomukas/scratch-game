package org.example;

import lombok.Data;

public @Data class WinScenario {
    private String label;
    private int multiplier;
    private int counter;
    private String[] coveredArea;
}
