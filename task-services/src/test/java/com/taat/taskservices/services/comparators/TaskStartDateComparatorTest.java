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
public class TaskStartDateComparatorTest {

    @Test
    public void testCompare() {
        List<Task> taskList = new ArrayList<>();
        Instant currentDateInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        taskList.add(new Task("1", "ext-1", "testOwner", "Test Task 1", "A task for testing", null,
                currentDateInstant.minus(1, ChronoUnit.DAYS), null, 5, Duration.M.toString(), Collections.emptyList(),
                false, false, Collections.emptyList()));
        taskList.add(new Task("2", "ext-2", "testOwner", "Test Task 2", "A task for testing", null,
                currentDateInstant.minus(2, ChronoUnit.DAYS), null, 5, Duration.M.toString(), Collections.emptyList(),
                false, false, Collections.emptyList()));
        taskList.add(
                new Task("3", "ext-3", "testOwner", "Test Task 3", "A task with a null start date", null, null, null, 5,
                        Duration.M.toString(), Collections.emptyList(), false, false, Collections.emptyList()));

        TaskStartDateComparator startDateComparator = new TaskStartDateComparator();
        List<Task> sortedList = taskList.stream().sorted(startDateComparator).collect(Collectors.toList());
        Assertions.assertNotNull(sortedList);
        Assertions.assertEquals(taskList.size(), sortedList.size());
        Task firstTask = sortedList.get(0);
        Assertions.assertNull(firstTask.getStartDate());
        Task secondTask = sortedList.get(1);
        Assertions.assertTrue(currentDateInstant.compareTo(secondTask.getStartDate()) > 0);
        Task lastTask = sortedList.get(sortedList.size() - 1);
        Assertions.assertTrue(lastTask.getStartDate().compareTo(secondTask.getStartDate()) > 0);
    }

}
