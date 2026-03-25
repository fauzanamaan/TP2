package guiViewPost;

import guiRole1.ViewRole1Home;
import guiSearchPosts.ViewSearchPosts;
import guiSearchPosts.ModelSearchPosts;
import entityClasses.Post;
import entityClasses.Reply;

import java.util.List;

/**
 * Controller for View Post functionality (Replies + Navigation)
 */
public class ControllerViewPost {

    /**
     * 🔹 BACK BUTTON (Search OR Home)
     */
    public static void goBack() {
        if (ViewViewPost.previousPageType.equals("search")) {
            List<Post> savedResults = ModelSearchPosts.getLastSearchResults();
            ViewSearchPosts.populateResultsTable(savedResults);
            ViewSearchPosts.displaySearchPosts(ViewViewPost.theStage);
        } else {
            ViewRole1Home.displayRole1Home(ViewViewPost.theStage, null);
        }
    }
    
    /**
     * 🔹 EDIT REPLY
     */
    protected static void performEditReply() {
    	int selectedIndex = ViewViewPost.list_Replies.getSelectionModel().getSelectedIndex();
    	if (selectedIndex < 0) {
    		showAlert("No selection", " Select a repl first");
    		return;
    	}
    	
    	Reply selectedReply = ViewViewPost.currentReplies.get(selectedIndex);
    	String currentUser = ViewViewPost.theUser.getUserName();
    	
    	if (!selectedReply.getUsername().equals(currentUser)) {
    		showAlert("Error", "You do not own this reply");
    		return;
    	}
    	
    	String newBody = ViewViewPost.text_ReplyBody.getText();
    	
    	if (newBody == null || newBody.trim().isEmpty()) {
    		showAlert("Validation Error", "Reply body cannot be empty");
    		return;
    	}
    	
    	boolean success = applicationMain.FoundationsMain.database.editPost(selectedReply.getPostID(), null, newBody.trim(), currentUser);
    	if (success) {
    		showAlert("Success", "Reply edited");
    		ViewViewPost.text_ReplyBody.clear();
    		ViewViewPost.loadReplies();
    	} else {
    		showAlert("Error", "Failed to edit reply");
    	}
    }
    
    /**
     * 🔹 EDIT POST
     */
    protected static void  performEditPost() {
    	Post post = ViewViewPost.thePost;
    	String currentUser = ViewViewPost.theUser.getUserName();
    	
    	if (!post.getUsername().equals(currentUser)) {
    		showAlert("Error", "You do not own this post");
    		return;
    	}
    	
    	String newBody = ViewViewPost.text_PostBody.getText();
    	if (newBody == null || newBody.trim().isEmpty()) {
    		showAlert("Validation Error", "Post body cannot be empty");
    		return;
    	}
    	
    	
    	boolean success = applicationMain.FoundationsMain.database.editPost(post.getPostID(), post.getTitle(), newBody, currentUser);
    	if (success) {
    		showAlert("Success", "Post was edited");
    	} else {
    		showAlert("Error", "Failed to edit post");
    	}
    }
    
    /**
     * 🔹 POST REPLY
     */
    protected static void performPostReply() {

        String replyBody = ViewViewPost.text_ReplyBody.getText();

        if (replyBody == null || replyBody.trim().isEmpty()) {
            showAlert("Validation Error", "Reply body cannot be empty.");
            return;
        }

        if (ViewViewPost.thePost == null) {
            showAlert("Error", "No post selected.");
            return;
        }

        if (ViewViewPost.theUser == null ||
            ViewViewPost.theUser.getUserName() == null ||
            ViewViewPost.theUser.getUserName().isBlank()) {
            showAlert("Error", "No valid user logged in.");
            return;
        }

        boolean success = false;

        // ROLE 1 (Student)
        if (applicationMain.FoundationsMain.activeHomePage == 2) {

            success = guiRole1.ModelRole1Home.createReply(
                ViewViewPost.thePost.getPostID(),
                replyBody.trim()
            );
        }

        // ROLE 2 (Staff)
        else if (applicationMain.FoundationsMain.activeHomePage == 3) {

            try {
                String threadName = ViewViewPost.thePost.getThreadName();
                if (threadName == null || threadName.isBlank()) {
                    threadName = "General";
                }

                applicationMain.FoundationsMain.database.createReply(
                    ViewViewPost.theUser.getUserName(),
                    replyBody.trim(),
                    "",
                    threadName,
                    ViewViewPost.thePost.getPostID()
                );

                success = true;

            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }

        else {
            showAlert("Error", "Unknown role.");
            return;
        }

        if (success) {
            showAlert("Success", "Reply posted!");
            ViewViewPost.text_ReplyBody.clear();
            ViewViewPost.loadReplies();
        } else {
            showAlert("Error", "Failed to post reply.");
        }
    }

    /**
     * 🔹 DELETE REPLY (FIXED WITH DATABASE)
     */
    protected static void performDeleteReply() {

        int selectedIndex = ViewViewPost.list_Replies.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            showAlert("No Selection", "Select a reply first.");
            return;
        }

        if (selectedIndex >= ViewViewPost.currentReplies.size()) {
            showAlert("Error", "Reply not found.");
            return;
        }

        Reply selectedReply = ViewViewPost.currentReplies.get(selectedIndex);
        String currentUser = ViewViewPost.theUser.getUserName();

        if (!selectedReply.getUsername().equals(currentUser)) {
            showAlert("Unauthorized", "You can only delete your own replies.");
            return;
        }

        boolean confirmed = ViewViewPost.showConfirmation(
            "Confirm Delete",
            "Delete this reply?"
        );

        if (!confirmed) return;

        boolean success = false;

        // ROLE 1
        if (applicationMain.FoundationsMain.activeHomePage == 2) {

            success = guiRole1.ModelRole1Home.deleteReply(selectedReply);
        }

        // ROLE 2 ✅ FIXED HERE
        else if (applicationMain.FoundationsMain.activeHomePage == 3) {

            success = applicationMain.FoundationsMain.database
                    .softDeletePostById(selectedReply.getPostID());
        }

        if (success) {
            showAlert("Success", "Reply deleted.");
            ViewViewPost.loadReplies();
        } else {
            showAlert("Error", "Delete failed.");
        }
    }

    /**
     * 🔹 RETURN BUTTON
     */
    protected static void performReturn() {

        if (applicationMain.FoundationsMain.activeHomePage == 3) {

            guiRole2.ViewRole2Home.displayRole2Home(
                ViewViewPost.theStage,
                ViewViewPost.theUser
            );

        } else {

            ViewRole1Home.displayRole1Home(
                ViewViewPost.theStage,
                ViewViewPost.theUser
            );
        }
    }

    /**
     * 🔹 ALERT HELPER
     */
    private static void showAlert(String title, String message) {

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
