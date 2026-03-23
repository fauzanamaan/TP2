package guiRole1;

import entityClasses.Post;
import java.util.List;

/**
 * <p> Title: ControllerRole1Home Class. </p>
 * 
 * <p> Description: Controller for Student Discussion System - handles all user actions </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 2.00 2025-02-07 Updated with discussion system functionality
 */
public class ControllerRole1Home {
    
    /**
     * Load all posts into the table
     */
    public static void loadAllPosts() {
        List<Post> posts = ModelRole1Home.getAllPosts();
        ViewRole1Home.populatePostTable(posts);
    }
    
    /**
     * Load only the current user's posts
     */
    protected static void loadMyPosts() {
        List<Post> posts = ModelRole1Home.getMyPosts();
        ViewRole1Home.populatePostTable(posts);
        
        if (posts.isEmpty()) {
            ViewRole1Home.showAlert("No Posts", "You have not created any posts yet.");
        }
    }
    
    /**
     * Create a new post - opens dialog
     */
    protected static void createNewPost() {
        guiCreatePost.ViewCreatePost.displayCreatePost(ViewRole1Home.theStage, 
            ViewRole1Home.theUser);
    }
    
    /**
     * View selected post and its replies
     */
    protected static void viewPost() {
        ViewRole1Home.PostDisplay selected = 
            ViewRole1Home.table_Posts.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            ViewRole1Home.showAlert("No Selection", 
                "Please select a post to view.");
            return;
        }
        
        int postId = selected.getPostId();
        Post post = ModelRole1Home.getPostById(postId);
        
        if (post == null) {
            ViewRole1Home.showAlert("Error", "Post not found.");
            return;
        }
        
        // Open view post dialog
        guiViewPost.ViewViewPost.displayViewPost(ViewRole1Home.theStage, 
            ViewRole1Home.theUser, post);
    }
    
    /**
     * Edit selected post
     */
    protected static void editPost() {
        ViewRole1Home.PostDisplay selected = 
            ViewRole1Home.table_Posts.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            ViewRole1Home.showAlert("No Selection", 
                "Please select a post to edit.");
            return;
        }
        
        int postId = selected.getPostId();
        Post post = ModelRole1Home.getPostById(postId);
        
        if (post == null) {
            ViewRole1Home.showAlert("Error", "Post not found.");
            return;
        }
        
        // Check if user is the author (using getUsername instead of getAuthor)
        if (!post.getUsername().equals(ModelRole1Home.getCurrentUser())) {
            ViewRole1Home.showAlert("Unauthorized", 
                "You can only edit your own posts.");
            return;
        }
        
        // Open edit post dialog
        guiEditPost.ViewEditPost.displayEditPost(ViewRole1Home.theStage, 
            ViewRole1Home.theUser, post);
    }
    
    /**
     * Delete selected post with confirmation
     */
    protected static void deletePost() {
        ViewRole1Home.PostDisplay selected = 
            ViewRole1Home.table_Posts.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            ViewRole1Home.showAlert("No Selection", 
                "Please select a post to delete.");
            return;
        }
        
        int postId = selected.getPostId();
        Post post = ModelRole1Home.getPostById(postId);
        
        if (post == null) {
            ViewRole1Home.showAlert("Error", "Post not found.");
            return;
        }
        
        // Check if user is the author (using getUsername instead of getAuthor)
        if (!post.getUsername().equals(ModelRole1Home.getCurrentUser())) {
            ViewRole1Home.showAlert("Unauthorized", 
                "You can only delete your own posts.");
            return;
        }
        
        // Confirmation dialog
        boolean confirmed = ViewRole1Home.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete this post?\n\n" +
            "Title: " + post.getTitle() + "\n\n" +
            "Note: Replies to this post will remain visible.");
        
        if (confirmed) {
            boolean success = ModelRole1Home.deletePost(postId);
            
            if (success) {
                ViewRole1Home.showAlert("Success", "Post deleted successfully.");
                loadAllPosts(); // Refresh the table
            } else {
                ViewRole1Home.showAlert("Error", "Failed to delete post.");
            }
        }
    }
    
    /**
     * Logout and return to login page
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
    }
    
    /**
     * Open search posts page
     */
    protected static void searchPosts() {
        guiSearchPosts.ViewSearchPosts.displaySearchPosts(ViewRole1Home.theStage);
    }

    /**
     * Quit the application
     */
    protected static void performQuit() {
        System.exit(0);
    }
}