package org.example.managers;

import org.example.Cpu;
import org.example.data.SimulationSpecs;
import org.example.data.Task;

import java.util.ArrayList;
import java.util.List;

public class SimulationManager {
    private final Cpu cpu;
    private int cpuTime;
    private int[] waitingTime;
    private int[] turnAroundTime;
    private int[] activationQuantity;
    private int[] lostDeadlineQuantity;

    public SimulationManager() {
        this.cpu = new Cpu();
    }

    public void startSimulation(SimulationSpecs specs) {

        int simulationTime = specs.simulation_time();
        boolean isComputing = false;

        List<Task> tasks = specs.tasks();
        List<Task> readyQueue = new ArrayList<>();

        warmupSimulation(tasks);

        switch (specs.scheduler_name().toLowerCase().replace(" ", "")) {
            case "firstcomefirstserve", "fcfs" -> fcfs(specs, simulationTime, isComputing, tasks, readyQueue);

            case "roundrobin", "rr" -> roundRobin(specs, simulationTime, isComputing, tasks, readyQueue);

            case "ratemonotonic", "rm" -> rateMonotonic(specs, simulationTime, isComputing, tasks, readyQueue);

            case "earliestdeadlinefirst", "edf" -> earliestDeadlineFirst(specs, simulationTime, isComputing, tasks, readyQueue);

            default -> System.out.println("Escalonador Inválido");
        }

    }

    private void warmupSimulation(List<Task> tasks) {
        cpuTime = 0;
        waitingTime = new int[tasks.size()];
        turnAroundTime = new int[tasks.size()];
        activationQuantity = new int[tasks.size()];
        lostDeadlineQuantity = new int[tasks.size()];


        for (int i = 0; i < tasks.size(); i++) {
            waitingTime[i] = 0;
            turnAroundTime[i] = 0;
        }
    }

    private void fcfs(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        for (int i = 0; i < simulationTime; i++) {
            nonPreemptiveAddToList(tasks, readyQueue, i);
            isComputing = step(readyQueue, isComputing, false, false, false, i);
        }

        logInfo(specs, tasks);
    }

    private void roundRobin(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        for (int i = 0; i < simulationTime; i++) {
            nonPreemptiveAddToList(tasks, readyQueue, i);
            isComputing = step(readyQueue, isComputing, true, false, false, i);
        }

        logInfo(specs, tasks);
    }

    private void nonPreemptiveAddToList(List<Task> tasks, List<Task> readyQueue, int i) {
        for (Task task : tasks) {
            if (i == task.getOffset() || ((i - task.getOffset()) % task.getPeriod_time()) == 0) {
                readyQueue.add(task.cloneTask());
                activationQuantity[task.getIndex()]++;
            }
        }

        System.out.println("time: " + i);
    }

    private void rateMonotonic(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        float systemUtilization = 0;

        for (Task task : tasks) {
            systemUtilization += ((float) task.getComputation_time() / task.getPeriod_time());
        }

        if (systemUtilization > (specs.tasks_number() * (Math.pow(2, ((double) 1 / specs.tasks_number()) - 1)))) {
            System.out.println("Não Escalonavel");
            return;

        } else {

            boolean preempt;

            for (int i = 0; i < simulationTime; i++) {
                for (Task task : tasks) {
                    preemptiveAddToList(readyQueue, i, task);
                }

                preempt = !readyQueue.isEmpty() && cpu.getTaskInCpu() != null && readyQueue.getFirst().getDeadline() < cpu.getTaskInCpu().getDeadline();

                System.out.println("time: " + i);
                isComputing = step(readyQueue, isComputing, false, preempt, true, i);

            }
        }


        logInfo(specs, tasks);
    }

    private void earliestDeadlineFirst(SimulationSpecs specs, int simulationTime, boolean isComputing, List<Task> tasks, List<Task> readyQueue) {
        float systemUtilization = 0;

        for (Task task : tasks) {
            systemUtilization += ((float) task.getComputation_time() / task.getPeriod_time());
        }

        if (systemUtilization > 1) {
            System.out.println("Não Escalonavel");
            return;
        } else {

            boolean preempt;

            for (int i = 0; i < simulationTime; i++) {
                for (Task task : tasks) {
                    preemptiveAddToList(readyQueue, i, task);
                }

                preempt = !readyQueue.isEmpty() && cpu.getTaskInCpu() != null && readyQueue.getFirst().getRelativeDeadline() < cpu.getTaskInCpu().getRelativeDeadline();

                System.out.println("time: " + i);
                isComputing = step(readyQueue, isComputing, false, preempt, true, i);
            }
        }


        logInfo(specs, tasks);
    }

    private void preemptiveAddToList(List<Task> readyQueue, int i, Task task) {
        boolean addedToList = false;

        if (i == task.getOffset() || (((i + 1) - task.getOffset()) % task.getPeriod_time()) == 0) {
            if (readyQueue.isEmpty()) {
                readyQueue.add(task.cloneTask());
                activationQuantity[task.getIndex()]++;
            } else {
                for (int j = 0; j < readyQueue.size(); j++) {
                    if (task.getPeriod_time() < readyQueue.get(j).getPeriod_time()) {
                        readyQueue.add(j, task.cloneTask());
                        activationQuantity[task.getIndex()]++;
                        addedToList = true;
                        break;
                    }
                }

                if (!addedToList) {
                    readyQueue.add(task.cloneTask());
                    activationQuantity[task.getIndex()]++;
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
                    isComputing = cpu.compute(cpu.getTaskInCpu(), hasQuantum, preempt);
                }
            } else {
                isComputing = cpu.compute(cpu.getTaskInCpu(), hasQuantum, false);
            }
        }

        if (cpu.getTaskInCpu() != null) {
            turnAroundTime[cpu.getTaskInCpu().getIndex()]++;

            GraphicManager.addPoint(time + 1, cpu.getTaskInCpu().getIndex());
            cpuTime++;

            cpu.getTaskInCpu().setRelativeDeadline(cpu.getTaskInCpu().getRelativeDeadline() - 1);
            if (cpu.getTaskInCpu().getRelativeDeadline() == 0 && hasDeadline) {
                GraphicManager.addLostDeadline(time, cpu.getTaskInCpu().getIndex());
                lostDeadlineQuantity[cpu.getTaskInCpu().getIndex()]++;
            }
        } else {
            System.out.println("    task In CPU: null");
            GraphicManager.addPoint(time + 1, -1);
        }

        for (Task task : readyQueue) {
            waitingTime[task.getIndex()]++;

            task.setRelativeDeadline(task.getRelativeDeadline() - 1);
            if (task.getRelativeDeadline() == 0 && hasDeadline) {
                GraphicManager.addLostDeadline(time, cpu.getTaskInCpu().getIndex());
                lostDeadlineQuantity[task.getIndex()]++;
            }
        }
        return isComputing;
    }

    private void logInfo(SimulationSpecs specs, List<Task> tasks) {
        System.out.println(" ");

        float utilization = ((float) cpuTime / specs.simulation_time()) * 100;
        float productivity = (float) specs.tasks_number() / specs.simulation_time();

        float averageWaitingTime, waitingTimeSum = 0;
        float averageTurnAroundTime, turnAroundTimeSum = 0;

        int longestWaitingTime = 0, longestIndex = 0;
        int shortestWaitingTime = specs.simulation_time() + 5, shortestIndex = 0;

        for (int i = 0; i < specs.tasks_number(); i++) {
            waitingTimeSum += waitingTime[i];
            turnAroundTimeSum += turnAroundTime[i] + waitingTime[i];

            System.out.println("activations for task " + (i + 1) + ": " + activationQuantity[i]);
            System.out.println("waiting time for task " + (i + 1) + ": " + waitingTime[i]);
            System.out.println("turn around time for task " + (i + 1) + ": " + (turnAroundTime[i] + waitingTime[i]));

            if (turnAroundTime[i] + waitingTime[i] >= specs.simulation_time()) {
                System.out.println("starvation for task " + (i + 1));
            }

            if (lostDeadlineQuantity[i] != 0) {
                System.out.println("lost deadline for task " + (i + 1));
                System.out.println("lost deadline ratio: " + ((float) lostDeadlineQuantity[i] / activationQuantity[i]));
            }
            System.out.println("-------------------------------------");

            if (longestWaitingTime < waitingTime[i]) {
                longestWaitingTime = waitingTime[i];
                longestIndex = i;
            }

            if (shortestWaitingTime > waitingTime[i]) {
                shortestWaitingTime = waitingTime[i];
                shortestIndex = i;
            }
        }

        System.out.println("task " + (longestIndex + 1) + " had the longest waiting time: " + longestWaitingTime);
        System.out.println("task " + (shortestIndex + 1) + " had the shortest waiting time: " + shortestWaitingTime);

        averageWaitingTime = waitingTimeSum / tasks.size();
        averageTurnAroundTime = turnAroundTimeSum / tasks.size();

        System.out.println("utilization: " + utilization + ", productivity: " + productivity + ", averageWaitingTime: " + averageWaitingTime + ", averageTurnAroundTime: " + averageTurnAroundTime);


    }
}
