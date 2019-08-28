package com.elevenpaths.googleindexretriever;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;

/**
 * The Class AppMenuBar.
 */
@SuppressWarnings("restriction")
public class AppMenuBar extends MenuBar {

	/**
	 * Instantiates a new app menu bar.
	 */
	public AppMenuBar() {
		super();
		this.createMenu();
	}

	/**
	 * Creates the menu.
	 */
	private void createMenu() {

		// menu File
		final Menu menuFile = new Menu("File");
		final MenuItem kewwords = new MenuItem("Spam Keywords");
		final MenuItem spamkeywords = new MenuItem("Retriever Keywords");
		menuFile.getItems().addAll(kewwords, spamkeywords);

		// menu Help
		final Menu menuHelp = new Menu("Help");
		final MenuItem help = new MenuItem("GIR Help");
		final MenuItem about = new MenuItem("About");
		menuHelp.getItems().addAll(help, about);

		help.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {

				final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URL("https://www.elevenpaths.com/labs/tools/index.html").toURI());
					} catch (final MalformedURLException e1) {

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();

						e1.printStackTrace();
					} catch (final IOException e1) {

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();

						e1.printStackTrace();
					} catch (final URISyntaxException e1) {

						final Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();

						e1.printStackTrace();
					}
				}

			}
		});

		this.getMenus().addAll(menuFile, menuHelp);

		about.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {

				final Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("About");
				alert.setHeaderText(null);
				alert.setContentText("Google Index Retriever(c) ElevenPaths 2019. Version: 2.0");
				alert.showAndWait();

			}
		});

	}

}
