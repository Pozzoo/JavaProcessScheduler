package org.example.data;

public record OutputLog(float utilization, float productivity, float averageWaitingTime, float averageTurnAroundTime) {

    @Override
    public String toString() {
        return "OutputLog{" +
                "utilization=" + utilization +
                ", productivity=" + productivity +
                ", averageWaitingTime=" + averageWaitingTime +
                ", averageTurnAroundTime=" + averageTurnAroundTime +
                '}';
    }
}
