package com.taat.taskservices.services.comparators;

import java.util.Comparator;

import com.taat.taskservices.model.Task;

import org.springframework.util.comparator.BooleanComparator;;

/**
 * Comparator that sorts non-delayable tasks before delayable
 */
public class TaskDelayableComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return new BooleanComparator(false).compare(o1.isDelayable(), o2.isDelayable());
    }

}
