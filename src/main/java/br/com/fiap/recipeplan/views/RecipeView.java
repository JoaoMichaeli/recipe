package br.com.fiap.recipeplan.views;

import br.com.fiap.recipeplan.model.Recipe;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class RecipeView extends VerticalLayout {
    public RecipeView(Recipe recipe) {
        setPadding(true);
        setSpacing(true);

        var title = new H2(recipe.getName());
        var description = new Paragraph(recipe.getDescription());

        add(title, description);

        if (!recipe.getIngredients().isEmpty()) {
            var ingredientsTitle = new H3("Ingredientes");
            add(ingredientsTitle);

            var ingredientsList = new MultiSelectListBox<String>();
            ingredientsList.setItems(recipe.getIngredients().stream()
                    .map(i -> {
                        if (i.getQuantity() != null && !i.getQuantity().isEmpty() &&
                                i.getUnit() != null && !i.getUnit().isEmpty()) {
                            return i.getQuantity() + " " + i.getUnit() + " de " + i.getName();
                        } else {
                            return i.getName();
                        }
                    })
                    .toList());

            add(ingredientsList);
        }

        var instructionsTitle = new H3("Instruções");
        add(instructionsTitle);

        String htmlContent = convertMarkdownToHtml(recipe.getInstructions());
        Div instructionsDiv = new Div();
        instructionsDiv.getElement().setProperty("innerHTML", htmlContent);
        instructionsDiv.getStyle().set("white-space", "pre-wrap");

        add(instructionsDiv);
    }

    private String convertMarkdownToHtml(String markdown) {
        try {
            Parser parser = Parser.builder().build();
            org.commonmark.node.Node document = parser.parse(markdown);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            return renderer.render(document);
        } catch (Exception e) {
            return markdown.replace("\n", "<br>");
        }
    }
}