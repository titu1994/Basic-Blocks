package com.somshubra.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Yue on 15-Mar-16.
 */
public class ParallelQuickSortTask extends RecursiveAction{

    private static int NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int MIN_GRANULARITY = 8192;

    private int data[];
    private int low, high;
    private int THRESHOLD = 0;

    public ParallelQuickSortTask(int[] data, int low, int high, int threshold) {
        this.data = data;
        this.low = low;
        this.high = high;
        this.THRESHOLD = threshold;
    }

    public static int computeThreshold(int n) {
        int g = 0;
        int threshold = (g = (n / (NO_OF_THREADS) << 2)) <= MIN_GRANULARITY ?  MIN_GRANULARITY: g;
        return threshold;
    }

    @Override
    protected void compute() {
        if((high - low) <= THRESHOLD) {
            Arrays.sort(data, low, high+1);
            return;
        }

        ArrayList<ParallelQuickSortTask> tasks = new ArrayList<>();

        int i = low, j = high;
        int pivot = data[(low + high) >>> 1];
        int temp;

        while (i <= j) {
            while (data[i] < pivot) {
                i++;
            }

            while (data[j] > pivot) {
                j--;
            }

            if (i <= j) {
                temp = data[i];
                data[i] = data[j];
                data[j] = temp;
                i++;
                j--;
            }
        }

        if (low < j) {
            ParallelQuickSortTask t = new ParallelQuickSortTask(data, low, j, THRESHOLD);
            tasks.add(t);
            t.fork();
        }
        if (i < high) {
            ParallelQuickSortTask t = new ParallelQuickSortTask(data, i, high, THRESHOLD);
            tasks.add(t);
            t.fork();
        }

        for (ParallelQuickSortTask t : tasks)
            t.join();
    }
}
