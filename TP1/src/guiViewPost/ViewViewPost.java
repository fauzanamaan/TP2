package guiViewPost;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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
    protected static Button button_EditReply = new Button("Edit Reply");
    protected static Button button_EditPost = new Button("Edit Post");
    
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
        
        label_PostTitle.setText(
            post.getTitle() == null || post.getTitle().isBlank() ? "(No Title)" : post.getTitle()
        );
        
        String formattedTimestamp = "";
        if (post.getTimestamp() != null) {
            java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formattedTimestamp = post.getTimestamp().format(formatter);
        }
        
        String postUser = (post.getUsername() == null || post.getUsername().isBlank())
            ? "Unknown User"
            : post.getUsername();

        String threadName = (post.getThreadName() == null || post.getThreadName().isBlank())
            ? "General"
            : post.getThreadName();
        
        label_PostMeta.setText(
            "By: " + postUser +
            " | Thread: " + threadName +
            " | " + formattedTimestamp
        );
        
        text_PostBody.setText(post.getBody() == null ? "" : post.getBody());
        text_ReplyBody.clear();
        
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
        
        if (thePost == null) {
            return;
        }
        
        if (thePost.isDeleted()) {
            replyData.add("Original post has been deleted.");
        }
        
        List<Reply> replies = new ArrayList<>();
        
        try {
            // Student side
            if (applicationMain.FoundationsMain.activeHomePage == 2) {
                replies = guiRole1.ModelRole1Home.getRepliesForPost(thePost.getPostID());
            }
            // Staff side
            else if (applicationMain.FoundationsMain.activeHomePage == 3) {
                replies = applicationMain.FoundationsMain.database.getRepliesForPost(thePost.getPostID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (replies == null) {
            return;
        }
        
        for (Reply reply : replies) {
            if (reply == null) {
                continue;
            }
            
            String timestamp = "";
            if (reply.getTimestamp() != null) {
                java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                timestamp = reply.getTimestamp().format(formatter);
            }
            
            String replyBody = reply.getBody() == null ? "" : reply.getBody();
            String replyUser = (reply.getUsername() == null || reply.getUsername().isBlank())
                ? "Unknown User"
                : reply.getUsername();
            
            if (reply.isDeleted()) {
                replyBody = "[Deleted Reply]";
            }
            
            String replyText = String.format("[%s] %s: %s",
                timestamp,
                replyUser,
                replyBody);
            
            replyData.add(replyText);
            currentReplies.add(reply);
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
        text_PostBody.setEditable(true);
        
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
        
        setupButtonUI(button_PostReply, "Dialog", 14, 150, Pos.CENTER, 200, 515);
        button_PostReply.setOnAction((e) -> { ControllerViewPost.performPostReply(); });
        
        setupButtonUI(button_DeleteReply, "Dialog", 14, 200, Pos.CENTER, 370, 515);
        button_DeleteReply.setOnAction((e) -> { ControllerViewPost.performDeleteReply(); });
        
        setupButtonUI(button_EditReply, "Dialog", 14, 200, Pos.CENTER, 550, 515);
        button_EditReply.setOnAction((e) -> { ControllerViewPost.performEditReply(); });
        
        setupButtonUI(button_EditPost, "Dialog", 14, 150, Pos.CENTER, 20, 515);
        button_EditPost.setOnAction((e) -> { ControllerViewPost.performEditPost(); });
        
        setupButtonUI(button_Return, "Dialog", 16, 150, Pos.CENTER, 275, 555);
        button_Return.setOnAction((e) -> { ControllerViewPost.performReturn(); });
        
        theRootPane.getChildren().addAll(
            label_PostTitle, label_PostMeta, text_PostBody,
            label_Replies, list_Replies,
            label_AddReply, text_ReplyBody,
            button_PostReply, button_DeleteReply, button_EditReply, button_EditPost,
            button_Return
        );
    }

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }
    
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
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
