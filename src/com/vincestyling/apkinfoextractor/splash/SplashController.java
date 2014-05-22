package com.vincestyling.apkinfoextractor.splash;

import com.vincestyling.apkinfoextractor.Main;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {
	private Main application;

	public void setApp(Main application) {
		this.application = application;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
	}

	public void doCreateSolution(ActionEvent actionEvent) throws Exception {
		if (application == null) {
			// We are running in isolated FXML, possibly in Scene Builder.
			return;
		}
		application.showCreateSolution();
	}
}
