package entityClasses;

import java.time.LocalDateTime;

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
		
	}
	
	public Post(String username, String title, String body, String keywords, String threadName) {
		
	}
	
	public void changeDelete() {
		
	}
	
	public int getPostID() {
		return postID;
	}
	
	public void setPostID(int postID) {
		
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	public void setThreadName() {
		
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public String getKeyWorkds() {
		return keywords;
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
