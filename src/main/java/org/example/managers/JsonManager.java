package org.example.managers;

import com.google.gson.Gson;
import org.example.data.SimulationSpecs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonManager {
    private final Gson gson;

    public JsonManager() {
        this.gson = new Gson();
    }

    public SimulationSpecs ReadJsonFile(String filePath) {

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return gson.fromJson(br, SimulationSpecs.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
