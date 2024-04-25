package com.taat.taskservices.services.comparators;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.utils.Duration;

@ExtendWith(MockitoExtension.class)
public class TaskDueDateComparatorTest {

    @Test
    public void testSort() {
        List<Task> taskList = new ArrayList<>();
        Instant currentDateInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        taskList.add(
                new Task("1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateInstant.minus(5, ChronoUnit.DAYS), 5, Duration.M.toString(), Collections.emptyList(),
                        false, false, Collections.emptyList()));
        taskList.add(
                new Task("2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateInstant.plus(100, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        null, 5, Duration.M.toString(), Collections.emptyList(), false, false,
                        Collections.emptyList()));

        TaskDueDateComparator dueDateComparator = new TaskDueDateComparator();
        List<Task> sortedList = taskList.stream().sorted(dueDateComparator).collect(Collectors.toList());
        Assertions.assertNotNull(sortedList);
        Task firstTask = sortedList.get(0);
        Assertions.assertTrue(currentDateInstant.compareTo(firstTask.getDueDate()) > 0);
        Task secondTask = sortedList.get(1);
        Assertions.assertTrue(secondTask.getDueDate().compareTo(firstTask.getDueDate()) > 0);
        Task lastTask = sortedList.get(sortedList.size() - 1);
        Assertions.assertNull(lastTask.getDueDate());
    }
}
