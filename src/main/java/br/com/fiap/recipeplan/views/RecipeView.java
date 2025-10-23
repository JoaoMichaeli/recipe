package br.com.fiap.recipeplan.views;

import br.com.fiap.recipeplan.model.Recipe;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class RecipeView extends VerticalLayout {
    public RecipeView(Recipe recipe) {
        setPadding(true);
        setSpacing(true);

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