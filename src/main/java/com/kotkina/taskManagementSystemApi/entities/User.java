package com.kotkina.taskManagementSystemApi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Setter
    private String name;

    @Column(unique = true, nullable = false)
    @Setter
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    @Setter
    private String password;

    @Column(unique = true, nullable = false)
    @JsonIgnore
    private String hash;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Task> tasksWithAuthor = new ArrayList<>();

    @OneToMany(mappedBy = "executor", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Task> tasksWithExecutor = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @PrePersist
    public void initializeUUID() {
        if (hash == null) {
            hash = UUID.randomUUID().toString();
        }
    }
}
