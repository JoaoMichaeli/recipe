package br.com.fiap.recipeplan.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Ingredient {
    private final UUID id = UUID.randomUUID();
    private String name;
    private String quantity;
    private String unit;
}
