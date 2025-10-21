package br.com.fiap.recipeplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
public class RecipeplanApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(RecipeplanApplication.class, args);
    }
}
