package com.elevenpaths.googleindexretriever;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;

/**
 * The Class NavigatorMenu.
 */
@SuppressWarnings("restriction")
public class NavigatorMenu extends MenuButton {

	/**
	 * Instantiates a new navigator menu.
	 *
	 * @param webView the web view
	 */
	public NavigatorMenu(final WebView webView) {
		// Set the Text of the WebMenu
		this.setText(App.bundle.getString("app.navigator.options"));

		// Set the Style-properties of the Navigation Bar
		// this.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
		// + "-fx-border-insets: 5;" + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

		// Create the Menu Items
		final CheckMenuItem ctxMenu = new CheckMenuItem(App.bundle.getString("app.navigator.enableContextMenu"));
		ctxMenu.setSelected(true);

		final MenuItem normalFontMenu = new MenuItem(App.bundle.getString("app.navigator.normal"));
		final MenuItem biggerFontMenu = new MenuItem(App.bundle.getString("app.navigator.10bigger"));
		final MenuItem smallerFontMenu = new MenuItem(App.bundle.getString("app.navigator.10smaller"));

		final MenuItem normalZoomMenu = new MenuItem(App.bundle.getString("app.navigator.normal"));
		final MenuItem biggerZoomMenu = new MenuItem(App.bundle.getString("app.navigator.10bigger"));
		final MenuItem smallerZoomMenu = new MenuItem(App.bundle.getString("app.navigator.10smaller"));

		// Create the RadioMenuItems
		final RadioMenuItem grayMenu = new RadioMenuItem(App.bundle.getString("app.navigator.gray"));
		grayMenu.setSelected(true);
		final RadioMenuItem lcdMenu = new RadioMenuItem(App.bundle.getString("app.navigator.lcd"));

		// Create the Menus
		final Menu scalingMenu = new Menu(App.bundle.getString("app.navigator.fontScale"));
		scalingMenu.textProperty().bind(new SimpleStringProperty(App.bundle.getString("app.navigator.fontScale") + " ")
				.concat(webView.fontScaleProperty().multiply(100.0)).concat("%"));

		final Menu smoothingMenu = new Menu(App.bundle.getString("app.navigator.fontSmoothing"));

		final Menu zoomMenu = new Menu(App.bundle.getString("app.navigator.zoom"));
		zoomMenu.textProperty().bind(new SimpleStringProperty(App.bundle.getString("app.navigator.zoom") + " ")
				.concat(webView.zoomProperty().multiply(100.0)).concat("%"));

		// Add the Items to the corresponding Menu
		scalingMenu.getItems().addAll(normalFontMenu, biggerFontMenu, smallerFontMenu);
		smoothingMenu.getItems().addAll(grayMenu, lcdMenu);
		zoomMenu.getItems().addAll(normalZoomMenu, biggerZoomMenu, smallerZoomMenu);

		// Create the ToggleGroup
		new ToggleGroup().getToggles().addAll(lcdMenu, grayMenu);

		// Define the Event Handler
		normalFontMenu.setOnAction((event) -> {
			webView.setFontScale(1.0);
		});

		biggerFontMenu.setOnAction((event) -> {
			webView.setFontScale(webView.getFontScale() + 0.10);
		});

		smallerFontMenu.setOnAction((event) -> {
			webView.setFontScale(webView.getFontScale() - 0.10);
		});

		grayMenu.setOnAction((event) -> {
			webView.setFontSmoothingType(FontSmoothingType.GRAY);
		});

		lcdMenu.setOnAction((event) -> {
			webView.setFontSmoothingType(FontSmoothingType.LCD);
		});

		normalZoomMenu.setOnAction((event) -> {
			webView.setZoom(1.0);
		});

		biggerZoomMenu.setOnAction((event) -> {
			webView.setZoom(webView.getZoom() + 0.10);
		});

		smallerZoomMenu.setOnAction((event) -> {
			webView.setZoom(webView.getZoom() - 0.10);
		});

		webView.contextMenuEnabledProperty().bind(ctxMenu.selectedProperty());

		// Enabled JavaScript option
		final CheckMenuItem scriptMenu = new CheckMenuItem(App.bundle.getString("app.navigator.enableJavascript"));
		scriptMenu.setSelected(true);
		webView.getEngine().javaScriptEnabledProperty().bind(scriptMenu.selectedProperty());

		// Add Menus to the WebMenu
		this.getItems().addAll(ctxMenu, scalingMenu, smoothingMenu, zoomMenu, new SeparatorMenuItem(), scriptMenu);
	}
}