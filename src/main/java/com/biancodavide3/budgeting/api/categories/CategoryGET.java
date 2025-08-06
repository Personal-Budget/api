package com.biancodavide3.budgeting.api.categories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryGET {
    private Long id;
    private String name;
    private double goal;
}
