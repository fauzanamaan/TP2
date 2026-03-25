package guiRole1;

import entityClasses.Post;
import entityClasses.Reply;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;

/**
 * <p> Title: ModelRole1Home Class </p>
 *
 * <p> Description: Model for Student Discussion System - manages all posts and replies in memory </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 * @version 2.00 2025-02-07 Updated with discussion system functionality
 */
public class ModelRole1Home {

    // In-memory storage (static so data persists across page navigations)
    private static List<Post> allPosts = new ArrayList<>();
    private static List<Reply> allReplies = new ArrayList<>();
    
    // Track replies per post (since Post class doesn't have reply management)
    private static Map<Integer, List<Reply>> postReplies = new HashMap<>();
    
    private static String currentUser = "";
    private static int nextPostId = 1;
    private static int nextReplyId = 1;

    // Real database connection
    private static Database database = new Database();
    private static boolean databaseReady = false;

    /**
     * Initializes the model with current user
     */
    public static void initialize(String username) {
        currentUser = username;
        
        if (!databaseReady) {
            try {
                database.connectToDatabase();
                databaseReady = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the current logged-in user
     */
    public static String getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets all posts
     */
    public static List<Post> getAllPosts() {
        if (databaseReady) {
            try {
                allPosts = database.getAllPosts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(allPosts);
    }

    /**
     * Gets a specific post by ID
     */
    public static Post getPostById(int postId) {
        if (databaseReady) {
            try {
                return database.getPostByID(postId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Post post : allPosts) {
            if (post.getPostID() == postId) {
                return post;
            }
        }
        return null;
    }

    /**
     * Gets posts by current user
     */
    public static List<Post> getMyPosts() {
        List<Post> myPosts = new ArrayList<>();
        
        List<Post> sourcePosts = getAllPosts();
        for (Post post : sourcePosts) {
            if (post.getUsername() != null && post.getUsername().equals(currentUser)) {
                myPosts.add(post);
            }
        }
        return myPosts;
    }

    /**
     * Creates a new post
     */
    public static boolean createPost(String title, String body, String thread) {
        // Input validation
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        if (body == null || body.trim().isEmpty()) {
            return false;
        }

        try {
            if (databaseReady) {
                Post newPost = database.createPost(
                    currentUser,
                    title.trim(),
                    body.trim(),
                    "",
                    (thread == null || thread.isEmpty()) ? "General" : thread
                );

                allPosts = database.getAllPosts();
                postReplies.put(newPost.getPostID(), new ArrayList<>());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Fallback in-memory logic
        Post newPost = new Post(currentUser, title.trim(), body.trim(), "",
                               (thread == null || thread.isEmpty()) ? "General" : thread);
        
        try {
            newPost.setPostID(nextPostId++);
        } catch (Exception e) {
            // If not implemented yet, continue
        }
        
        allPosts.add(newPost);
        postReplies.put(newPost.getPostID(), new ArrayList<>());
        return true;
    }

    /**
     * Updates a post
     */
    public static boolean updatePost(int postId, String newTitle, String newBody) {
        Post post = getPostById(postId);
        if (post == null) {
            return false;
        }
        if (!post.getUsername().equals(currentUser)) {
            return false; // User can only update their own posts
        }
        if (newTitle == null || newTitle.trim().isEmpty() || 
            newBody == null || newBody.trim().isEmpty()) {
            return false;
        }
        
        post.setTitle(newTitle);
        post.setBody(newBody);
        return true;
    }

    /**
     * Deletes a post
     */
    public static boolean deletePost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return false;
        }
        if (!post.getUsername().equals(currentUser)) {
            return false;
        }
        
        post.changeDelete(); // Friend's method instead of markAsDeleted()
        return true;
    }

    /**
     * Creates a reply to a post
     */
    public static boolean createReply(int postId, String body) {
        // Input validation
        if (body == null || body.trim().isEmpty()) {
            return false;
        }

        Post post = getPostById(postId);
        if (post == null) {
            return false;
        }

        try {
            if (databaseReady) {
                Reply newReply = database.createReply(
                    currentUser,
                    body.trim(),
                    "",
                    post.getThreadName(),
                    postId
                );

                allReplies.add(newReply);

                if (!postReplies.containsKey(postId)) {
                    postReplies.put(postId, new ArrayList<>());
                }
                postReplies.get(postId).add(newReply);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Fallback in-memory logic
        Reply newReply = new Reply(postId, currentUser, body.trim());
        
        allReplies.add(newReply);
        
        if (!postReplies.containsKey(postId)) {
            postReplies.put(postId, new ArrayList<>());
        }
        postReplies.get(postId).add(newReply);
        
        return true;
    }

    /**
     * Updates a reply
     */
    public static boolean updateReply(int replyId, String newBody) {
        // Find reply by iterating (since no getReplyById in their code)
        Reply reply = null;
        for (Reply r : allReplies) {
            if (r.getUsername().equals(currentUser)) {
                reply = r;
                break;
            }
        }
        
        if (reply == null) {
            return false;
        }
        if (newBody == null || newBody.trim().isEmpty()) {
            return false;
        }
        
        reply.setBody(newBody);
        return true;
    }

    /**
     * Deletes a reply
     */
    public static boolean deleteReply(Reply replyToDelete) {
        if (replyToDelete == null) {
            return false;
        }
        if (!replyToDelete.getUsername().equals(currentUser)) {
            return false;
        }
        
        allReplies.remove(replyToDelete);
        
        // Remove from post tracking
        int postId = replyToDelete.getParentPostID();
        if (postReplies.containsKey(postId)) {
            postReplies.get(postId).remove(replyToDelete);
        }
        
        return true;
    }

    /**
     * Gets replies for a specific post
     */
    public static List<Reply> getRepliesForPost(int postId) {
        if (databaseReady) {
            try {
                return database.getRepliesForPost(postId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (postReplies.containsKey(postId)) {
            return new ArrayList<>(postReplies.get(postId));
        }
        return new ArrayList<>();
    }
    
    /**
     * Get reply count for a post
     */
    public static int getReplyCount(int postId) {
        return getRepliesForPost(postId).size();
    }
    
    /**
     * Helper: Get formatted timestamp
     */
    public static String getFormattedTimestamp(Post post) {
        if (post.getTimestamp() == null) {
            return "";
        }
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return post.getTimestamp().format(formatter);
    }
}
