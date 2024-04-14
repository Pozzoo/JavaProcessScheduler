package org.example.managers;

import com.google.gson.Gson;
import org.example.data.OutputLog;
import org.example.data.SimulationSpecs;

import java.io.*;

public class JsonManager {
    private final Gson gson;

    public JsonManager() {
        this.gson = new Gson();
    }

    public SimulationSpecs ReadJsonFile(String filePath) {

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            SimulationSpecs specs = gson.fromJson(br, SimulationSpecs.class);

            for (int i = 0; i < specs.tasks_number(); i++) {
                specs.tasks().get(i).setIndex(i);
            }

            return specs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void WriteJsonLog(OutputLog outputLog) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("OutputLog.json"))) {
            String json = gson.toJson(outputLog, OutputLog.class);

            bw.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
