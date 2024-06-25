package com.solvd.gui.util;

import org.apache.logging.log4j.Logger;

public class TimingUtils {
    public static void timeMethod(Runnable method, Logger logger) {
        long start = System.nanoTime();
        method.run();
        long end = System.nanoTime();
        double durationSeconds = ((double) (end - start)) / 1_000_000_000d;
        logger.info("Execution took %.6f seconds".formatted(durationSeconds));
    }
}
