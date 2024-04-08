package com.taat.taskservices.services.comparators;

import java.util.Comparator;

import com.taat.taskservices.model.UserTask;

public class UserTaskSortComparator implements Comparator<UserTask> {

    @Override
    public int compare(UserTask o1, UserTask o2) {

        return Comparator
                .nullsLast(
                        Comparator.comparing(UserTask::getSortValue, Comparator.nullsLast(Comparator.reverseOrder())))
                .compare(o1, o2);
    }

}
