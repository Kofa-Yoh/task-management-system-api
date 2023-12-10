package com.kotkina.taskManagementSystemApi.controllers;

import com.kotkina.taskManagementSystemApi.entities.Task;
import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.repositories.TaskRepository;
import com.kotkina.taskManagementSystemApi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class ManagementControllerTests {

    private static final String CURRENT_USER_EMAIL = "nikita@gmail.com";
    private static final String NEW_USER_EMAIL = "test@gmail.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @WithUserDetails(CURRENT_USER_EMAIL)
    public void addTask_Status200() throws Exception {
        String title = "Task 1";

        mockMvc.perform(post("/api/management/task/new")
                        .param("title", title)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.author.email", is(CURRENT_USER_EMAIL)));
    }

    @Test
    @WithUserDetails(CURRENT_USER_EMAIL)
    public void updateSomeonesTask_Status403() throws Exception {
        Task newTask = createTask(createUser(NEW_USER_EMAIL));

        mockMvc.perform(put("/api/management/task/update/status")
                        .param("id", Long.toString(newTask.getId()))
                        .param("status", "PROCESSING"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getTasksByAuthor_WithoutAuth_Status404() throws Exception {
        mockMvc.perform(get("/api/management/tasks/author"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(CURRENT_USER_EMAIL)
    public void getTasksByAuthor_WithAuth_Status200_PageSize() throws Exception {
        int size = 2;

        mockMvc.perform(get("/api/management/tasks/author")
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.*", hasSize(size)));
    }

    @Test
    @WithUserDetails(CURRENT_USER_EMAIL)
    public void noTasks_WithAuth_Status204() throws Exception {
        createUser(NEW_USER_EMAIL);

        mockMvc.perform(get("/api/management/tasks/author/" + NEW_USER_EMAIL))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private User createUser(String email) {
        if (userRepository.existsByEmail(email)) {
            return userRepository.findUserByEmail(email).get();
        }
        return userRepository.saveAndFlush(new User("Test", email, "test"));
    }

    private Task createTask(User user) {
        Task task = new Task();
        task.setTitle("Task test");
        task.setAuthor(user);

        return taskRepository.saveAndFlush(task);
    }

}