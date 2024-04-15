package org.example;

import org.example.managers.JsonManager;
import org.example.managers.SimulationManager;
import org.example.data.OutputLog;
import org.example.data.SimulationSpecs;

public class Main {
    public static void main(String[] args) {
        JsonManager jsonManager = new JsonManager();
        SimulationManager simulationManager = new SimulationManager();

        SimulationSpecs simulation = jsonManager.ReadJsonFile("src/main/java/org/example/exemplo_sched.json");
        SimulationSpecs simulation1 = jsonManager.ReadJsonFile("src/main/java/org/example/exemplo_rm.json");

        OutputLog outputLog = simulationManager.startSimulation(simulation1);
        System.out.println(outputLog);

        //jsonManager.WriteJsonLog(outputLog);
    }
}