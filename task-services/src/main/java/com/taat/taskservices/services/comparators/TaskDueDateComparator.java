package com.taat.taskservices.services.comparators;

import java.util.Comparator;

import com.taat.taskservices.model.Task;

public class TaskDueDateComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        /*
         * Outer nullsLast will handle the cases when the Task objects are null.
         * Inner nullsLast will handle the cases when the return value of
         * Task::getDueDate is null.
         */
        return Comparator
                .nullsLast(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .compare(o1, o2);
    }

}
