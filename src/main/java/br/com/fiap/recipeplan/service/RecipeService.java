package br.com.fiap.recipeplan.service;

import br.com.fiap.recipeplan.model.Recipe;
import br.com.fiap.recipeplan.model.Ingredient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;

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
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    public Flux<String> getResponse(String message) {
        String formattedPrompt = """
        Gere uma receita completa e detalhada em **Markdown** para o prato: %s.
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
        Use formato limpo e direto, sem repetições.
        """.formatted(prompt);

        String response = chatClient
                .prompt()
                .user(formattedPrompt)
                .call()
                .content();

        assert response != null;
        return parseRecipeFromResponse(response, prompt);
    }

    private Recipe parseRecipeFromResponse(String response, String originalPrompt) {
        Recipe recipe = new Recipe();

        String cleanedResponse = cleanRecipeContent(response);

        String[] lines = cleanedResponse.split("\n");

        for (String line : lines) {
            if (line.startsWith("# ") && !line.startsWith("##")) {
                recipe.setName(line.replace("# ", "").trim());
                break;
            }
        }

        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            recipe.setName("Receita Gerada");
        }

        boolean inIngredientsSection = false;
        Pattern ingredientPattern = Pattern.compile("^-\\s*(.*?)$");

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.equalsIgnoreCase("## Ingredientes") ||
                    trimmedLine.equalsIgnoreCase("# Ingredientes") ||
                    trimmedLine.toLowerCase().contains("ingredientes")) {
                inIngredientsSection = true;
                continue;
            }

            if (inIngredientsSection) {
                if (trimmedLine.startsWith("##") ||
                        trimmedLine.startsWith("# ") ||
                        trimmedLine.toLowerCase().contains("modo de preparo") ||
                        trimmedLine.toLowerCase().contains("instruções")) {
                    break;
                }
            }
        }

        recipe.setInstructions(cleanedResponse);
        return recipe;
    }

    private String cleanRecipeContent(String content) {
        String[] lines = content.split("\n");
        List<String> cleanedLines = new ArrayList<>();
        boolean skipEmptyLines = false;
        boolean foundIngredients = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty() && cleanedLines.isEmpty()) {
                continue;
            }

            if (trimmedLine.startsWith("# ")) {
            } else if (trimmedLine.equalsIgnoreCase("## Ingredientes") ||
                    trimmedLine.equalsIgnoreCase("# Ingredientes") ||
                    trimmedLine.toLowerCase().contains("ingredientes")) {
                foundIngredients = true;
            } else if (trimmedLine.equalsIgnoreCase("## Modo de Preparo") ||
                    trimmedLine.equalsIgnoreCase("# Modo de Preparo") ||
                    trimmedLine.toLowerCase().contains("modo de preparo") ||
                    trimmedLine.toLowerCase().contains("instruções")) {
                foundIngredients = false;
            }

            if ((trimmedLine.toLowerCase().contains("para o bolo") ||
                    trimmedLine.toLowerCase().contains("para a cobertura")) &&
                    foundIngredients) {
                String finalTrimmedLine = trimmedLine;
                boolean alreadyExists = cleanedLines.stream()
                        .anyMatch(l -> l.toLowerCase().contains(finalTrimmedLine.toLowerCase()));
                if (alreadyExists) {
                    continue;
                }
            }

            if (trimmedLine.matches("^\\d+\\.\\s+.*")) {
                trimmedLine = trimmedLine.replaceFirst("^\\d+\\.\\s+", "- ");
            }

            trimmedLine = trimmedLine.replace("- [ ]", "-");

            if (trimmedLine.matches("^\\d+$") || trimmedLine.equals("-")) {
                continue;
            }

            if (isRepeatedTitle(trimmedLine, cleanedLines)) {
                continue;
            }

            if (!trimmedLine.isEmpty()) {
                cleanedLines.add(trimmedLine);
                skipEmptyLines = false;
            } else if (!skipEmptyLines && !cleanedLines.isEmpty()) {
                cleanedLines.add("");
                skipEmptyLines = true;
            }
        }

        return String.join("\n", cleanedLines);
    }

    private boolean isRepeatedTitle(String currentLine, List<String> previousLines) {
        if (!currentLine.startsWith("#")) return false;

        String currentTitle = currentLine.replace("#", "").trim().toLowerCase();

        for (String previousLine : previousLines) {
            if (previousLine.startsWith("#")) {
                String previousTitle = previousLine.replace("#", "").trim().toLowerCase();
                if (previousTitle.equals(currentTitle)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Ingredient parseIngredient(String ingredientText) {
        try {
            String cleanText = ingredientText.trim();

            String[] parts = cleanText.split("\\s+", 2);
            if (parts.length >= 2) {
                Ingredient ingredient = new Ingredient();
                ingredient.setQuantity(parts[0]);
                ingredient.setUnit("");
                ingredient.setName(parts[1]);
                return ingredient;
            } else {
                Ingredient ingredient = new Ingredient();
                ingredient.setQuantity("");
                ingredient.setUnit("");
                ingredient.setName(cleanText);
                return ingredient;
            }
        } catch (Exception e) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(ingredientText);
            return ingredient;
        }
    }
}