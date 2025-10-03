package com.example.SpotifyWebAPI.Tools.Files.Parsers;

import com.example.SpotifyWebAPI.Tools.Logger.Logger;

public enum JSONSymbols {
    OPEN_BRACKETS('{'),
    CLOSE_BRACKETS('}'),
    OPEN_ARRAY('['),
    CLOSE_ARRAY(']'),
    SEPARATOR(','),
    SPLIT(':');

    private final char symbol;

    JSONSymbols(char symbol) {
        this.symbol = symbol;
    }

    public char toChar() {
        return this.symbol;
    }

    public boolean equals(char symbol) {
        if (this.symbol == symbol)
            Logger.DEBUG.Log(this.symbol + " vs " + symbol + " = " + true);
        return this.symbol == symbol;
    }
}