package com.kotkina.taskManagementSystemApi.web.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ListPage {

    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 5;

    @NotBlank
    @PositiveOrZero
    @Schema(defaultValue = "0", description = "Номер страницы")
    private int page;

    @NotBlank
    @Positive
    @Schema(defaultValue = "5", description = "Кол-во записей на странице")
    private int size;

    public ListPage() {
        this.page = PAGE_NUMBER;
        this.size = PAGE_SIZE;
    }
}
