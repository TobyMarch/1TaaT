package com.taat.taskservices.services.comparators;

import java.util.Comparator;
import com.taat.taskservices.model.Task;
import com.taat.taskservices.utils.Duration;


/**
 * Comparator that sorts tasks by the ordinal value of their duration enum.
 * Tasks with null durations or null tasks themselves are placed at the end.
 */

public class TaskDurationComparator implements Comparator<Task> {
    private static final int MAX_ORDINAL_FOR_NULL = 5;
    @Override
    public int compare(Task o1, Task o2) {
        Comparator<Task> ordinalComparator = new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                // Convert string to enum and compare ordinal values
                int ordinal1 = t1.getDuration() != null ? Duration.valueOf(t1.getDuration().toUpperCase()).ordinal() : MAX_ORDINAL_FOR_NULL;
                int ordinal2 = t2.getDuration() != null ? Duration.valueOf(t2.getDuration().toUpperCase()).ordinal() : MAX_ORDINAL_FOR_NULL;
                return Integer.compare(ordinal1, ordinal2);
            }
        };
        return Comparator.nullsLast(ordinalComparator).compare(o1, o2);
    }
}
