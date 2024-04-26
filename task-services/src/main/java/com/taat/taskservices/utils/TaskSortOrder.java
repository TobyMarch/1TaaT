package com.taat.taskservices.utils;

import lombok.Getter;

public enum TaskSortOrder {
    ASC("ASC", 1), DESC("DESC", -1);

    @Getter
    String paramValue;
    @Getter
    Integer mongoSearchValue;

    TaskSortOrder(String paramValue, Integer searchValue) {
        this.paramValue = paramValue;
        this.mongoSearchValue = searchValue;
    }

    public static TaskSortOrder findByName(String paramValue) {
        TaskSortOrder result = null;
        for (TaskSortOrder value : values()) {
            if (value.getParamValue().equalsIgnoreCase(paramValue)) {
                result = value;
                break;
            }
        }
        return result;
    }
}
