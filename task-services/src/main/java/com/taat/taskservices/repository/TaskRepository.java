// package com.taat.taskservices.repository;

// import com.taat.taskservices.model.Task;

// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

// import org.springframework.data.domain.Pageable;
// import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface TaskRepository extends ReactiveMongoRepository<Task, String>
// {
// Flux<Task> findAllBy(Pageable pageable);

// Mono<Long> count();
// }
