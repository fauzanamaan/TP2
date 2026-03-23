package guiViewPost;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.Reply;
import guiRole1.ModelRole1Home;
import java.util.List;

/**
 * <p> Title: ViewViewPost Class </p>
 *
 * <p> Description: View Post GUI - displays individual post with replies </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @version 1.00 2026-03-23 Initial implementation for Read & Search functionality
 */
public class ViewViewPost {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // Post details
    protected static Label label_PostTitle = new Label();
    protected static Label label_Author = new Label();
    protected static Label label_Thread = new Label();
    protected static Label label_Date = new Label();
    protected static TextArea textArea_Body = new TextArea();

    // Replies section
    protected static Label label_RepliesTitle = new Label("Replies:");
    protected static ListView<String> listView_Replies = new ListView<>();

    // Buttons
    protected static Button button_Back = new Button("Back");

    private static ViewViewPost theView;
    protected static Stage theStage;
    protected static Pane theRootPane;
    private static Scene theViewPostScene = null;
    protected static String previousPageType = "main"; // "main" or "search"

    /**
     * Entry point to display view post page
     */
    public static void displayViewPost(Stage ps, String fromPage, Post post) {
        theStage = ps;
        previousPageType = fromPage;

        if (theView == null) {
            theView = new ViewViewPost();
        }

        // Load post and mark as read
        ModelViewPost.loadPost(post);
        database.Database db = applicationMain.FoundationsMain.database;
        db.markPostAsRead(applicationMain.FoundationsMain.currentUsername, post.getPostID());

        // Update display
        populatePostDetails();
        populateReplies();

        theStage.setTitle("CSE 360 Foundations: View Post");
        theStage.setScene(theViewPostScene);
        theStage.show();
    }

    /**
     * Constructor - creates all GUI elements
     */
    private ViewViewPost() {
        theRootPane = new Pane();
        theViewPostScene = new Scene(theRootPane, width, height);

        // Post title
        label_PostTitle.setText("");
        setupLabelUI(label_PostTitle, "Arial", 22, width - 40, Pos.BASELINE_LEFT, 20, 10);

        // Post details (author, thread, date)
        label_Author.setText("");
        setupLabelUI(label_Author, "Arial", 12, 300, Pos.BASELINE_LEFT, 20, 40);

        label_Thread.setText("");
        setupLabelUI(label_Thread, "Arial", 12, 300, Pos.BASELINE_LEFT, 350, 40);

        label_Date.setText("");
        setupLabelUI(label_Date, "Arial", 12, 300, Pos.BASELINE_LEFT, 620, 40);

        // Post body
        textArea_Body.setWrapText(true);
        textArea_Body.setEditable(false);
        textArea_Body.setFont(Font.font("Arial", 12));
        textArea_Body.setLayoutX(20);
        textArea_Body.setLayoutY(70);
        textArea_Body.setPrefWidth(760);
        textArea_Body.setPrefHeight(150);

        // Replies section
        setupLabelUI(label_RepliesTitle, "Arial", 14, 100, Pos.BASELINE_LEFT, 20, 230);

        listView_Replies.setLayoutX(20);
        listView_Replies.setLayoutY(255);
        listView_Replies.setPrefWidth(760);
        listView_Replies.setPrefHeight(200);

        // Back button
        setupButtonUI(button_Back, "Dialog", 16, 180, Pos.CENTER, 20, 470);
        button_Back.setOnAction((_) -> {ControllerViewPost.goBack(); });

        theRootPane.getChildren().addAll(
            label_PostTitle, label_Author, label_Thread, label_Date,
            textArea_Body,
            label_RepliesTitle, listView_Replies,
            button_Back);
    }

    /**
     * Populate post details from model
     */
    private static void populatePostDetails() {
        Post post = ModelViewPost.getPost();
        if (post == null) return;

        label_PostTitle.setText(post.getTitle());
        label_Author.setText("Author: " + post.getUsername());
        label_Thread.setText("Thread: " + post.getThreadName());
        label_Date.setText("Date: " + ModelRole1Home.getFormattedTimestamp(post));
        textArea_Body.setText(post.getBody());
    }

    /**
     * Populate replies list
     */
    private static void populateReplies() {
        listView_Replies.getItems().clear();
        List<Reply> replies = ModelViewPost.getReplies();

        if (replies == null || replies.isEmpty()) {
            listView_Replies.getItems().add("No replies yet.");
            return;
        }

        for (Reply reply : replies) {
            String replyText = reply.getUsername() + ": " + reply.getBody();
            listView_Replies.getItems().add(replyText);
        }
    }

    /**
     * Show alert dialog
     */
    protected static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper methods
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
        l.setWrapText(true);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
