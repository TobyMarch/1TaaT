package com.taat.taskservices.services.comparators;

import java.util.Comparator;

import com.taat.taskservices.model.Task;

public class TaskStartDateComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        /*
         * Outer nullsFirst will handle the cases when the Task objects are null.
         * Inner nullsFirst will handle the cases when the return value of
         * Task::getStartDate is null.
         */
        return Comparator
                .nullsFirst(Comparator.comparing(Task::getStartDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .compare(o1, o2);
    }

}
