package com.github.lukaslt1993.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ScratchGameConfigMapper {

    private final ObjectMapper objectMapper;

    public ScratchGameConfigMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public ScratchGameConfig mapJsonToConfig(String jsonFilePath) throws IOException {
        File configFile = new File(jsonFilePath);
        return objectMapper.readValue(configFile, ScratchGameConfig.class);
    }

}