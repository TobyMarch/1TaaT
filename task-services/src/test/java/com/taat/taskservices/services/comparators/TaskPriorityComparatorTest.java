package com.taat.taskservices.services.comparators;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taat.taskservices.model.Task;

@ExtendWith(MockitoExtension.class)
public class TaskPriorityComparatorTest {

    @Test
    public void testCompare() {
        List<Task> taskList = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        taskList.add(
                new Task("1", "testOwner", "Test Task 1", "A task for testing", null, null,
                        currentDateTime.minusDays(5l).toString(), 1,
                        false, false));
        taskList.add(
                new Task("2", "testOwner", "Test Task 2", "A task for testing", null, null,
                        currentDateTime.plusDays(100l).toString(), 10,
                        false, false));
        taskList.add(
                new Task("3", "testOwner", "Test Task 3", "A task with a null due date", null, null,
                        currentDateTime.minusDays(5l).toString(), 5, false, false));

        Comparator<Task> priorityComparator = new TaskPriorityComparator();
        List<Task> sortedList = taskList.stream().sorted(priorityComparator).collect(Collectors.toList());
        Assertions.assertNotNull(sortedList);
        Assertions.assertEquals(taskList.size(), sortedList.size());
        Assertions.assertEquals(10, sortedList.get(0).getPriority());
        Assertions.assertEquals(1, sortedList.get(sortedList.size() - 1).getPriority());

    }

}
