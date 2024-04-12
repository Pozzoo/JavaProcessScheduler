package org.example.data;

public class Task {
    private int offset;
    private int computation_time;
    private int period_time;
    private int quantum;
    private int deadline;

    public Task(int offset, int computation_time, int period_time, int quantum, int deadline) {
        this.offset = offset;
        this.computation_time = computation_time;
        this.period_time = period_time;
        this.quantum = quantum;
        this.deadline = deadline;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getComputation_time() {
        return computation_time;
    }

    public void setComputation_time(int computation_time) {
        this.computation_time = computation_time;
    }

    public int getPeriod_time() {
        return period_time;
    }

    public void setPeriod_time(int period_time) {
        this.period_time = period_time;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "offset=" + offset +
                ", computation_time=" + computation_time +
                ", period_time=" + period_time +
                ", quantum=" + quantum +
                ", deadline=" + deadline +
                '}';
    }
}
