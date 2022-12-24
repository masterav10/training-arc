package org.pfc.tarc;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

@SpringBootApplication
public class TrainingArcConfig
{
    /**
     * The root node for the user interface. Loaded from TrainingArcMain.fxml
     * 
     * @return             the ui bean.
     * @throws IOException
     */
    @Bean
    public Parent rootNode() throws IOException
    {
        ClassPathResource resource = new ClassPathResource("TrainingArcMain.fxml");
        return FXMLLoader.load(resource.getURL());
    }
}
