package guiSearchPosts;

import entityClasses.Post;
import guiRole1.ViewRole1Home;

import java.util.List;

/**
 * <p> Title: ControllerSearchPosts Class </p>
 *
 * <p> Description: Controller for Search Posts functionality - handles user actions </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @version 1.00 2026-03-23 Initial implementation for Read & Search functionality
 */
public class ControllerSearchPosts {

    /**
     * Perform search when user clicks Search button
     */
    public static void performSearch() {
        // Get input from text fields
        String keyword = ViewSearchPosts.textField_Keyword.getText().trim();
        String thread = ViewSearchPosts.textField_Thread.getText().trim();

        // Validate keyword
        if (keyword.isEmpty()) {
            ViewSearchPosts.showAlert("Invalid Input", "Please enter a keyword to search.");
            return;
        }

        // If thread is empty, treat as null (search all threads)
        String threadFilter = thread.isEmpty() ? null : thread;

        // Call model to search
        List<Post> results = ModelSearchPosts.search(keyword, threadFilter);

        // Populate results table
        ViewSearchPosts.populateResultsTable(results);

        // Show result count
        if (results.isEmpty()) {
            ViewSearchPosts.showAlert(
                "No Results",
                "No posts found matching '" + keyword + "'" +
                (threadFilter != null ? " in thread '" + threadFilter + "'" : "")
            );
        }
    }

    /**
     * View selected post from results table
     */
    public static void viewSelectedPost() {
        ViewSearchPosts.PostDisplay selected =
            ViewSearchPosts.table_Results.getSelectionModel().getSelectedItem();

        if (selected == null) {
            ViewSearchPosts.showAlert("No Selection", "Please select a post to view.");
            return;
        }

        int postId = selected.getPostId();
        Post post = guiRole1.ModelRole1Home.getPostById(postId);

        if (post == null) {
            ViewSearchPosts.showAlert("Error", "Post not found.");
            return;
        }

        // Open view post dialog
        guiViewPost.ViewViewPost.displayViewPost(
            ViewSearchPosts.theStage,
            ViewSearchPosts.theStage.getTitle(),
            post
        );
    }

    /**
     * Go back to main page
     */
    public static void goBack() {
        // Clear the search fields for next time
        ViewSearchPosts.textField_Keyword.clear();
        ViewSearchPosts.textField_Thread.clear();

        // Return to Role1Home
        guiRole1.ViewRole1Home.displayRole1Home(
            ViewSearchPosts.theStage,
            null
        );
    }
}