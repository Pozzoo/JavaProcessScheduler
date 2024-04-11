package org.example.data;

import java.util.List;

public record SimulationSpecs(int simulation_time, String scheduler_name, int tasks_number, List<Task> tasks) {
}
