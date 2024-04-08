package com.taat.taskservices.utils;

public enum Duration {
    S("short"), M("medium"), L("long"), XL("extra-long");

    String name;

    Duration(String n) {
        this.name = n;
    }

    String getName() {
        return this.name;
    }
}
