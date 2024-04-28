package com.taat.taskservices.services.filters;

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
public class TaskCurrentOrOverdueFilterTest {

    @Test
    public void testFilter() {
        List<Task> taskList = new ArrayList<>();
        Instant currentDateInstant = Instant.now().truncatedTo(ChronoUnit.DAYS);

        taskList.add(
                new Task("1", "testOwner", "Test Task 1", "A task due in the recent past", null, null,
                        currentDateInstant.minus(5, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), true, false, Collections.emptyList()));
        taskList.add(
                new Task("2", "testOwner", "Test Task 2", "A task due in the distant(ish) past", null, null,
                        currentDateInstant.minus(100, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("3", "testOwner", "Test Task 3", "A task with a null due date", null, null, null, 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("4", "testOwner", "Test Task 4", "A task due today", null, null,
                        currentDateInstant.plus(5, ChronoUnit.MINUTES), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("5", "testOwner", "Test Task 5", "A task due tomorrow", null, null,
                        currentDateInstant.plus(1, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("6", "testOwner", "Test Task 6", "A task next week", null, null,
                        currentDateInstant.plus(8, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));

        TaskCurrentOrOverdueFilter filter = new TaskCurrentOrOverdueFilter();
        List<Task> filteredList = taskList.stream().filter(filter).collect(Collectors.toList());
        Assertions.assertNotNull(filteredList);
        Assertions.assertEquals(3, filteredList.size());
        Assertions.assertTrue(filteredList.contains(taskList.get(0)));
        Assertions.assertTrue(filteredList.contains(taskList.get(1)));
        Assertions.assertTrue(filteredList.contains(taskList.get(3)));
        Assertions.assertFalse(filteredList.contains(taskList.get(2)));
        Assertions.assertFalse(filteredList.contains(taskList.get(4)));
        Assertions.assertFalse(filteredList.contains(taskList.get(5)));

    }
}
