package guiViewPost;

import entityClasses.Post;
import entityClasses.Reply;
import guiRole1.ModelRole1Home;
import java.util.List;

/**
 * <p> Title: ModelViewPost Class </p>
 *
 * <p> Description: Model for View Post functionality - holds post and reply data </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @version 1.00 2026-03-23 Initial implementation for Read & Search functionality
 */
public class ModelViewPost {

    private static Post currentPost;
    private static List<Reply> currentReplies;

    /**
     * Load post and its replies
     */
    public static void loadPost(Post post) {
        currentPost = post;
        currentReplies = ModelRole1Home.getRepliesForPost(post.getPostID());
    }

    /**
     * Get current post
     */
    public static Post getPost() {
        return currentPost;
    }

    /**
     * Get replies for current post
     */
    public static List<Reply> getReplies() {
        return currentReplies;
    }

    /**
     * Get post ID
     */
    public static int getPostId() {
        return currentPost != null ? currentPost.getPostID() : -1;
    }
}
