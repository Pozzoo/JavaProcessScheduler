package org.example;

import org.example.managers.JsonManager;
import org.example.managers.SimulationManager;
import org.example.data.OutputLog;
import org.example.data.SimulationSpecs;

public class Main {
    public static void main(String[] args) {
        JsonManager jsonManager = new JsonManager();
        SimulationManager simulationManager = new SimulationManager();

        SimulationSpecs simulation1 = jsonManager.ReadJsonFile("C:\\Users\\pozzo\\Documents\\IntelliJ projects\\ProcessScheduler\\src\\main\\java\\org\\example\\exemplo_sched.json");

        OutputLog outputLog = simulationManager.fcfs(simulation1);

        //OutputLog outputLog = simulationManager.sjf(simulation1);

        System.out.println(outputLog);
    }
}