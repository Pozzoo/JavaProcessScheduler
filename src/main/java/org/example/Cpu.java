package org.example;

import org.example.data.Task;

public class Cpu {
    private int computationTime = 0;
    private int quantum = 0;
    private Task taskInCpu;

    public boolean compute(Task task, boolean hasQuantum, boolean preempt) {

        if (computationTime <= 0 || quantum <= 0 || preempt) {
            if (task != null) {
                task.setComputation_time(task.getComputation_time() - 1);
                computationTime = task.getComputation_time();
                quantum = task.getQuantum() - 1;
                taskInCpu = task;
            } else {
                taskInCpu = null;
            }
        } else {
            task.setComputation_time(task.getComputation_time() - 1);
            computationTime = task.getComputation_time();
            quantum -= 1;
        }

        if (!hasQuantum) {
            quantum = 2;
        }

        if (taskInCpu != null) {
            System.out.println("    task In CPU: " + (taskInCpu.getIndex()));
            System.out.println("    " + taskInCpu.toString());
        }

        return computationTime > 0 && quantum > 0;
    }

    public Task getTaskInCpu() {
        return taskInCpu;
    }

    public void setTaskInCpu(Task taskInCpu) {
        this.taskInCpu = taskInCpu;
    }

    public int getCurrentQuantum() {
        return quantum;
    }
}
