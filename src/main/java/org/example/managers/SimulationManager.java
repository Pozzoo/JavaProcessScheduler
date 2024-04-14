package org.example.managers;

import org.example.Cpu;
import org.example.data.OutputLog;
import org.example.data.SimulationSpecs;
import org.example.data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationManager {
    private final Cpu cpu;
    private int cpuTime;
    private Map<Task, Integer> WTMap;
    private Map<Task, Integer> TATMap;
    private List<List<Task>> finalReadyQueue;
    private List<Task> finalCpu;

    public SimulationManager() {
        this.cpu = new Cpu();
    }

    public OutputLog startSimulation(SimulationSpecs specs) {

        int simulationTime = specs.simulation_time();
        boolean isComputing = false;

        List<Task> tasks = specs.tasks();
        List<Task> readyQueue = new ArrayList<>();

        warmupSimulation(tasks);

        switch (specs.scheduler_name().toLowerCase().replace(" ", "")) {
            case "firstcomefirstserve", "fcfs" -> {return fcfs(specs, simulationTime, isComputing, tasks, readyQueue);}

            case "roundrobin", "rr" -> { return roundRobin(specs, simulationTime, isComputing, tasks, readyQueue); }

            default -> {
                System.out.println("Escalonador Invalido");
                return null;
            }
        }

    }

    private void warmupSimulation(List<Task> tasks) {
        cpuTime = 0;
        WTMap = new HashMap<>();
        TATMap = new HashMap<>();

        finalReadyQueue = new ArrayList<>();
        finalCpu = new ArrayList<>();

        for (Task task : tasks) {
            WTMap.put(task, 0);
            TATMap.put(task, 0);
        }
    }

    private OutputLog fcfs(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        for (int i = 0; i < simulationTime; i++) {
            for (Task task : tasks) {
                if (i == task.getOffset() || ((i - task.getOffset()) % task.getPeriod_time()) == 0) {
                    readyQueue.add(task);
                }
            }

            System.out.println("time: " + i);
            isComputing = step(readyQueue, isComputing, false);

            finalReadyQueue.add(new ArrayList<>(readyQueue));
            finalCpu.add(cpu.getTaskInCpu());

            System.out.println("---");
        }

        return logInfo(specs, tasks);
    }

    private OutputLog roundRobin(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        for (int i = 0; i < simulationTime; i++) {
            for (Task task : tasks) {
                if (i == task.getOffset() || (i - task.getOffset() % task.getPeriod_time()) == 0) {
                    readyQueue.add(task);
                }
            }

            System.out.println("time: " + i);
            isComputing = step(readyQueue, isComputing, true);

            finalReadyQueue.add(new ArrayList<>(readyQueue));
            finalCpu.add(cpu.getTaskInCpu());
        }

        return logInfo(specs, tasks);
    }



    private boolean step(List<Task> readyQueue, boolean isComputing, boolean hasQuantum) {
        if (!readyQueue.isEmpty() || cpu.getTaskInCpu() != null) {

            if (!isComputing) {
                if (cpu.getTaskInCpu() != null && cpu.getTaskInCpu().getComputation_time() != 0) {
                    readyQueue.add(cpu.getTaskInCpu());
                }

                isComputing = cpu.compute(readyQueue.getFirst(), hasQuantum);
                readyQueue.removeFirst();
            } else {
                isComputing = cpu.compute(cpu.getTaskInCpu(), hasQuantum);
            }
            cpuTime++;
        }

        if (cpu.getTaskInCpu() != null) {
            TATMap.replace(cpu.getTaskInCpu(), TATMap.get(cpu.getTaskInCpu()) + 1);
        }

        for (Task task : readyQueue) {
            WTMap.replace(task, WTMap.get(task) + 1);
        }

        return isComputing;
    }

    private OutputLog logInfo(SimulationSpecs specs, List<Task> tasks) {
        float utilization = ((float) cpuTime / specs.simulation_time()) * 100;
        float productivity = (float) specs.tasks_number() / specs.simulation_time();

        float averageWaitingTime, waitingTimeSum = 0;
        float averageTurnAroundTime, turnAroundTimeSum = 0;

        for (Task task : tasks) {
            waitingTimeSum += WTMap.get(task);
            turnAroundTimeSum += (TATMap.get(task) + 1 + WTMap.get(task));
        }

        averageWaitingTime = waitingTimeSum / tasks.size();
        averageTurnAroundTime = turnAroundTimeSum / tasks.size();

        return new OutputLog(utilization, productivity, averageWaitingTime, averageTurnAroundTime, finalReadyQueue, finalCpu);
    }
}
