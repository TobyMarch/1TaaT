package com.taat.taskservices.services.comparators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taat.taskservices.model.UserTask;

@ExtendWith(MockitoExtension.class)
public class UserTaskSortComparatorTest {

    @Test
    public void testCompare() {
        List<UserTask> testList = new ArrayList<>();
        testList.add(new UserTask("1", "user1", "task1", null, null, null, false));
        testList.add(new UserTask("2", "user1", "task2", 12, null, null, false));
        testList.add(new UserTask("3", "user1", "task3", 5, null, null, false));
        testList.add(new UserTask("4", "user1", "task4", 13, null, null, false));

        UserTaskSortComparator sortComparator = new UserTaskSortComparator();
        List<UserTask> sortedList = testList.stream().sorted(sortComparator).collect(Collectors.toList());
        Assertions.assertNotNull(sortedList);
        Assertions.assertEquals(testList.size(), sortedList.size());

        UserTask firstUserTask = sortedList.get(0);
        Assertions.assertEquals(13, firstUserTask.getSortValue());
        UserTask lastUserTask = sortedList.get(sortedList.size() - 1);
        Assertions.assertNull(lastUserTask.getSortValue());
    }
}
