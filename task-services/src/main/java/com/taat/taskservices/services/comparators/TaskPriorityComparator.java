package com.taat.taskservices.services.comparators;

import java.util.Comparator;

import com.taat.taskservices.model.Task;

/**
 * Comparator that sorts task in descending priority order
 */
public class TaskPriorityComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return Comparator.comparing(Task::getPriority).reversed().compare(o1, o2);
    }

}
