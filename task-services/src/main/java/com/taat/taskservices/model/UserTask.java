package com.taat.taskservices.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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

    private LocalDateTime skipUntil;

    private LocalDateTime lastSorted;

    private boolean archived;
}
