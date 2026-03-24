package guiViewPost;

import guiRole1.ViewRole1Home;
import guiSearchPosts.ViewSearchPosts;
import guiSearchPosts.ModelSearchPosts;
import java.util.List;
import entityClasses.Post;

/**
 * <p> Title: ControllerViewPost Class </p>
 *
 * <p> Description: Controller for View Post functionality - handles user actions </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @version 1.00 2026-03-23 Initial implementation for Read & Search functionality
 */
public class ControllerViewPost {

    /**
     * Go back to previous page (main or search results)
     */
    public static void goBack() {
        // Determine which page to return to
        if (ViewViewPost.previousPageType.equals("search")) {
            // Restore saved search results
            List<Post> savedResults = ModelSearchPosts.getLastSearchResults();
            ViewSearchPosts.populateResultsTable(savedResults);

            // Return to search results page
            ViewSearchPosts.displaySearchPosts(ViewViewPost.theStage);
        } else {
            // Default: return to main Role1Home page
            ViewRole1Home.displayRole1Home(ViewViewPost.theStage, null);
        }
    }
}
