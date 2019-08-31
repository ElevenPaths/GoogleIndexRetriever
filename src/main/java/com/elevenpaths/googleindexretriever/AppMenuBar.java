package com.elevenpaths.googleindexretriever;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;

// TODO: Auto-generated Javadoc
/**
 * The Class AppMenuBar.
 */
@SuppressWarnings("restriction")
public class AppMenuBar extends MenuBar {

	/** The app. */
	private final App app;

	/**
	 * Instantiates a new app menu bar.
	 *
	 * @param app the app
	 */
	public AppMenuBar(final App app) {
		super();
		this.app = app;
		this.buildMenu();
	}

	/**
	 * Builds the menu.
	 */
	private void buildMenu() {

		// menu File
		final Menu menuFile = new Menu(App.bundle.getString("menu.file"));
		final MenuItem kewwords = new MenuItem(App.bundle.getString("menu.editKeywords"));
		final MenuItem spamkeywords = new MenuItem(App.bundle.getString("menu.editSpamKeywords"));
		menuFile.getItems().addAll(kewwords, spamkeywords);

		// menu Help
		final Menu menuHelp = new Menu(App.bundle.getString("menu.helpMenu"));
		final MenuItem help = new MenuItem(App.bundle.getString("menu.help"));
		final MenuItem about = new MenuItem(App.bundle.getString("menu.about"));
		menuHelp.getItems().addAll(help, about);

		// add menus to bar
		this.getMenus().addAll(menuFile, menuHelp);

		// kewwords Action Listener
		kewwords.setOnAction((event) -> {
			app.getRetrieverControl().launchKeywords(false);
		});

		// spamkeywords Action Listener
		spamkeywords.setOnAction((event) -> {
			app.getSpamControl().launchKeywords(true);
		});

		// help Action Listener
		help.setOnAction((event) -> {

			final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URL(App.bundle.getString("helpURL")).toURI());
				} catch (final MalformedURLException e1) {

					final Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle(App.bundle.getString("alert.title"));
					alert.setHeaderText(null);
					alert.setContentText(e1.getMessage());
					alert.showAndWait();

					e1.printStackTrace();
				} catch (final IOException e1) {

					final Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle(App.bundle.getString("alert.title"));
					alert.setHeaderText(null);
					alert.setContentText(e1.getMessage());
					alert.showAndWait();

					e1.printStackTrace();
				} catch (final URISyntaxException e1) {

					final Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle(App.bundle.getString("alert.title"));
					alert.setHeaderText(null);
					alert.setContentText(e1.getMessage());
					alert.showAndWait();

					e1.printStackTrace();
				}
			}

		});

		// about Action Listener
		about.setOnAction((event) -> {

			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(App.bundle.getString("alert.about.title"));
			alert.setHeaderText(null);

			final StringBuilder sb = new StringBuilder();
			sb.append("Google Index Retriever(c) ElevenPaths 2019. Version: 2.0.2 ")
					.append("author version 2.0  sbesada - Sergio Besada                       ")
					.append("author version 1.0  TODO");
			alert.setContentText(sb.toString());
			alert.showAndWait();

		});

	}

}
