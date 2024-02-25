package com.example;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
public class SpringBootApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApiApplication.class, args);
    }

}


@RestController
class DataController {
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/data")
    public void receiveData(@RequestBody DataDTO data) {
        // Here you can process the received data
        System.out.println("Received data: " + data.toString());
        // You can perform actions like saving the data to a database, etc.
    }
}

class DataDTO {
    private String owner;
    private String task;
    private String dueDate;
    private String addedDate;
    private int rating;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "DataDTO{" +
                "owner='" + owner + '\'' +
                ", task='" + task + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", addedDate='" + addedDate + '\'' +
                ", rating=" + rating +
                '}';
    }
}
