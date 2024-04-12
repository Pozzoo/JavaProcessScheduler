package org.example;

import org.example.data.Task;

public class Cpu {
    private int computationTime = 0;
    private int quantum;
    private Task taskInCpu;

    public Boolean nonPreemptiveCompute(Task task) {
        if (computationTime == 0) {
            computationTime = task.getComputation_time() - 1;
            taskInCpu = task;
        } else {
            computationTime -= 1;
        }
        return computationTime > 0;
    }

    public boolean preemptiveCompute(Task task) {

        if (computationTime == 0 || quantum == 0) {
            task.setComputation_time(task.getComputation_time() - 1);
            computationTime = task.getComputation_time();
            quantum = task.getQuantum() - 1;
            taskInCpu = task;
        } else {
            task.setComputation_time(task.getComputation_time() - 1);
            computationTime -= 1;
            quantum -= 1;
        }
        return computationTime > 0 && quantum > 0;
    }

    public Task getTaskInCpu() {
        return taskInCpu;
    }

    public void setTaskInCpu(Task taskInCpu) {
        this.taskInCpu = taskInCpu;
    }
}
