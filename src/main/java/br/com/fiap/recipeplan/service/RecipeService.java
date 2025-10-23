package br.com.fiap.recipeplan.service;

import br.com.fiap.recipeplan.model.Recipe;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class RecipeService {

    private final ChatClient chatClient;

    public RecipeService(ChatClient.Builder builder) {
        String systemMessage = """
            Você é um chefe de receitas.
            Responda de forma clara e objetiva, mostrando passo a passo de como fazer a receita.
            Nunca responda temas diferentes de receitas.
            Nunca use HTML ou JSON, apenas Markdown.
            """;

        ChatOptions options = ChatOptions.builder()
                .temperature(0.2)
                .presencePenalty(1.0)
                .frequencyPenalty(1.0)
                .build();

        this.chatClient = builder
                .defaultSystem(systemMessage)
                .defaultOptions(options)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public Flux<String> getResponse(String message) {
        String prompt = "Gere uma receita completa e detalhada em **Markdown** para o prato: " + message;
        return chatClient.prompt().user(prompt).stream().content();
    }

    public Recipe generateRecipe(String prompt) {
        String message = "Gere uma receita completa e detalhada para o prato: " + prompt + ". Use formato limpo e direto.";
        String response = chatClient.prompt().user(message).call().content();

        Recipe recipe = new Recipe();
        recipe.setName(prompt);
        recipe.setInstructions(response != null ? response : "Receita não disponível.");

        return recipe;
    }
}
