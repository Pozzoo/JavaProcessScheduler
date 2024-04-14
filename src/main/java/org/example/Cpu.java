package org.example;

import org.example.data.Task;

public class Cpu {
    private int computationTime = 0;
    private int quantum = 0;
    private Task taskInCpu;

    public boolean compute(Task task, boolean hasQuantum) {

        if (!hasQuantum) {
            quantum = 2;
        }

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

        System.out.println("    task In CPU: " + taskInCpu.getIndex());

        if (computationTime > 0 && quantum > 0) {
            return true;
        } else {
            taskInCpu = null;
            return false;
        }
    }

    public Task getTaskInCpu() {
        return taskInCpu;
    }

    public void setTaskInCpu(Task taskInCpu) {
        this.taskInCpu = taskInCpu;
    }
}
