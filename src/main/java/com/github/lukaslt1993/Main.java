package com.github.lukaslt1993;

import com.github.lukaslt1993.config.ScratchGameConfig;
import com.github.lukaslt1993.config.ScratchGameConfigMapper;
import com.github.lukaslt1993.service.Printer;
import com.github.lukaslt1993.service.ScratchGame;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 4 || !args[0].equals("--config") || !args[2].equals("--betting-amount")) {
            System.out.println("Usage: java -jar <your-jar-file> --config <config_path> --betting-amount <bet_amount>");
            return;
        }

        String configPath = args[1];
        double betAmount = Double.parseDouble(args[3]);

        ScratchGameConfigMapper configMapper = new ScratchGameConfigMapper();
        ScratchGameConfig gameConfig = configMapper.mapJsonToConfig(configPath);

        ScratchGameConfig.GameInput gameInput = new ScratchGameConfig.GameInput();
        gameInput.setBetAmount(betAmount);

        ScratchGame scratchGame = new ScratchGame(gameConfig, gameInput);

        ScratchGameConfig.GameOutput gameOutput = scratchGame.playGame();

        Printer printer = new Printer();
        printer.printMatrix(gameOutput.getMatrix());
        System.out.println("\nTotal Reward: " + gameOutput.getReward());
        printer.printWinningCombinations(gameOutput.getAppliedWinningCombinations());
        System.out.println("Applied Bonus Symbol: " + gameOutput.getAppliedBonusSymbol());
    }

}
