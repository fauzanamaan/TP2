package guiRole2;

import entityClasses.Post;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;

/*******
 * <p> Title: ControllerRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 2 Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * Staff can create posts, create threads, delete threads, and view posts.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation
 */

public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerRole2Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	

	/**********
	 * <p> Method: loadAllPosts() </p>
	 * 
	 * <p> Description: Load all posts for the staff page. </p>
	 * 
	 */
	protected static void loadAllPosts() {
		List<Post> posts = ModelRole2Home.getAllPosts();
		ViewRole2Home.populatePostList(posts);
	}

	/**********
	 * <p> Method: loadAllThreads() </p>
	 * 
	 * <p> Description: Load all threads for the staff page. </p>
	 * 
	 */
	protected static void loadAllThreads() {
		List<String> threads = ModelRole2Home.getAllThreads();
		ViewRole2Home.populateThreadList(threads);
	}

	/**********
	 * <p> Method: performCreatePost() </p>
	 * 
	 * <p> Description: Allows staff to create a post. </p>
	 * 
	 */
	protected static void performCreatePost() {
		TextInputDialog titleDialog = new TextInputDialog();
		titleDialog.setTitle("Create Staff Post");
		titleDialog.setHeaderText("Enter Post Title");
		titleDialog.setContentText("Title:");

		Optional<String> titleResult = titleDialog.showAndWait();

		if (titleResult.isEmpty()) {
			return;
		}

		String title = titleResult.get().trim();
		if (title.isEmpty()) {
			ViewRole2Home.showAlert("Error", "Title cannot be empty.");
			return;
		}

		TextInputDialog bodyDialog = new TextInputDialog();
		bodyDialog.setTitle("Create Staff Post");
		bodyDialog.setHeaderText("Enter Post Body");
		bodyDialog.setContentText("Body:");

		Optional<String> bodyResult = bodyDialog.showAndWait();

		if (bodyResult.isEmpty()) {
			return;
		}

		String body = bodyResult.get().trim();
		if (body.isEmpty()) {
			ViewRole2Home.showAlert("Error", "Body cannot be empty.");
			return;
		}

		String selectedThread = ViewRole2Home.list_Threads.getSelectionModel().getSelectedItem();
		if (selectedThread == null || selectedThread.isBlank()) {
			selectedThread = "General";
		}

		boolean success = ModelRole2Home.createPost(title, body, selectedThread);

		if (success) {
			ViewRole2Home.showAlert("Success", "Post created successfully.");
			loadAllPosts();
		} else {
			ViewRole2Home.showAlert("Error", "Failed to create post.");
		}
	}

	/**********
	 * <p> Method: performCreateThread() </p>
	 * 
	 * <p> Description: Allows staff to create a thread. </p>
	 * 
	 */
	protected static void performCreateThread() {
		TextInputDialog threadDialog = new TextInputDialog();
		threadDialog.setTitle("Create Thread");
		threadDialog.setHeaderText("Enter Thread Name");
		threadDialog.setContentText("Thread Name:");

		Optional<String> threadResult = threadDialog.showAndWait();

		if (threadResult.isEmpty()) {
			return;
		}

		String threadName = threadResult.get().trim();
		if (threadName.isEmpty()) {
			ViewRole2Home.showAlert("Error", "Thread name cannot be empty.");
			return;
		}

		boolean success = ModelRole2Home.createThread(threadName);

		if (success) {
			ViewRole2Home.showAlert("Success", "Thread created successfully.");
			loadAllThreads();
		} else {
			ViewRole2Home.showAlert("Error", "Failed to create thread.");
		}
	}

	/**********
	 * <p> Method: performDeleteThread() </p>
	 * 
	 * <p> Description: Allows staff to delete the selected thread. </p>
	 * 
	 */
	protected static void performDeleteThread() {
		String selectedThread = ViewRole2Home.list_Threads.getSelectionModel().getSelectedItem();

		if (selectedThread == null || selectedThread.isBlank()) {
			ViewRole2Home.showAlert("No Selection", "Please select a thread to delete.");
			return;
		}

		if (selectedThread.equals("General")) {
			ViewRole2Home.showAlert("Error", "The General thread cannot be deleted.");
			return;
		}

		boolean success = ModelRole2Home.deleteThread(selectedThread);

		if (success) {
			ViewRole2Home.showAlert("Success", "Thread deleted successfully.");
			loadAllThreads();
			loadAllPosts();
		} else {
			ViewRole2Home.showAlert("Error", "Failed to delete thread.");
		}
	}

	/**********
	 * <p> Method: performViewSelectedPost() </p>
	 * 
	 * <p> Description: Opens the selected post so staff can view and reply. </p>
	 * 
	 */
	protected static void performViewSelectedPost() {
		int selectedIndex = ViewRole2Home.list_Posts.getSelectionModel().getSelectedIndex();

		if (selectedIndex < 0) {
			ViewRole2Home.showAlert("No Selection", "Please select a post to view.");
			return;
		}

		List<Post> posts = ModelRole2Home.getAllPosts();
		if (selectedIndex >= posts.size()) {
			ViewRole2Home.showAlert("Error", "Selected post could not be found.");
			return;
		}

		Post selectedPost = posts.get(selectedIndex);

		guiViewPost.ViewViewPost.displayViewPost(
			ViewRole2Home.theStage,
			ViewRole2Home.theUser,
			selectedPost
		);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
}
