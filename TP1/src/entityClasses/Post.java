package entityClasses;

import java.time.LocalDateTime;
/*******
 * <p> Title: Post Class </p>
 *
 * <p> Description: This class represents a post in the Student Discussion
 * System.
 * A post can be created by a student, viewed by other students, searched,
 * marked as read/unread,
 * edited, and soft-deleted. Supports full CRUD operations and enables all
 * student user stories
 * for discussion functionality. </p>
 *
 *
 */


public class Post{
	
	private int postID;
	private String username;
	private String title;
	private String body;
	private String threadName;
	private LocalDateTime timestamp;
	private boolean isDeleted;
	private String keywords;
	
	// Staff Epics
	private String feedback;
	private String feedbackAuthor;
	private boolean isFlagged;
	private String reason;
	
	
	public Post() {
		this.postID = -1;
        this.username = "";                                                   
        this.title = "";
        this.body = "";                                                       
        this.keywords = "";                                                   
        this.threadName = "General";
        this.timestamp = LocalDateTime.now();                                 
        this.isDeleted = false;
        this.feedback = "";                                                   
        this.feedbackAuthor = "";
        this.isFlagged = false;                                               
        this.reason = "";
	      

	}
	
	public Post(String username, String title, String body, String keywords, String threadName) {
        this.username = username;
        this.title = title;
        this.body = body;
        this.keywords = keywords;
        // Default thread to "General" if not specified
        this.threadName = (threadName == null || threadName.isEmpty()) ? "General" : threadName;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
        this.postID = -1;
        this.feedback = "";
        this.feedbackAuthor = "";
        this.isFlagged = false;
        this.reason = "";
	}
	
	public void changeDelete() {
		
	}
	
	public int getPostID() {
		return postID;
	}
	
	public void setPostID(int postID) {
        this.postID = postID;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
        this.username = username;
    }

	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
		
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	public void setThreadName(String threadName) {
		this.threadName = (threadName == null || threadName.isEmpty()) ? "General" : threadName;	
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
	      this.timestamp = timestamp;
    }

	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public String getKeyWords() {
		return keywords;
	}
	
	public void setKeyWords(String keywords) {
		this.keywords = keywords;
	}
	
	// Staff EPICS Functions
	
	public String getFeedback() {
		return feedback;
	}
	
	public void setFeedback() {
		
	}
	
	public String getFeedbackAuthor() {
		return feedbackAuthor;
	}
	
	public void setFeedbackAuthor() {
		
	}
	
	public boolean isFlag() {
		return isFlagged;
	}
	
	public void isFlag(boolean Flag) {
		
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason() {
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
