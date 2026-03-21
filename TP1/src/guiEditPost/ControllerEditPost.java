package guiEditPost;

import guiRole1.ModelRole1Home;

/**
 * <p> Title: ControllerEditPost Class. </p>
 * 
 * <p> Description: Controller for editing posts </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-02-07
 */
public class ControllerEditPost {
    
    /**
     * Save the edited post
     */
    protected static void performSave() {
        String newTitle = ViewEditPost.text_Title.getText();
        String newBody = ViewEditPost.text_Body.getText();
        
        // Validation
        if (newTitle == null || newTitle.trim().isEmpty()) {
            showAlert("Validation Error", "Title cannot be empty.");
            return;
        }
        
        if (newBody == null || newBody.trim().isEmpty()) {
            showAlert("Validation Error", "Body cannot be empty.");
            return;
        }
        
        // Update the post using friend's getPostID() method
        boolean success = ModelRole1Home.updatePost(
            ViewEditPost.thePost.getPostID(), 
            newTitle, 
            newBody
        );
        
        if (success) {
            showAlert("Success", "Post updated successfully!");
            
            // Return to student home and refresh
            guiRole1.ViewRole1Home.displayRole1Home(ViewEditPost.theStage, 
                ViewEditPost.theUser);
            guiRole1.ControllerRole1Home.loadAllPosts();
        } else {
            showAlert("Error", "Failed to update post.");
        }
    }
    
    /**
     * Cancel and return to home
     */
    protected static void performCancel() {
        guiRole1.ViewRole1Home.displayRole1Home(ViewEditPost.theStage, 
            ViewEditPost.theUser);
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