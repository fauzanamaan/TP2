package entityClasses;

import java.time.LocalDateTime;

public class Post {

    private int postID;
    private int parentPostID; // -1 for a normal post
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
        this.parentPostID = -1;
        this.username = "";
        this.title = "";
        this.body = "";
        this.threadName = "General";
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
        this.keywords = "";
        this.feedback = "";
        this.feedbackAuthor = "";
        this.isFlagged = false;
        this.reason = "";
    }

    public Post(String username, String title, String body, String keywords, String threadName) {
        this.postID = -1;
        this.parentPostID = -1;
        this.username = username;
        this.title = title;
        this.body = body;
        this.keywords = keywords;
        this.threadName = (threadName == null || threadName.isBlank()) ? "General" : threadName;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
        this.feedback = "";
        this.feedbackAuthor = "";
        this.isFlagged = false;
        this.reason = "";
    }

    public void changeDelete() {
        this.isDeleted = !this.isDeleted;
        if (this.isDeleted) {
            this.title = "This Post has been deleted";
            this.body = "This Post has been deleted";
        }
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public int getParentPostID() {
        return parentPostID;
    }

    public void setParentPostID(int parentPostID) {
        this.parentPostID = parentPostID;
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
        this.threadName = (threadName == null || threadName.isBlank()) ? "General" : threadName;
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

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getKeyWorkds() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    // Staff EPICS Functions

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedbackAuthor() {
        return feedbackAuthor;
    }

    public void setFeedbackAuthor(String feedbackAuthor) {
        this.feedbackAuthor = feedbackAuthor;
    }

    public boolean isFlag() {
        return isFlagged;
    }

    public void isFlag(boolean flag) {
        this.isFlagged = flag;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        this.isFlagged = flagged;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isReply() {
        return parentPostID != -1;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postID=" + postID +
                ", parentPostID=" + parentPostID +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", threadName='" + threadName + '\'' +
                ", timestamp=" + timestamp +
                ", isDeleted=" + isDeleted +
                ", keywords='" + keywords + '\'' +
                ", feedback='" + feedback + '\'' +
                ", feedbackAuthor='" + feedbackAuthor + '\'' +
                ", isFlagged=" + isFlagged +
                ", reason='" + reason + '\'' +
                '}';
    }
}
	
