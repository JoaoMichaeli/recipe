package br.com.fiap.recipeplan.views;

import br.com.fiap.recipeplan.model.Recipe;
import br.com.fiap.recipeplan.service.RecipeService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class HomeView extends VerticalLayout {

    private final VerticalLayout responsePanel = new VerticalLayout();

    @Autowired
    private RecipeService recipeService;

    public HomeView() {
        var input = new MessageInput();
        input.setWidthFull();

        add(new H1("Planejador de Receitas"));
        add(new Paragraph("O que você gostaria de cozinhar hoje?"));
        add(input, responsePanel);

        input.addSubmitListener(this::onSubmit);
    }

    private void onSubmit(MessageInput.SubmitEvent event) {
        String userPrompt = event.getValue();
        Recipe recipe = recipeService.generateRecipe(userPrompt);
        showRecipe(recipe);
    }

    private void showRecipe(Recipe recipe) {
        responsePanel.removeAll();
        responsePanel.add(new RecipeView(recipe));
    }
}
