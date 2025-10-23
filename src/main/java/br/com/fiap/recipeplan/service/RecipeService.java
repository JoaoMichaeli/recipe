package br.com.fiap.recipeplan.service;

import br.com.fiap.recipeplan.model.Recipe;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class RecipeService {

    private final String systemMessage = """
        Você é um chefe de receitas.
        Responda de forma clara e objetiva, mostrando passo a passo de como fazer a receita.
        Nunca responda temas diferentes de receitas.
        Nunca use HTML ou JSON, apenas Markdown.
        """;

    private final ChatOptions options = ChatOptions.builder()
            .temperature(0.2)
            .presencePenalty(1.0)
            .frequencyPenalty(1.0)
            .build();

    private final ChatClient chatClient;

    public RecipeService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(systemMessage)
                .defaultOptions(options)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    public Flux<String> getResponse(String message) {
        String formattedPrompt = """
        Gere uma receita completa e detalhada em **Markdown** para o prato: %s.
        Inclua nome, descrição, lista de ingredientes e modo de preparo passo a passo.
        """.formatted(message);

        return chatClient
                .prompt()
                .user(formattedPrompt)
                .stream()
                .content();
    }

    public Recipe generateRecipe(String prompt) {
        String formattedPrompt = """
        Gere uma receita completa e detalhada para o prato: %s.
        Inclua nome, descrição, lista de ingredientes e modo de preparo passo a passo.
        Responda apenas em texto simples (Markdown).
        """.formatted(prompt);

        String response = chatClient
                .prompt()
                .user(formattedPrompt)
                .call()
                .content();

        Recipe recipe = new Recipe();
        recipe.setName("Receita Gerada");
        recipe.setDescription("Baseado no seu pedido: " + prompt);
        recipe.setInstructions(response);
        return recipe;
    }
}
