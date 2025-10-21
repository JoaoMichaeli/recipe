package br.com.fiap.recipeplan.views;

import br.com.fiap.recipeplan.model.Recipe;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class RecipeView extends VerticalLayout {
    public RecipeView(Recipe recipe) {
        setPadding(true);
        setSpacing(true);

        var title = new H2(recipe.getName());
        var description = new Paragraph(recipe.getDescription());

        add(title, description);

        var ingredientsTitle = new H3("Ingredientes");
        add(ingredientsTitle);

        var ingredientsList = new MultiSelectListBox<String>();
        ingredientsList.setItems(recipe.getIngredients().stream().map(
                i -> i.getQuantity() + " " + i.getUnit() + " de " + i.getName()
        ).toList());
        
        add(ingredientsList);

        var instructionsTitle = new com.vaadin.flow.component.html.H3("Instruções");
        var instructions = new com.vaadin.flow.component.html.Paragraph(recipe.getInstructions());

        add(instructionsTitle, instructions);
    }
}
