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
    Cpu cpu;
    int cpuTime;
    Map<Task, Integer> WTMap;
    Map<Task, Integer> TATMap;

    public SimulationManager() {
        this.cpu = new Cpu();
    }

    public OutputLog fcfs(SimulationSpecs specs) {

        int simulationTime = specs.simulation_time();
        boolean isComputing = false;

        List<Task> tasks = specs.tasks();
        List<Task> readyQueue = new ArrayList<>();

        warmupSimulation(tasks);

        for (int i = 0; i < simulationTime; i++) {
            for (Task task : tasks) {
                if (i == task.offset() || ((i - task.offset()) % task.period_time()) == 0) {
                    readyQueue.add(task);
                }
            }

            isComputing = nonPreemptiveStep(readyQueue, isComputing);

            System.out.println("time: " + (i) + ", task In CPU: " + (tasks.indexOf(cpu.getTaskInCpu()) + 1));
        }

        return logInfo(specs, tasks);
    }

    public OutputLog sjf(SimulationSpecs specs) {

        int simulationTime = specs.simulation_time();
        boolean isComputing = false;

        List<Task> tasks = specs.tasks();
        List<Task> readyQueue = new ArrayList<>();

        warmupSimulation(tasks);

        for (int i = 0; i < simulationTime; i++) {
            for (Task task : tasks) {
                if (i == task.offset() || ((i - task.offset()) % task.period_time()) == 0) {
                    if (readyQueue.isEmpty()) {
                        readyQueue.add(task);
                    } else {
                        for (int j = 0; j < readyQueue.size(); j++) {
                            if (task.computation_time() < readyQueue.get(j).computation_time()) {
                                readyQueue.add(readyQueue.indexOf(readyQueue.get(j)), task);
                                break;
                            }
                        }
                    }
                }
            }

            isComputing = nonPreemptiveStep(readyQueue, isComputing);

            System.out.println("time: " + (i) + ", task In CPU: " + (tasks.indexOf(cpu.getTaskInCpu()) + 1));
        }

        return logInfo(specs, tasks);
    }

    private void warmupSimulation(List<Task> tasks) {
        cpuTime = 0;
        WTMap = new HashMap<>();
        TATMap = new HashMap<>();

        for (Task task : tasks) {
            WTMap.put(task, 0);
            TATMap.put(task, 0);
        }
    }

    private boolean nonPreemptiveStep(List<Task> readyQueue, boolean isComputing) {

        if (!readyQueue.isEmpty() || isComputing) {

            if (!isComputing) {
                cpu.setTaskInCpu(null);
                isComputing = cpu.nonPreeptiveCompute(readyQueue.getFirst());
                readyQueue.removeFirst();
                cpuTime++;
            } else {
                isComputing = cpu.nonPreeptiveCompute(cpu.getTaskInCpu());
                cpuTime++;
                TATMap.replace(cpu.getTaskInCpu(), TATMap.get(cpu.getTaskInCpu()) + 1);
            }
        } else {
            cpu.setTaskInCpu(null);
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

        return new OutputLog(utilization, productivity, averageWaitingTime, averageTurnAroundTime);
    }
}
