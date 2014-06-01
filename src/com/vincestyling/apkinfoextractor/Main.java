package com.vincestyling.apkinfoextractor;

import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.launch.LaunchController;
import com.vincestyling.apkinfoextractor.splash.SplashController;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.wizard.WizardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
	private Stage stage;

	private final double MINIMUM_WINDOW_WIDTH = 390.0;
	private final double MINIMUM_WINDOW_HEIGHT = 500.0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.setTitle(Constancts.APP_NAME);
		stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
		stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
		showSplash();
		stage.show();
	}

	public void showSplash() throws Exception {
		SplashController splash = (SplashController) replaceSceneContent(Constancts.SPLASH_PAGE);
		splash.setApp(this);
	}

	private Initializable replaceSceneContent(String fxml) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = Main.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(Main.class.getResource(fxml));
		Parent root;
		try {
			root = (Parent) loader.load(in);
		} finally {
			in.close();
		}

		Scene scene = new Scene(root, 1000, 600);
		stage.setScene(scene);
		stage.sizeToScene();

		return loader.getController();
	}

	public void showCreateSolution() throws Exception {
		WizardController wizard = (WizardController) replaceSceneContent(Constancts.WIZARD_PAGE);
		wizard.setApp(this);
	}

	public void launchSolution(Solution solution) throws Exception {
		LaunchController launch = (LaunchController) replaceSceneContent(Constancts.LAUNCH_PAGE);
		launch.init(this, solution);
	}

	public static void main(String[] args) {
		Application.launch(Main.class, (java.lang.String[]) null);
	}
}
