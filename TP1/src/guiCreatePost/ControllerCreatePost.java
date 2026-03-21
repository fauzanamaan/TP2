package guiCreatePost;

import guiRole1.ModelRole1Home;

/**
 * <p> Title: ControllerCreatePost Class. </p>
 * 
 * <p> Description: Controller for creating new posts </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-02-07
 */
public class ControllerCreatePost {
    
    /**
     * Create the post
     */
    protected static void performCreatePost() {
        String title = ViewCreatePost.text_Title.getText();
        String body = ViewCreatePost.text_Body.getText();
        
        // Validation
        if (title == null || title.trim().isEmpty()) {
            showAlert("Validation Error", "Title cannot be empty.");
            return;
        }
        
        if (body == null || body.trim().isEmpty()) {
            showAlert("Validation Error", "Body cannot be empty.");
            return;
        }
        
        // Create the post (thread defaults to "General" in ModelRole1Home)
        boolean success = ModelRole1Home.createPost(title, body, null);
        
        if (success) {
            showAlert("Success", "Post created successfully!");
            
            // Return to student home and refresh
            guiRole1.ViewRole1Home.displayRole1Home(ViewCreatePost.theStage, 
                ViewCreatePost.theUser);
            guiRole1.ControllerRole1Home.loadAllPosts();
        } else {
            showAlert("Error", "Failed to create post.");
        }
    }
    
    /**
     * Cancel and return to home
     */
    protected static void performCancel() {
        guiRole1.ViewRole1Home.displayRole1Home(ViewCreatePost.theStage, 
            ViewCreatePost.theUser);
    }
    
    /**
     * Show alert dialog
     */
    private static void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}