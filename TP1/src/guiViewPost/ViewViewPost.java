package guiViewPost;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import java.util.List;
import java.util.ArrayList;

/**
 * <p> Title: ViewViewPost Class. </p>
 * 
 * <p> Description: View post with replies and add new replies </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-02-07
 */
public class ViewViewPost {
    
    private static double width = 700;
    private static double height = 600;

    protected static Label label_PostTitle = new Label();
    protected static Label label_PostMeta = new Label();
    protected static TextArea text_PostBody = new TextArea();
    
    protected static Label label_Replies = new Label("Replies:");
    protected static ListView<String> list_Replies = new ListView<>();
    protected static ObservableList<String> replyData = FXCollections.observableArrayList();
    
    // Track actual Reply objects for deletion
    protected static List<Reply> currentReplies = new ArrayList<>();
    
    protected static Label label_AddReply = new Label("Add Reply:");
    protected static TextArea text_ReplyBody = new TextArea();
    protected static Button button_PostReply = new Button("Post Reply");
    protected static Button button_DeleteReply = new Button("Delete Selected Reply");
    
    protected static Button button_Return = new Button("Return");

    private static ViewViewPost theView;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    protected static Post thePost;
    private static Scene theScene = null;

    public static void displayViewPost(Stage ps, User user, Post post) {
        theStage = ps;
        theUser = user;
        thePost = post;
        
        if (theView == null) {
            theView = new ViewViewPost();
        }
        
        // Display post details using friend's methods
        label_PostTitle.setText(post.getTitle());
        
        // Friend's methods: getUsername(), getThreadName(), getTimestamp()
        label_PostMeta.setText("By: " + post.getUsername() + 
                              " | Thread: " + (post.getThreadName() != null ? post.getThreadName() : "General") +
                              " | " + guiRole1.ModelRole1Home.getFormattedTimestamp(post));
        
        text_PostBody.setText(post.getBody());
        text_ReplyBody.clear();
        
        // Load replies
        loadReplies();
        
        theStage.setTitle("View Post");
        theStage.setScene(theScene);
        theStage.show();
    }
    
    /**
     * Load replies for the current post
     */
    protected static void loadReplies() {
        replyData.clear();
        currentReplies.clear();
        
        if (thePost.isDeleted()) {
            replyData.add("Original post has been deleted.");
        } else {
            // Get replies from ModelRole1Home using friend's getPostID()
            List<Reply> replies = guiRole1.ModelRole1Home.getRepliesForPost(thePost.getPostID());
            
            for (Reply reply : replies) {
                // Friend's methods: getUsername(), getBody()
                // Format timestamp using helper
                String timestamp = "";
                if (reply.getTimestamp() != null) {
                    java.time.format.DateTimeFormatter formatter = 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    timestamp = reply.getTimestamp().format(formatter);
                }
                
                String replyText = String.format("[%s] %s: %s", 
                    timestamp, 
                    reply.getUsername(), 
                    reply.getBody());
                replyData.add(replyText);
                currentReplies.add(reply);
            }
        }
    }
    
    private ViewViewPost() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);
        
        // Post title (bold)
        label_PostTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label_PostTitle.setLayoutX(20);
        label_PostTitle.setLayoutY(20);
        label_PostTitle.setPrefWidth(660);
        
        // Post metadata (gray)
        setupLabelUI(label_PostMeta, "Arial", 14, 660, Pos.BASELINE_LEFT, 20, 50);
        label_PostMeta.setTextFill(Color.GRAY);
        
        // Post body (read-only)
        text_PostBody.setFont(Font.font("Arial", 14));
        text_PostBody.setLayoutX(20);
        text_PostBody.setLayoutY(80);
        text_PostBody.setPrefWidth(660);
        text_PostBody.setPrefHeight(120);
        text_PostBody.setWrapText(true);
        text_PostBody.setEditable(false);
        
        // Replies section
        setupLabelUI(label_Replies, "Arial", 16, 200, Pos.BASELINE_LEFT, 20, 215);
        
        list_Replies.setItems(replyData);
        list_Replies.setLayoutX(20);
        list_Replies.setLayoutY(245);
        list_Replies.setPrefWidth(660);
        list_Replies.setPrefHeight(150);
        
        // Add reply section
        setupLabelUI(label_AddReply, "Arial", 16, 200, Pos.BASELINE_LEFT, 20, 410);
        
        text_ReplyBody.setFont(Font.font("Arial", 14));
        text_ReplyBody.setLayoutX(20);
        text_ReplyBody.setLayoutY(440);
        text_ReplyBody.setPrefWidth(660);
        text_ReplyBody.setPrefHeight(60);
        text_ReplyBody.setWrapText(true);
        text_ReplyBody.setPromptText("Type your reply here...");
        
        setupButtonUI(button_PostReply, "Dialog", 14, 150, Pos.CENTER, 225, 515);
        button_PostReply.setOnAction((_) -> {ControllerViewPost.performPostReply(); });
        
        setupButtonUI(button_DeleteReply, "Dialog", 14, 200, Pos.CENTER, 425, 515);
        button_DeleteReply.setOnAction((_) -> {ControllerViewPost.performDeleteReply(); });
        
        setupButtonUI(button_Return, "Dialog", 16, 150, Pos.CENTER, 275, 555);
        button_Return.setOnAction((_) -> {ControllerViewPost.performReturn(); });
        
        theRootPane.getChildren().addAll(
            label_PostTitle, label_PostMeta, text_PostBody,
            label_Replies, list_Replies,
            label_AddReply, text_ReplyBody,
            button_PostReply, button_DeleteReply,
            button_Return);
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
    
    /**
     * Show confirmation dialog
     */
    protected static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}