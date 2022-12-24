package org.pfc.tarc;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.stage.Stage;

public class TrainingArcApplication extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    private ConfigurableApplicationContext ctx;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Training Arc");
        primaryStage.show();
    }

    @Override
    public void init() throws Exception
    {
        String[] args = getParameters().getRaw()
                                       .stream()
                                       .toArray(String[]::new);

        this.ctx = SpringApplication.run(ComboApplicationConfig.class, args);
    }

    @Override
    public void stop() throws Exception
    {
        this.ctx.close();
    }
}
