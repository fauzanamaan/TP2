package entityClasses;

public class Reply extends Post {

    public Reply() {
        super();
        setTitle(null); // replies should not have a title
        setParentPostID(-1);
    }

    public Reply(int parentPostID, String authorUserName, String body) {
        super(authorUserName, null, body, null, null);

        // replies should not have a title
        setTitle(null);

        // set parent post ID (IMPORTANT)
        setParentPostID(parentPostID);
    }

    @Override
    public String toString() {
        return "Reply{" +
                "postID=" + getPostID() +
                ", parentPostID=" + getParentPostID() +
                ", username='" + getUsername() + '\'' +
                ", body='" + getBody() + '\'' +
                ", threadName='" + getThreadName() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }

