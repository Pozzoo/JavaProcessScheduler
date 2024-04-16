package org.example.managers;

import org.example.Cpu;
import org.example.data.OutputLog;
import org.example.data.SimulationSpecs;
import org.example.data.Task;

import java.util.ArrayList;
import java.util.List;

public class SimulationManager {
    private final Cpu cpu;
    private int cpuTime;
    private int[] waitingTime;
    private int[] turnAroundTime;

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

            case "ratemonotonic", "rm" -> { return  rateMonotonic(specs, simulationTime, isComputing, tasks, readyQueue); }

            case "earliestdeadlinefirst", "edf" -> { return earliestDeadlineFirst(specs, simulationTime, isComputing, tasks, readyQueue); }

            default -> {
                System.out.println("Escalonador Inválido");
                return null;
            }
        }

    }

    private void warmupSimulation(List<Task> tasks) {
        cpuTime = 0;
        waitingTime = new int[tasks.size()];
        turnAroundTime = new int[tasks.size()];


        for (int i = 0; i < tasks.size(); i++) {
            waitingTime[i] = 0;
            turnAroundTime[i] = 0;
        }
    }

    private OutputLog fcfs(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        for (int i = 0; i < simulationTime; i++) {
            for (Task task : tasks) {
                if (i == task.getOffset() || ((i - task.getOffset()) % task.getPeriod_time()) == 0) {
                    readyQueue.add(task.cloneTask());
                }
            }

            System.out.println("time: " + i);
            isComputing = step(readyQueue, isComputing, false, false, false, i);

            System.out.println("---");
        }

        return logInfo(specs, tasks);
    }

    private OutputLog roundRobin(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        for (int i = 0; i < simulationTime; i++) {
            for (Task task : tasks) {
                if (i == task.getOffset() || (i - task.getOffset() % task.getPeriod_time()) == 0) {
                    readyQueue.add(task.cloneTask());
                }
            }

            System.out.println("time: " + i);
            isComputing = step(readyQueue, isComputing, true, false, false, i);
        }

        return logInfo(specs, tasks);
    }

    private OutputLog rateMonotonic(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        float systemUtilization = 0;

        for (Task task : tasks) {
            systemUtilization += ((float) task.getComputation_time() / task.getPeriod_time());
        }

        if (systemUtilization > (specs.tasks_number() * (Math.pow(2, ((double) 1 / specs.tasks_number()) - 1)))) {
            System.out.println("Não Escalonavel");
            return logInfo(specs, tasks);

        } else {

            boolean preempt;

            for (int i = 0; i < simulationTime; i++) {
                for (Task task : tasks) {
                    addToList(readyQueue, i, task);
                }

                preempt = !readyQueue.isEmpty() && cpu.getTaskInCpu() != null && readyQueue.getFirst().getDeadline() < cpu.getTaskInCpu().getDeadline();

                System.out.println("time: " + i);
                isComputing = step(readyQueue, isComputing, false, preempt, true, i);

            }
        }


        return logInfo(specs, tasks);
    }

    private OutputLog earliestDeadlineFirst(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        float systemUtilization = 0;

        for (Task task : tasks) {
            systemUtilization += ((float) task.getComputation_time() / task.getPeriod_time());
        }

        if (systemUtilization > 1) {
            System.out.println("Não Escalonavel");
            return logInfo(specs, tasks);
        } else {

            boolean preempt;

            for (int i = 0; i < simulationTime; i++) {
                for (Task task : tasks) {
                    addToList(readyQueue, i, task);
                }

                preempt = !readyQueue.isEmpty() && cpu.getTaskInCpu() != null && readyQueue.getFirst().getRelativeDeadline() < cpu.getTaskInCpu().getRelativeDeadline();

                System.out.println("time: " + i);
                isComputing = step(readyQueue, isComputing, false, preempt, true, i);
            }
        }


        return logInfo(specs, tasks);
    }

    private void addToList(List<Task> readyQueue, int i, Task task) {
        boolean addedToList = false;

        if (i == task.getOffset() || (((i + 1) - task.getOffset()) % task.getPeriod_time()) == 0) {
            if (readyQueue.isEmpty()) {
                readyQueue.add(task.cloneTask());
            } else {
                for (int j = 0; j < readyQueue.size(); j++) {
                    if (task.getPeriod_time() < readyQueue.get(j).getPeriod_time()) {
                        readyQueue.add(j, task.cloneTask());
                        addedToList = true;
                        break;
                    }
                }

                if (!addedToList) {
                    readyQueue.add(task.cloneTask());
                }
            }
        }
    }

    private boolean step(List<Task> readyQueue, boolean isComputing, boolean hasQuantum, boolean preempt, boolean hasDeadline, int time) {
        if (!readyQueue.isEmpty() || cpu.getTaskInCpu() != null) {

            if (!isComputing || preempt) {
                if (cpu.getTaskInCpu() != null && cpu.getTaskInCpu().getComputation_time() != 0) {
                    readyQueue.add(cpu.getTaskInCpu());
                    cpu.setTaskInCpu(null);
                }

                if (!readyQueue.isEmpty()) {
                    isComputing = cpu.compute(readyQueue.getFirst(), hasQuantum, preempt);
                    readyQueue.removeFirst();
                } else {
                    System.out.println("    task In CPU: null");
                    GraphicManager.addPoint(time + 1, -1);
                }
            } else {
                isComputing = cpu.compute(cpu.getTaskInCpu(), hasQuantum, false);
            }

            cpuTime++;
            GraphicManager.addPoint(time + 1, cpu.getTaskInCpu().getIndex());
        }

        if (cpu.getTaskInCpu() != null) {
            turnAroundTime[cpu.getTaskInCpu().getIndex()]++;

            cpu.getTaskInCpu().setRelativeDeadline(cpu.getTaskInCpu().getRelativeDeadline() - 1);
            if (cpu.getTaskInCpu().getRelativeDeadline() <= 0 && hasDeadline) {
                GraphicManager.addLostDeadline(time, cpu.getTaskInCpu().getIndex());
                System.out.println("PERDA DE DEADLINE!");
            }
        }

        for (Task task : readyQueue) {
            waitingTime[task.getIndex()]++;

            task.setRelativeDeadline(task.getRelativeDeadline() - 1);
            if (task.getRelativeDeadline() <= 0 && hasDeadline) {
                GraphicManager.addLostDeadline(time, cpu.getTaskInCpu().getIndex());
                System.out.println("PERDA DE DEADLINE");
            }
        }

        return isComputing;
    }

    private OutputLog logInfo(SimulationSpecs specs, List<Task> tasks) {
        float utilization = ((float) cpuTime / specs.simulation_time()) * 100;
        float productivity = (float) specs.tasks_number() / specs.simulation_time();

        float averageWaitingTime, waitingTimeSum = 0;
        float averageTurnAroundTime, turnAroundTimeSum = 0;

        for (int i = 0; i < specs.tasks_number(); i++) {
            waitingTimeSum += waitingTime[i];
            turnAroundTimeSum += turnAroundTime[i];
        }

        averageWaitingTime = waitingTimeSum / tasks.size();
        averageTurnAroundTime = turnAroundTimeSum / tasks.size();

        return new OutputLog(utilization, productivity, averageWaitingTime, averageTurnAroundTime);
    }
}
