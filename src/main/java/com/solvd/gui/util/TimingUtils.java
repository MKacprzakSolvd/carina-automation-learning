package com.solvd.gui.util;

import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class TimingUtils {
    public static void timeMethod(Supplier<?> method, Logger logger) {
        long start = System.nanoTime();
        method.get();
        long end = System.nanoTime();
        double durationSeconds = ((double) (end - start)) / 1_000_000_000d;
        logger.info("Execution took %.6f seconds".formatted(durationSeconds));
    }
}
