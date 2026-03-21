package guiEditPost;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import entityClasses.Post;

/**
 * <p> Title: ViewEditPost Class. </p>
 * 
 * <p> Description: Edit existing discussion post </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-02-07
 */
public class ViewEditPost {
    
    private static double width = 500;
    private static double height = 450;

    protected static Label label_PostInfo = new Label();
    
    protected static Label label_Title = new Label("Title:");
    protected static TextField text_Title = new TextField();
    
    protected static Label label_Body = new Label("Body:");
    protected static TextArea text_Body = new TextArea();
    
    protected static Button button_Save = new Button("Save Changes");
    protected static Button button_Cancel = new Button("Cancel");

    private static ViewEditPost theView;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    protected static Post thePost;
    private static Scene theScene = null;

    public static void displayEditPost(Stage ps, User user, Post post) {
        theStage = ps;
        theUser = user;
        thePost = post;
        
        if (theView == null) {
            theView = new ViewEditPost();
        }
        
        // Pre-populate fields with existing data
        // Friend's methods: getPostID(), getThreadName(), getTimestamp()
        label_PostInfo.setText("Post ID: " + post.getPostID() + 
                              " | Thread: " + (post.getThreadName() != null ? post.getThreadName() : "General") +
                              " | " + guiRole1.ModelRole1Home.getFormattedTimestamp(post));
        
        text_Title.setText(post.getTitle());
        text_Body.setText(post.getBody());
        
        theStage.setTitle("Edit Post");
        theStage.setScene(theScene);
        theStage.show();
    }
    
    private ViewEditPost() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);
        
        setupLabelUI(label_PostInfo, "Arial", 14, 460, Pos.BASELINE_LEFT, 20, 20);
        label_PostInfo.setTextFill(Color.GRAY);
        
        setupLabelUI(label_Title, "Arial", 18, 80, Pos.BASELINE_LEFT, 20, 60);
        setupTextFieldUI(text_Title, "Arial", 16, 380, Pos.BASELINE_LEFT, 100, 60);
        
        setupLabelUI(label_Body, "Arial", 18, 80, Pos.BASELINE_LEFT, 20, 110);
        text_Body.setFont(Font.font("Arial", 16));
        text_Body.setLayoutX(20);
        text_Body.setLayoutY(140);
        text_Body.setPrefWidth(460);
        text_Body.setPrefHeight(200);
        text_Body.setWrapText(true);
        
        setupButtonUI(button_Save, "Dialog", 16, 150, Pos.CENTER, 60, 370);
        button_Save.setOnAction((_) -> {ControllerEditPost.performSave(); });
        
        setupButtonUI(button_Cancel, "Dialog", 16, 150, Pos.CENTER, 290, 370);
        button_Cancel.setOnAction((_) -> {ControllerEditPost.performCancel(); });
        
        theRootPane.getChildren().addAll(
            label_PostInfo,
            label_Title, text_Title,
            label_Body, text_Body,
            button_Save, button_Cancel);
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