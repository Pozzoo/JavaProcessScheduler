package org.example;

import org.example.data.Task;

public class Cpu {
    private int computationTime = 0;
    private Task taskInCpu;

    public Boolean nonPreeptiveCompute(Task task) {
        if (computationTime == 0) {
            computationTime = task.computation_time() - 1;
            taskInCpu = task;
        } else {
            computationTime -= 1;
        }
        return computationTime > 0;
    }

    public Task getTaskInCpu() {
        return taskInCpu;
    }

    public void setTaskInCpu(Task taskInCpu) {
        this.taskInCpu = taskInCpu;
    }
}
