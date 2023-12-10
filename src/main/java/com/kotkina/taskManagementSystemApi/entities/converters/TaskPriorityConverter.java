package com.kotkina.taskManagementSystemApi.entities.converters;

import com.kotkina.taskManagementSystemApi.entities.TaskPriority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, String> {

    @Override
    public String convertToDatabaseColumn(TaskPriority priority) {
        if (priority == null) {
            return null;
        }
        return priority.name();
    }

    @Override
    public TaskPriority convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(TaskPriority.values())
                .filter(c -> c.name().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
