package com.kotkina.taskManagementSystemApi.web.models.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TaskResponseList {

    List<TaskResponse> tasks = new ArrayList<>();
}
