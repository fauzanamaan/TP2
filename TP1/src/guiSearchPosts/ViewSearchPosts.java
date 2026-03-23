package guiSearchPosts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import entityClasses.Post;
import guiRole1.ModelRole1Home;

import java.util.List;

/**
 * <p> Title: ViewSearchPosts Class </p>
 *
 * <p> Description: Search Posts GUI - allows user to search posts by keyword
 * and thread </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @version 1.00 2026-03-23 Initial implementation for Read & Search functionality
 */
public class ViewSearchPosts {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // Page title
    protected static Label label_PageTitle = new Label();

    // Search fields
    protected static Label label_Keyword = new Label("Keyword:");
    protected static TextField textField_Keyword = new TextField();
    protected static Label label_Thread = new Label("Thread (optional):");
    protected static TextField textField_Thread = new TextField();
    protected static Button button_Search = new Button("Search");

    // Results table
    protected static TableView<PostDisplay> table_Results = new TableView<>();
    protected static ObservableList<PostDisplay> resultData = FXCollections.observableArrayList();

    // Action buttons
    protected static Button button_ViewPost = new Button("View Post & Replies");
    protected static Button button_Back = new Button("Back");

    private static ViewSearchPosts theView;
    protected static Stage theStage;
    protected static Pane theRootPane;
    private static Scene theSearchScene = null;

    /**
     * Entry point to display search posts page
     */
    public static void displaySearchPosts(Stage ps) {
        theStage = ps;

        if (theView == null) {
            theView = new ViewSearchPosts();
        }

        theStage.setTitle("CSE 360 Foundations: Search Posts");
        theStage.setScene(theSearchScene);
        theStage.show();
    }

    /**
     * Constructor - creates all GUI elements
     */
    private ViewSearchPosts() {
        theRootPane = new Pane();
        theSearchScene = new Scene(theRootPane, width, height);

        // Page title
        label_PageTitle.setText("Search Posts");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // Search section
        setupLabelUI(label_Keyword, "Arial", 14, 80, Pos.BASELINE_LEFT, 20, 60);
        setupTextFieldUI(textField_Keyword, "Arial", 14, 200, Pos.BASELINE_LEFT, 100, 55);

        setupLabelUI(label_Thread, "Arial", 14, 150, Pos.BASELINE_LEFT, 330, 60);
        setupTextFieldUI(textField_Thread, "Arial", 14, 200, Pos.BASELINE_LEFT, 480, 55);

        setupButtonUI(button_Search, "Dialog", 14, 100, Pos.CENTER, 700, 50);
        button_Search.setOnAction((_) -> {
            ControllerSearchPosts.performSearch();
        });

        // Results table
        setupTableView();

        // Action buttons
        setupButtonUI(button_ViewPost, "Dialog", 16, 180, Pos.CENTER, 20, 495);
        button_ViewPost.setOnAction((_) -> {
            ControllerSearchPosts.viewSelectedPost();
        });

        setupButtonUI(button_Back, "Dialog", 16, 180, Pos.CENTER, 220, 495);
        button_Back.setOnAction((_) -> {
            ControllerSearchPosts.goBack();
        });

        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_Keyword, textField_Keyword,
            label_Thread, textField_Thread, button_Search,
            table_Results,
            button_ViewPost, button_Back
        );
    }

    /**
     * Setup the TableView with columns
     */
    private void setupTableView() {
        TableColumn<PostDisplay, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("postId"));
        idCol.setPrefWidth(50);

        TableColumn<PostDisplay, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);

        TableColumn<PostDisplay, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(100);

        TableColumn<PostDisplay, String> threadCol = new TableColumn<>("Thread");
        threadCol.setCellValueFactory(new PropertyValueFactory<>("thread"));
        threadCol.setPrefWidth(80);

        TableColumn<PostDisplay, Integer> repliesCol = new TableColumn<>("Replies");
        repliesCol.setCellValueFactory(new PropertyValueFactory<>("replyCount"));
        repliesCol.setPrefWidth(60);

        TableColumn<PostDisplay, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<PostDisplay, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dateCol.setPrefWidth(140);

        table_Results.getColumns().addAll(
            idCol, titleCol, authorCol, threadCol, repliesCol, statusCol, dateCol
        );

        table_Results.setItems(resultData);
        table_Results.setLayoutX(20);
        table_Results.setLayoutY(120);
        table_Results.setPrefWidth(760);
        table_Results.setPrefHeight(350);
    }

    /**
     * Populates the table with search results
     */
    protected static void populateResultsTable(List<Post> posts) {
        resultData.clear();
        for (Post post : posts) {
            if (!post.isDeleted()) {
                resultData.add(new PostDisplay(post));
            }
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
    private static void setupLabelUI(Label l, String ff, double f, double w,
                                     Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w,
                                      Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    private static void setupTextFieldUI(TextField t, String ff, double f,
                                         double w, Pos p, double x, double y) {
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
    }

    /**
     * Inner class for TableView display
     */
    public static class PostDisplay {
        private final javafx.beans.property.SimpleIntegerProperty postId;
        private final javafx.beans.property.SimpleStringProperty title;
        private final javafx.beans.property.SimpleStringProperty author;
        private final javafx.beans.property.SimpleStringProperty thread;
        private final javafx.beans.property.SimpleIntegerProperty replyCount;
        private final javafx.beans.property.SimpleStringProperty status;
        private final javafx.beans.property.SimpleStringProperty timestamp;

        public PostDisplay(Post post) {
            this.postId = new javafx.beans.property.SimpleIntegerProperty(post.getPostID());
            this.title = new javafx.beans.property.SimpleStringProperty(post.getTitle());
            this.author = new javafx.beans.property.SimpleStringProperty(post.getUsername());
            this.thread = new javafx.beans.property.SimpleStringProperty(post.getThreadName());
            this.replyCount = new javafx.beans.property.SimpleIntegerProperty(
                ModelRole1Home.getReplyCount(post.getPostID())
            );
            this.status = new javafx.beans.property.SimpleStringProperty(
                ModelRole1Home.isRead(post.getPostID()) ? "READ" : "UNREAD"
            );
            this.timestamp = new javafx.beans.property.SimpleStringProperty(
                ModelRole1Home.getFormattedTimestamp(post)
            );
        }

        public int getPostId() { return postId.get(); }
        public String getTitle() { return title.get(); }
        public String getAuthor() { return author.get(); }
        public String getThread() { return thread.get(); }
        public int getReplyCount() { return replyCount.get(); }
        public String getStatus() { return status.get(); }
        public String getTimestamp() { return timestamp.get(); }
    }
}