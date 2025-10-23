package br.com.fiap.recipeplan.service;

import br.com.fiap.recipeplan.model.Recipe;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

@Service
public class RecipeService {

    private final ChatClient chatClient;

    public RecipeService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public Recipe generateRecipe(String dishName) {

        String systemMessage = """
            Você é um assistente culinário especialista.
            Sua tarefa é gerar uma receita completa e bem-estruturada para o prato solicitado.
            """;

        return chatClient.prompt()
                .system(systemMessage)
                .user("Gere uma receita para o seguinte prato: " + dishName)
                .call()
                .entity(Recipe.class);
    }
}