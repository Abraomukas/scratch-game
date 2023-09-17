package org.example;

import lombok.Data;

public @Data class Symbol {
    private String label;
    private int multiplier;
    private String type;

    public Symbol(String objectName, String type, int multiplier) {
        this.label = objectName;
        this.type = type;
        this.multiplier = multiplier;
    }
}
