package com.taat.taskservices.services.filters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;

import com.taat.taskservices.model.Task;

public class TaskCurrentOrOverdueFilter implements Predicate<Task> {

    @Override
    public boolean test(Task t) {
        if (t.getDueDate() != null) {
            Instant current = Instant.now().truncatedTo(ChronoUnit.DAYS);
            return current.compareTo(t.getDueDate().truncatedTo(ChronoUnit.DAYS)) >= 0;
        } else {
            return false;
        }
    }

}
