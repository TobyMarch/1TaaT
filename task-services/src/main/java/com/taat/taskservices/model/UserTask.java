package com.taat.taskservices.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("usertasks")
public class UserTask {

    @Id
    private String id;

    private String userId;

    private String taskId;

    private Double sortValue;

    private Instant skipUntil;

    private Instant lastSorted;

    private boolean archived;
}
