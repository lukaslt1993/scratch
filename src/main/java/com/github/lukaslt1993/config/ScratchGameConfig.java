package com.github.lukaslt1993.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScratchGameConfig {
    private Integer columns;
    private Integer rows;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;
    @JsonProperty("win_combinations")
    private Map<String, WinCombination> winCombinations;

    @Data
    public static class Symbol {
        private Double rewardMultiplier;
        private SymbolType type;
        private Double extra;
        private BonusImpact impact;
    }

    @Data
    public static class Probabilities {
        @JsonProperty("standard_symbols")
        private List<Probability> standardSymbols;
        @JsonProperty("bonus_symbols")
        private BonusProbabilities bonusSymbols;
    }

    @Data
    public static class Probability {
        private Integer column;
        private Integer row;
        private Map<String, Integer> symbols;

        public double getSymbolsProbabilitySum() {
            return symbols.values().stream().mapToDouble(Integer::doubleValue).sum();
        }
    }

    @Data
    public static class BonusProbabilities {
        private Map<String, Integer> symbols;
    }

    @Data
    public static class WinCombination {
        @JsonProperty("reward_multiplier")
        private Double rewardMultiplier;
        private String when;
        private Integer count;
        private String group;
        @JsonProperty("covered_areas")
        private List<List<String>> coveredAreas;
    }

    @Data
    public static class GameInput {
        private Double betAmount;
    }

    @Data
    public static class GameOutput {
        private List<List<String>> matrix;
        private Double reward;
        private Map<String, List<String>> appliedWinningCombinations;
        private String appliedBonusSymbol;
    }

    public enum SymbolType {
        STANDARD("standard"),
        BONUS("bonus");

        private final String value;

        SymbolType(String value) {
            this.value = value;
        }

        @JsonCreator
        public static SymbolType fromValue(String value) {
            for (SymbolType type : SymbolType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid SymbolType value: " + value);
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public enum BonusImpact {
        MULTIPLY_REWARD("multiply_reward"),
        EXTRA_BONUS("extra_bonus"),
        MISS("miss");

        private final String value;

        BonusImpact(String value) {
            this.value = value;
        }

        @JsonCreator
        public static BonusImpact fromValue(String value) {
            for (BonusImpact impact : BonusImpact.values()) {
                if (impact.value.equalsIgnoreCase(value)) {
                    return impact;
                }
            }
            throw new IllegalArgumentException("Invalid BonusImpact value: " + value);
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

}
