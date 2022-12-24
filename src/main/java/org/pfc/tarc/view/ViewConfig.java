package org.pfc.tarc.view;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * This configuration creates beans related to the user interface.
 * 
 * @author Dan Avila
 */
@Configuration
public class ViewConfig
{
    /**
     * The root node for the user interface. Loaded from TrainingArcMain.fxml
     * 
     * @return             the ui bean.
     * @throws IOException if the primary resource could not be loaded.
     */
    @Bean
    public Parent rootNode() throws IOException
    {
        ClassPathResource resource = new ClassPathResource("TrainingArcMain.fxml");
        return FXMLLoader.load(resource.getURL());
    }
}
