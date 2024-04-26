package com.github.lukaslt1993.service;

import com.github.lukaslt1993.config.ScratchGameConfig;
import com.github.lukaslt1993.config.ScratchGameConfig.*;

import java.util.*;

public class ScratchGame {

    private final ScratchGameConfig config;
    private final GameInput input;
    private final Random random;

    public ScratchGame(ScratchGameConfig config, GameInput input) {
        this.config = config;
        this.input = input;
        this.random = new Random();
    }

    public GameOutput playGame() {
        List<List<String>> matrix = generateMatrix();
        double totalReward = 0;
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        String appliedBonusSymbol = null;

        for (Map.Entry<String, Symbol> entry : config.getSymbols().entrySet()) {
            String symbol = entry.getKey();
            Symbol symbolConfig = entry.getValue();

            List<String> winningCombinations = checkWinningCombinations(matrix, symbol, symbolConfig.getType());
            if (!winningCombinations.isEmpty()) {
                appliedWinningCombinations.put(symbol, winningCombinations);
                double symbolReward = calculateSymbolReward(symbolConfig, winningCombinations.size());
                totalReward += symbolReward;
            }
        }

        String bonusSymbol = generateRandomBonusSymbol();
        Symbol bonusSymbolConfig = config.getSymbols().get(bonusSymbol);

        if (bonusSymbolConfig != null) {
            appliedBonusSymbol = bonusSymbol;
            totalReward = applyBonus(totalReward, bonusSymbolConfig);
        }

        GameOutput gameOutput = new GameOutput();
        gameOutput.setMatrix(matrix);
        gameOutput.setReward(totalReward);
        gameOutput.setAppliedWinningCombinations(appliedWinningCombinations);
        gameOutput.setAppliedBonusSymbol(appliedBonusSymbol);

        return gameOutput;
    }

    private List<List<String>> generateMatrix() {
        List<List<String>> matrix = new ArrayList<>();
        int rows = config.getRows();
        int columns = config.getColumns();

        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                String symbol = getRandomSymbol(config.getProbabilities().getStandardSymbols());
                row.add(symbol);
            }
            matrix.add(row);
        }

        return matrix;
    }

    private String getRandomSymbol(List<Probability> symbolProbabilities) {
        double totalProbability = symbolProbabilities.stream()
            .mapToDouble(Probability::getSymbolsProbabilitySum)
            .sum();
        double randomValue = random.nextDouble() * totalProbability;

        double currentProbabilitySum = 0;
        for (Probability probability : symbolProbabilities) {
            currentProbabilitySum += probability.getSymbolsProbabilitySum();
            if (randomValue <= currentProbabilitySum) {
                return getRandomSymbolFromMap(probability.getSymbols());
            }
        }

        return symbolProbabilities.get(0).getSymbols().keySet().iterator().next();
    }

    private String getRandomSymbolFromMap(Map<String, Integer> symbolMap) {
        int randomValue = random.nextInt(symbolMap.values().stream().mapToInt(Integer::intValue).sum()) + 1;
        int currentProbabilitySum = 0;

        for (Map.Entry<String, Integer> entry : symbolMap.entrySet()) {
            currentProbabilitySum += entry.getValue();
            if (randomValue <= currentProbabilitySum) {
                return entry.getKey();
            }
        }

        return symbolMap.keySet().iterator().next();
    }

    private List<String> checkWinningCombinations(List<List<String>> matrix, String symbol, SymbolType symbolType) {
        List<String> appliedWinningCombinations = new ArrayList<>();

        for (Map.Entry<String, WinCombination> entry : config.getWinCombinations().entrySet()) {
            String winCombinationName = entry.getKey();
            WinCombination winCombination = entry.getValue();

            if (winCombinationName.startsWith("same_symbol") && winCombinationName.endsWith(symbol)) {
                int count = winCombination.getCount();
                if (symbolType == SymbolType.STANDARD) {
                    List<String> matchingRows = findMatchingRows(matrix, symbol, count);
                    if (!matchingRows.isEmpty()) {
                        appliedWinningCombinations.add(winCombinationName);
                    }
                }
            } else if (winCombinationName.startsWith("same_symbols_")) {
                List<String> matchingAreas = findMatchingAreas(matrix, winCombination.getCoveredAreas(), symbol);
                if (!matchingAreas.isEmpty()) {
                    appliedWinningCombinations.add(winCombinationName);
                }
            }
        }

        return appliedWinningCombinations;
    }

    private List<String> findMatchingRows(List<List<String>> matrix, String symbol, int count) {
        List<String> matchingRows = new ArrayList<>();
        for (List<String> row : matrix) {
            long symbolCount = row.stream().filter(s -> s.equals(symbol)).count();
            if (symbolCount >= count) {
                matchingRows.add("row_" + matrix.indexOf(row));
            }
        }
        return matchingRows;
    }

    private List<String> findMatchingAreas(List<List<String>> matrix, List<List<String>> coveredAreas, String symbol) {
        List<String> matchingAreas = new ArrayList<>();
        for (List<String> coveredArea : coveredAreas) {
            boolean isMatching = coveredArea.stream().allMatch(pos -> {
                String[] coordinates = pos.split(":");
                int row = Integer.parseInt(coordinates[0]);
                int column = Integer.parseInt(coordinates[1]);
                return matrix.get(row).get(column).equals(symbol);
            });
            if (isMatching) {
                matchingAreas.add(coveredAreas.indexOf(coveredArea) + "_area");
            }
        }
        return matchingAreas;
    }

    private double calculateSymbolReward(Symbol symbolConfig, int winCount) {
        double rewardMultiplier = symbolConfig.getRewardMultiplier();
        return input.getBetAmount() * rewardMultiplier * winCount;
    }

    private String generateRandomBonusSymbol() {
        List<String> bonusSymbols = new ArrayList<>(config.getProbabilities().getBonusSymbols().getSymbols().keySet());
        return bonusSymbols.get(random.nextInt(bonusSymbols.size()));
    }

    private double applyBonus(double totalReward, Symbol bonusSymbolConfig) {
        BonusImpact bonusImpact = bonusSymbolConfig.getImpact();
        switch (bonusImpact) {
            case MULTIPLY_REWARD:
                return totalReward * bonusSymbolConfig.getRewardMultiplier();
            case EXTRA_BONUS:
                return totalReward + bonusSymbolConfig.getExtra();
            default:
                return totalReward;
        }
    }
}
