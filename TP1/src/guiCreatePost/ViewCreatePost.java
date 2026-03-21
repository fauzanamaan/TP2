package guiCreatePost;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

/**
 * <p> Title: ViewCreatePost Class. </p>
 * 
 * <p> Description: Create new discussion post </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-02-07
 */
public class ViewCreatePost {
    
    private static double width = 500;
    private static double height = 400;

    protected static Label label_Title = new Label("Title:");
    protected static TextField text_Title = new TextField();
    
    protected static Label label_Body = new Label("Body:");
    protected static TextArea text_Body = new TextArea();
    
    protected static Button button_Create = new Button("Create Post");
    protected static Button button_Cancel = new Button("Cancel");

    private static ViewCreatePost theView;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    private static Scene theScene = null;

    public static void displayCreatePost(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        
        if (theView == null) {
            theView = new ViewCreatePost();
        }
        
        // Clear fields
        text_Title.clear();
        text_Body.clear();
        
        theStage.setTitle("Create New Post");
        theStage.setScene(theScene);
        theStage.show();
    }
    
    private ViewCreatePost() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);
        
        setupLabelUI(label_Title, "Arial", 18, 80, Pos.BASELINE_LEFT, 20, 30);
        setupTextFieldUI(text_Title, "Arial", 16, 380, Pos.BASELINE_LEFT, 100, 30);
        
        setupLabelUI(label_Body, "Arial", 18, 80, Pos.BASELINE_LEFT, 20, 80);
        text_Body.setFont(Font.font("Arial", 16));
        text_Body.setLayoutX(20);
        text_Body.setLayoutY(110);
        text_Body.setPrefWidth(460);
        text_Body.setPrefHeight(180);
        text_Body.setWrapText(true);
        
        setupButtonUI(button_Create, "Dialog", 16, 150, Pos.CENTER, 60, 320);
        button_Create.setOnAction((_) -> {ControllerCreatePost.performCreatePost(); });
        
        setupButtonUI(button_Cancel, "Dialog", 16, 150, Pos.CENTER, 290, 320);
        button_Cancel.setOnAction((_) -> {ControllerCreatePost.performCancel(); });
        
        theRootPane.getChildren().addAll(
            label_Title, text_Title,
            label_Body, text_Body,
            button_Create, button_Cancel);
    }

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }
    
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);        
    }
    
    private static void setupTextFieldUI(TextField t, String ff, double f, double w, Pos p, double x, double y){
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
    }
}