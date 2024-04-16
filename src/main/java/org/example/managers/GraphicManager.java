package org.example.managers;

import org.example.data.SimulationSpecs;

public class GraphicManager {
    private static String[][] canvas;
    private final int height;
    private final int width;
    private static int lastTime = 0;

    public GraphicManager(SimulationSpecs specs) {
        width = specs.simulation_time() * 4;
        height = specs.tasks_number() + 3;

        canvas = new String[height][width];
    }

    public static void addPoint(int time, int processNumber) {
        if (processNumber != -1) {
            for (int i = lastTime; i < time * 4; i++) {
                canvas[processNumber][i] = "+";
            }
        }

        lastTime = time * 4;
    }

    public static void addLostDeadline(int time, int processNumber) {
        if (processNumber != -1) {
            canvas[processNumber][time * 4] = "!";
        }
    }

    public void draw() {
        int process = 1;
        int time = 0;

        for (int i = 0; i < height; i++) {
            if (i < height - 3) {
                System.out.print(process + " ");
                process++;
            } else {
                System.out.print("  ");
            }

            for (int j = 0; j < width; j++) {
                if (i < height - 3) {
                    if (canvas[i][j] == null) {
                        canvas[i][j] = ".";
                    }
                } else if (i == height - 3) {
                    canvas[i][j] = "-";
                } else if (j % 4 == 0 && i == height - 2){
                    canvas[i][j] = "|";

                    String timeStr = String.valueOf(time);

                    if (timeStr.length() == 1) {
                        canvas[i + 1][j] = timeStr;
                    } else {
                        for (int k = 0; k < timeStr.length(); k++) {
                            canvas[i + 1][j + k] = String.valueOf(timeStr.charAt(k));
                        }
                    }
                    time++;
                }

                if (canvas[i][j] != null) {
                    System.out.print(canvas[i][j]);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

    }
}
