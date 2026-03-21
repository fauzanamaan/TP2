package guiRole1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import entityClasses.Post;
import java.util.List;

/**
 * <p> Title: ViewRole1Home Class. </p>
 * 
 * <p> Description: Student Discussion System Home Page with CRUD operations for posts and replies </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 2.00 2025-02-07 Updated with discussion system functionality
 */
public class ViewRole1Home {
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // Page title and user info
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");
    
    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
    
    // Action buttons
    protected static Button button_CreatePost = new Button("Create New Post");
    protected static Button button_ViewAllPosts = new Button("View All Posts");
    protected static Button button_ViewMyPosts = new Button("View My Posts");
    
    protected static Line line_Separator2 = new Line(160, 210, width-20, 160);
    
    // Post table
    protected static TableView<PostDisplay> table_Posts = new TableView<>();
    protected static ObservableList<PostDisplay> postData = FXCollections.observableArrayList();
    
    protected static Line line_Separator3 = new Line(20, 480, width-20, 480);
    
    // Post action buttons
    protected static Button button_ViewPost = new Button("View Post & Replies");
    protected static Button button_EditPost = new Button("Edit Post");
    protected static Button button_DeletePost = new Button("Delete Post");
    
    protected static Line line_Separator4 = new Line(20, 525, width-20, 525);
    
    // Navigation
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    private static ViewRole1Home theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    private static Scene theViewRole1HomeScene = null;
    protected static final int theRole = 2;

    /**
     * Entry point to display student home page
     */
    public static void displayRole1Home(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        
        if (theView == null) {
            theView = new ViewRole1Home();
        }
        
        // Initialize the model with current user
        ModelRole1Home.initialize(theUser.getUserName());
        
        // Load all posts by default
        ControllerRole1Home.loadAllPosts();
        
        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;
        
        label_UserDetails.setText("User: " + theUser.getUserName());
        
        theStage.setTitle("CSE 360 Foundations: Student Discussion System");
        theStage.setScene(theViewRole1HomeScene);
        theStage.show();
    }
    
    /**
     * Constructor - creates all GUI elements
     */
    private ViewRole1Home() {
        theRootPane = new Pane();
        theViewRole1HomeScene = new Scene(theRootPane, width, height);
        
        // Page title
        label_PageTitle.setText("Student Discussion System");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: ");
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
        
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((_) -> 
            {guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
        
        // Action buttons
        setupButtonUI(button_CreatePost, "Dialog", 16, 150, Pos.CENTER, 20, 110);
        button_CreatePost.setOnAction((_) -> {ControllerRole1Home.createNewPost(); });
        
        setupButtonUI(button_ViewAllPosts, "Dialog", 16, 150, Pos.CENTER, 190, 110);
        button_ViewAllPosts.setOnAction((_) -> {ControllerRole1Home.loadAllPosts(); });
        
        setupButtonUI(button_ViewMyPosts, "Dialog", 16, 150, Pos.CENTER, 360, 110);
        button_ViewMyPosts.setOnAction((_) -> {ControllerRole1Home.loadMyPosts(); });
        
        // Table
        setupTableView();
        
        // Post actions
        setupButtonUI(button_ViewPost, "Dialog", 16, 180, Pos.CENTER, 20, 495);
        button_ViewPost.setOnAction((_) -> {ControllerRole1Home.viewPost(); });
        
        setupButtonUI(button_EditPost, "Dialog", 16, 150, Pos.CENTER, 220, 495);
        button_EditPost.setOnAction((_) -> {ControllerRole1Home.editPost(); });
        
        setupButtonUI(button_DeletePost, "Dialog", 16, 150, Pos.CENTER, 390, 495);
        button_DeletePost.setOnAction((_) -> {ControllerRole1Home.deletePost(); });
        
        // Navigation
        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((_) -> {ControllerRole1Home.performLogout(); });
    
        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> {ControllerRole1Home.performQuit(); });
        
        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
            button_CreatePost, button_ViewAllPosts, button_ViewMyPosts,
            line_Separator2,
            table_Posts, line_Separator3,
            button_ViewPost, button_EditPost, button_DeletePost, line_Separator4,
            button_Logout, button_Quit);
    }

    /**
     * Setups the TableView with columns
     */
    private void setupTableView() {
        TableColumn<PostDisplay, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("postId"));
        idCol.setPrefWidth(50);
        
        TableColumn<PostDisplay, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(350);
        
        TableColumn<PostDisplay, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(120);
        
        TableColumn<PostDisplay, Integer> repliesCol = new TableColumn<>("Replies");
        repliesCol.setCellValueFactory(new PropertyValueFactory<>("replyCount"));
        repliesCol.setPrefWidth(80);
        
        TableColumn<PostDisplay, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        dateCol.setPrefWidth(160);
        
        table_Posts.getColumns().addAll(idCol, titleCol, authorCol, repliesCol, dateCol);
        table_Posts.setItems(postData);
        
        table_Posts.setLayoutX(20);
        table_Posts.setLayoutY(175);
        table_Posts.setPrefWidth(760);
        table_Posts.setPrefHeight(290);
    }
    
    /**
     * Populates the table with posts
     */
    protected static void populatePostTable(List<Post> posts) {
        postData.clear();
        for (Post post : posts) {
            if (!post.isDeleted()) {
                postData.add(new PostDisplay(post));
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

    // Helper methods
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
    
    private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y){
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
    }
    
    /**
     * Inner class for TableView display
     * ADAPTED FOR FRIEND'S POST CLASS
     */
    public static class PostDisplay {
        private final javafx.beans.property.SimpleIntegerProperty postId;
        private final javafx.beans.property.SimpleStringProperty title;
        private final javafx.beans.property.SimpleStringProperty author;
        private final javafx.beans.property.SimpleIntegerProperty replyCount;
        private final javafx.beans.property.SimpleStringProperty timestamp;
        
        public PostDisplay(Post post) {
            // Friend's methods: getPostID(), getUsername(), getTitle()
            this.postId = new javafx.beans.property.SimpleIntegerProperty(post.getPostID());
            this.title = new javafx.beans.property.SimpleStringProperty(post.getTitle());
            this.author = new javafx.beans.property.SimpleStringProperty(post.getUsername());
            
            // Reply count tracked in ModelRole1Home
            this.replyCount = new javafx.beans.property.SimpleIntegerProperty(
                ModelRole1Home.getReplyCount(post.getPostID()));
            
            // Formatted timestamp from ModelRole1Home helper
            this.timestamp = new javafx.beans.property.SimpleStringProperty(
                ModelRole1Home.getFormattedTimestamp(post));
        }
        
        public int getPostId() { return postId.get(); }
        public String getTitle() { return title.get(); }
        public String getAuthor() { return author.get(); }
        public int getReplyCount() { return replyCount.get(); }
        public String getTimestamp() { return timestamp.get(); }
    }
}