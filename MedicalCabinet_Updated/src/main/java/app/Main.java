package app;

import database.DatabaseInitializer;
import javafx.application.Application;
import ui.Dashboard;

public class Main {

    public static void main(String[] args) {

        DatabaseInitializer.initialize();

        Application.launch(Dashboard.class, args);

    }
}