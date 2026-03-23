
package entityClasses;

public class Reply extends Post{
	
	
	private int parentPostID;
	
	public Reply() {
		super();
        this.parentPostID = -1;
	}
	
	public Reply(int parentPostID, String authorUserName, String body) {
		super(authorUserName, null, body, null, null);
        this.parentPostID = parentPostID;
	}
	
	public int getParentPostID() {
		return parentPostID;
	}
    public void setParentPostID(int parentPostID) {
        this.parentPostID = parentPostID;
    }

}
