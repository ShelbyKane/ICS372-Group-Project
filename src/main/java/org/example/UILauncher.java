package org.example;

    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.stage.Stage;
    import java.util.ArrayList;

public class UILauncher extends Application {

    private ArrayList<Order> myOrders;

    public UILauncher() {
        myOrders = new ArrayList<Order>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/OrderUI.fxml"));
        Parent root = loader.load();  // FXML creates the controller

        // Get the controller instance created by FXMLLoader
        UIController controller = loader.getController();
        controller.setOrders(myOrders);  // inject your orders list

        // Set up the scene and stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Order Management");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
