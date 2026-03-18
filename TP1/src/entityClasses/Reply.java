
package entityClasses;

public class Reply extends Post{
	
	
	private int parentPostID;
	
	public Reply() {
		super();
	}
	
	public Reply(int parentPostID, String authorUserName, String body) {
		super(authorUserName, null, body, null, null);
	}
	
	public int getParentPostID() {
		return parentPostID;
	}
}
