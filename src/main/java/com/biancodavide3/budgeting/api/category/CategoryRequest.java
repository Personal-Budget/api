package com.biancodavide3.budgeting.api.category;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NotBlank
    private String name;
    @NotNull
    @Digits(integer = 10, fraction = 2, message = "goal must be a valid decimal number with up to 10 digits and 2 decimal places")
    private BigDecimal goal;
}
