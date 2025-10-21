package br.com.fiap.recipeplan.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Recipe {
    private final UUID id = UUID.randomUUID();
    private String name;
    private String description;
    private List<Ingredient> ingredients = new ArrayList<>();
    private String instructions;

}
