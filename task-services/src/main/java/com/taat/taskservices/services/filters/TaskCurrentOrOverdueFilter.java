package com.taat.taskservices.services.filters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.utils.Constants;

public class TaskCurrentOrOverdueFilter implements Predicate<Task> {

    @Override
    public boolean test(Task t) {
        if (t.getDueDate() != null) {
            String current = LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
            return current.compareTo(t.getDueDate()) >= 1;
        } else {
            return false;
        }
    }

}
