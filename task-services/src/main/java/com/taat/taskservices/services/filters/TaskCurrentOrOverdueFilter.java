package com.taat.taskservices.services.filters;

import java.time.Instant;
import java.util.function.Predicate;

import com.taat.taskservices.model.Task;

public class TaskCurrentOrOverdueFilter implements Predicate<Task> {

    @Override
    public boolean test(Task t) {
        if (t.getDueDate() != null) {
            // String current =
            // LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
            Instant current = Instant.now();
            return current.compareTo(t.getDueDate()) >= 1;
        } else {
            return false;
        }
    }

}
