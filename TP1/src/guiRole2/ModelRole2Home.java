package guiRole2;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Post;

/*******
 * <p> Title: ModelRole2Home Class. </p>
 * 
 * <p> Description: The Role2Home Page Model. This class handles staff discussion actions,
 * including viewing posts, creating posts, creating threads, and deleting threads.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 * @version 1.01		2025-09-13 Updated JavaDoc description
 *  
 */

public class ModelRole2Home {

	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> Method: getAllPosts() </p>
	 * 
	 * <p> Description: Gets all posts for the staff page. </p>
	 * 
	 * @return a list of posts
	 * 
	 */
	protected static List<Post> getAllPosts() {
		try {
			List<Post> posts = theDatabase.getAllPosts();
			if (posts == null) {
				return new ArrayList<>();
			}
			return posts;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**********
	 * <p> Method: createPost(String title, String body, String threadName) </p>
	 * 
	 * <p> Description: Allows staff to create a post. </p>
	 * 
	 * @param title the post title
	 * @param body the post body
	 * @param threadName the selected thread
	 * 
	 * @return true if successful, else false
	 * 
	 */
	protected static boolean createPost(String title, String body, String threadName) {
		try {
			if (ViewRole2Home.theUser == null) return false;
			String currentUser = ViewRole2Home.theUser.getUserName();
			if (currentUser == null || currentUser.isBlank()) return false;

			String finalThreadName = (threadName == null || threadName.isBlank()) ? "General" : threadName;
			theDatabase.createPost(currentUser, title, body, "", finalThreadName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**********
	 * <p> Method: getAllThreads() </p>
	 * 
	 * <p> Description: Gets all thread names. </p>
	 * 
	 * @return a list of threads
	 * 
	 */
	protected static List<String> getAllThreads() {
		try {
			List<String> threads = theDatabase.getAllThreads();
			if (threads == null || threads.isEmpty()) {
				List<String> defaultThreads = new ArrayList<>();
				defaultThreads.add("General");
				return defaultThreads;
			}
			return threads;
		} catch (Exception e) {
			e.printStackTrace();
			List<String> defaultThreads = new ArrayList<>();
			defaultThreads.add("General");
			return defaultThreads;
		}
	}

	/**********
	 * <p> Method: createThread(String threadName) </p>
	 * 
	 * <p> Description: Allows staff to create a new thread. </p>
	 * 
	 * @param threadName the new thread name
	 * 
	 * @return true if successful, else false
	 * 
	 */
	protected static boolean createThread(String threadName) {
		try {
			if (threadName == null || threadName.isBlank()) return false;
			if (ViewRole2Home.theUser == null) return false;

			String currentUser = ViewRole2Home.theUser.getUserName();
			if (currentUser == null || currentUser.isBlank()) return false;

			return theDatabase.createThread(threadName.trim(), currentUser);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**********
	 * <p> Method: deleteThread(String threadName) </p>
	 * 
	 * <p> Description: Allows staff to delete a thread. </p>
	 * 
	 * @param threadName the thread to delete
	 * 
	 * @return true if successful, else false
	 * 
	 */
	protected static boolean deleteThread(String threadName) {
		try {
			if (threadName == null || threadName.isBlank()) return false;
			if ("General".equalsIgnoreCase(threadName.trim())) return false;

			return theDatabase.deleteThread(threadName.trim());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
