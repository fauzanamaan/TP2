package guiRole1;

import entityClasses.Post;
import entityClasses.Reply;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Initializes the model with current user
     */
    public static void initialize(String username) {
        currentUser = username;
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
        return new ArrayList<>(allPosts);
    }

    /**
     * Gets a specific post by ID
     */
    public static Post getPostById(int postId) {
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
        for (Post post : allPosts) {
            if (post.getUsername().equals(currentUser)) {
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

        // Friend's constructor: Post(username, title, body, keywords, threadName)
        Post newPost = new Post(currentUser, title.trim(), body.trim(), "", 
                               (thread == null || thread.isEmpty()) ? "General" : thread);
        
        // Manually set the ID since their setPostID is a stub
        // This will be implemented by your teammates
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

        // Friend's constructor: Reply(parentPostID, authorUserName, body)
        Reply newReply = new Reply(postId, currentUser, body.trim());
        
        // Manually set ID if their implementation doesn't do it
        // This will be handled by teammates later
        
        allReplies.add(newReply);
        
        // Track reply for this post
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
            // Assuming Reply will have a way to identify itself
            // Your teammates will need to implement this
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
        if (postReplies.containsKey(postId)) {
            return new ArrayList<>(postReplies.get(postId));
        }
        return new ArrayList<>();
    }
    
    /**
     * Get reply count for a post
     */
    public static int getReplyCount(int postId) {
        if (postReplies.containsKey(postId)) {
            return postReplies.get(postId).size();
        }
        return 0;
    }
    
    /**
     * Helper: Get formatted timestamp (since friend's Post doesn't have this)
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