package org.example.data;

import java.util.List;

public record OutputLog(float utilization, float productivity, float averageWaitingTime, float averageTurnAroundTime, List<List<Task>> readyQueue, List<Task> cpu) {

    @Override
    public String toString() {
        return "OutputLog{" +
                "utilization=" + utilization +
                ", productivity=" + productivity +
                ", averageWaitingTime=" + averageWaitingTime +
                ", averageTurnAroundTime=" + averageTurnAroundTime +
                ", \n readyQueue=" + readyQueue +
                ", \n cpu=" + cpu +
                '}';
    }
}
