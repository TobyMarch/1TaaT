package com.taat.taskservices.services.comparators;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.utils.Duration;

@ExtendWith(MockitoExtension.class)
public class TaskDelayableComparatorTest {

    @Test
    public void testCompare() {
        List<Task> taskList = new ArrayList<>();
        Instant currentDateInstant = Instant.now().truncatedTo(ChronoUnit.MINUTES);

        taskList.add(
                new Task("1", "ext-1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateInstant.minus(5, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), true, false, Collections.emptyList()));
        taskList.add(
                new Task("2", "ext-2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateInstant.plus(100, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));
        taskList.add(
                new Task("3", "ext-3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        currentDateInstant.minus(5, ChronoUnit.DAYS), 5, Duration.M.toString(),
                        Collections.emptyList(), false, false, Collections.emptyList()));

        Comparator<Task> delayableComparator = new TaskDelayableComparator();
        List<Task> sortedList = taskList.stream().sorted(delayableComparator).collect(Collectors.toList());
        Assertions.assertNotNull(sortedList);
        Assertions.assertEquals(taskList.size(), sortedList.size());
        Assertions.assertFalse(sortedList.get(0).isDelayable());
        Assertions.assertTrue(sortedList.get(sortedList.size() - 1).isDelayable());
    }

}
