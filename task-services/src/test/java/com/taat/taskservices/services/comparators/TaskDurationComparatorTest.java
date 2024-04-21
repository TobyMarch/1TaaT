package com.taat.taskservices.services.comparators;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import com.taat.taskservices.model.Task;
import com.taat.taskservices.utils.Duration;

@ExtendWith(MockitoExtension.class)
public class TaskDurationComparatorTest {

    @Test
    public void testCompare() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("1", "testOwner", "Task Short", "A short task", null, null, null, 5, Duration.S.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("2", "testOwner", "Task Medium", "A medium task", null, null, null, 5, Duration.M.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("3", "testOwner", "Task Long", "A long task", null, null, null, 5, Duration.L.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("4", "testOwner", "Task Extra Long", "An extra-long task", null, null, null, 5, Duration.XL.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("5", "testOwner", "Task Null Duration", "A task with null duration", null, null, null, 5, null, false, false, Collections.emptyList()));

        Collections.shuffle(taskList);
        TaskDurationComparator comparator = new TaskDurationComparator();
        Collections.sort(taskList, comparator);
        
        Assertions.assertNotNull(taskList, "Task list should not be null.");
        Assertions.assertEquals(5, taskList.size(), "The list should contain exactly 5 tasks.");
        Assertions.assertNull(taskList.get(4).getDuration(), "Task with null duration should be last.");
        Assertions.assertEquals(Duration.S.toString(), taskList.get(0).getDuration(), "Shortest duration should be next.");
        Assertions.assertEquals(Duration.M.toString(), taskList.get(1).getDuration(), "Medium duration should follow.");
        Assertions.assertEquals(Duration.L.toString(), taskList.get(2).getDuration(), "Long duration should be next.");
        Assertions.assertEquals(Duration.XL.toString(), taskList.get(3).getDuration(), "Extra-long duration should be last.");
    }

    @Test
    public void testCompareWithReverseOrder() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task("4", "testOwner", "Task Extra Long", "An extra-long task", null, null, null, 5, Duration.XL.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("3", "testOwner", "Task Long", "A long task", null, null, null, 5, Duration.L.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("2", "testOwner", "Task Medium", "A medium task", null, null, null, 5, Duration.M.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("1", "testOwner", "Task Short", "A short task", null, null, null, 5, Duration.S.toString(), false, false, Collections.emptyList()));
        taskList.add(new Task("5", "testOwner", "Task Null Duration", "A task with null duration", null, null, null, 5, null, false, false, Collections.emptyList()));

        TaskDurationComparator comparator = new TaskDurationComparator();
        Collections.sort(taskList, comparator);

        Assertions.assertNotNull(taskList, "Task list should not be null.");
        Assertions.assertEquals(5, taskList.size(), "The list should contain exactly 5 tasks.");
        Assertions.assertNull(taskList.get(4).getDuration(), "Task with null duration should be last.");
        Assertions.assertEquals(Duration.S.toString(), taskList.get(0).getDuration(), "Shortest duration should be next.");
        Assertions.assertEquals(Duration.M.toString(), taskList.get(1).getDuration(), "Medium duration should follow.");
        Assertions.assertEquals(Duration.L.toString(), taskList.get(2).getDuration(), "Long duration should be next.");
        Assertions.assertEquals(Duration.XL.toString(), taskList.get(3).getDuration(), "Extra-long duration should be last.");
    }
}
