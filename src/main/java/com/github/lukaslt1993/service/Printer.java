package com.github.lukaslt1993.service;

import java.util.List;
import java.util.Map;

public class Printer {

    public void printMatrix(List<List<String>> matrix) {
        for (List<String> row : matrix) {
            System.out.println(row);
        }
    }

    public void printWinningCombinations(Map<String, List<String>> appliedWinningCombinations) {
        for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
