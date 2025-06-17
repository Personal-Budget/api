package com.biancodavide3.budgeting.models.categories;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryPostDTO {
    private String name;
    private double goal;
}
