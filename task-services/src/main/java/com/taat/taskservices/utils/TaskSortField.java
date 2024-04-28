package com.taat.taskservices.utils;

import lombok.Getter;

public enum TaskSortField {
    CREATED_DATE("createdDate", "result.createdDate"), START_DATE("startDate", "result.startDate"),
    DUE_DATE("dueDate", "result.dueDate"), PRIORITY("priority", "result.priority"), DEFAULT("", "sortValue");

    @Getter
    String paramValue;
    @Getter
    String mongoSearchValue;

    TaskSortField(String paramValue, String mongoSearchValue) {
        this.paramValue = paramValue;
        this.mongoSearchValue = mongoSearchValue;
    }

    public static TaskSortField findByParamName(String paramValue) {
        TaskSortField result = null;
        for (TaskSortField fieldValue : values()) {
            if (fieldValue.getParamValue().equalsIgnoreCase(paramValue)) {
                result = fieldValue;
                break;
            }
        }
        return result;
    }
}
