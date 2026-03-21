package guiViewPost;

import guiRole1.ModelRole1Home;
import entityClasses.Reply;

/**
 * <p> Title: ControllerViewPost Class. </p>
 * 
 * <p> Description: Controller for viewing posts and managing replies </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-02-07
 */
public class ControllerViewPost {
    
    /**
     * Post a new reply
     */
    protected static void performPostReply() {
        String replyBody = ViewViewPost.text_ReplyBody.getText();
        
        // Validation
        if (replyBody == null || replyBody.trim().isEmpty()) {
            showAlert("Validation Error", "Reply body cannot be empty.");
            return;
        }
        
        // Create reply using friend's getPostID() method
        boolean success = ModelRole1Home.createReply(
            ViewViewPost.thePost.getPostID(), 
            replyBody
        );
        
        if (success) {
            showAlert("Success", "Reply posted successfully!");
            
            // Clear the text area
            ViewViewPost.text_ReplyBody.clear();
            
            // Reload replies
            ViewViewPost.loadReplies();
        } else {
            showAlert("Error", "Failed to post reply.");
        }
    }
    
    /**
     * Delete selected reply
     */
    protected static void performDeleteReply() {
        int selectedIndex = ViewViewPost.list_Replies.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            showAlert("No Selection", "Please select a reply to delete.");
            return;
        }
        
        // Get the actual Reply object from currentReplies
        Reply selectedReply = ViewViewPost.currentReplies.get(selectedIndex);
        
        // Check if user owns the reply (using friend's getUsername() method)
        if (!selectedReply.getUsername().equals(ModelRole1Home.getCurrentUser())) {
            showAlert("Unauthorized", "You can only delete your own replies.");
            return;
        }
        
        // Confirmation dialog
        boolean confirmed = ViewViewPost.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete this reply?");
        
        if (confirmed) {
            // Delete the reply
            boolean success = ModelRole1Home.deleteReply(selectedReply);
            
            if (success) {
                showAlert("Success", "Reply deleted successfully.");
                
                // Reload replies
                ViewViewPost.loadReplies();
            } else {
                showAlert("Error", "Failed to delete reply.");
            }
        }
    }
    
    /**
     * Return to student home
     */
    protected static void performReturn() {
        guiRole1.ViewRole1Home.displayRole1Home(ViewViewPost.theStage, 
            ViewViewPost.theUser);
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