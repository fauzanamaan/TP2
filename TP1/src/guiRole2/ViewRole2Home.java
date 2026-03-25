 package guiRole2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import entityClasses.Post;
import java.util.List;


/*******
 * <p> Title: ViewRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role2 Home Page. Staff can create posts, view posts,
 * create threads, delete threads, and reply through the post view screen.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewRole2Home {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: Staff discussion system controls
	protected static Button button_CreatePost = new Button("Create Post");
	protected static Button button_ViewAllPosts = new Button("View All Posts");
	protected static Button button_CreateThread = new Button("Create Thread");
	protected static Button button_DeleteThread = new Button("Delete Thread");
	protected static Button button_ViewPost = new Button("View Selected Post");

	protected static Label label_Posts = new Label("Posts:");
	protected static ListView<String> list_Posts = new ListView<>();
	protected static ObservableList<String> postData = FXCollections.observableArrayList();

	protected static Label label_Threads = new Label("Threads:");
	protected static ListView<String> list_Threads = new ListView<>();
	protected static ObservableList<String> threadData = FXCollections.observableArrayList();
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole2Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	
	private static Scene theRole2HomeScene;		// The shared Scene each invocation populates
	protected static final int theRole = 3;		// Admin: 1; Role1: 2; Role2: 3

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayRole2Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Role2 Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayRole2Home(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole2Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());// Set the username

		ControllerRole2Home.loadAllPosts();
		ControllerRole2Home.loadAllThreads();

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Staff Discussion System");
		theStage.setScene(theRole2HomeScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: ViewRole2Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewRole2Home() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theRole2HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// GUI Area 1
		label_PageTitle.setText("Staff Discussion System");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((e) -> {ControllerRole2Home.performUpdate(); });
		
		// GUI Area 2
		setupButtonUI(button_CreatePost, "Dialog", 16, 140, Pos.CENTER, 20, 110);
		button_CreatePost.setOnAction((e) -> {ControllerRole2Home.performCreatePost(); });

		setupButtonUI(button_ViewAllPosts, "Dialog", 16, 140, Pos.CENTER, 180, 110);
		button_ViewAllPosts.setOnAction((e) -> {ControllerRole2Home.loadAllPosts(); });

		setupButtonUI(button_CreateThread, "Dialog", 16, 140, Pos.CENTER, 340, 110);
		button_CreateThread.setOnAction((e) -> {ControllerRole2Home.performCreateThread(); });

		setupButtonUI(button_DeleteThread, "Dialog", 16, 140, Pos.CENTER, 500, 110);
		button_DeleteThread.setOnAction((e) -> {ControllerRole2Home.performDeleteThread(); });

		setupLabelUI(label_Posts, "Arial", 18, 200, Pos.BASELINE_LEFT, 20, 165);
		list_Posts.setItems(postData);
		list_Posts.setLayoutX(20);
		list_Posts.setLayoutY(195);
		list_Posts.setPrefWidth(460);
		list_Posts.setPrefHeight(250);

		setupButtonUI(button_ViewPost, "Dialog", 16, 180, Pos.CENTER, 160, 455);
		button_ViewPost.setOnAction((e) -> {ControllerRole2Home.performViewSelectedPost(); });

		setupLabelUI(label_Threads, "Arial", 18, 200, Pos.BASELINE_LEFT, 510, 165);
		list_Threads.setItems(threadData);
		list_Threads.setLayoutX(510);
		list_Threads.setLayoutY(195);
		list_Threads.setPrefWidth(250);
		list_Threads.setPrefHeight(250);
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((e) -> {ControllerRole2Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((e) -> {ControllerRole2Home.performQuit(); });

		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			button_CreatePost, button_ViewAllPosts, button_CreateThread, button_DeleteThread,
			label_Posts, list_Posts, button_ViewPost,
			label_Threads, list_Threads,
	        line_Separator4, button_Logout, button_Quit);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**
	 * Populate the post list shown to staff
	 */
	protected static void populatePostList(List<Post> posts) {
		postData.clear();

		if (posts == null) {
			return;
		}

		for (Post post : posts) {
			if (post == null) continue;

			String title = post.getTitle();
			if (title == null || title.isBlank()) {
				title = "(Reply/No Title)";
			}

			String username = post.getUsername();
			if (username == null || username.isBlank()) {
				username = "(Unknown User)";
			}

			String threadName = post.getThreadName();
			if (threadName == null || threadName.isBlank()) {
				threadName = "General";
			}

			postData.add("ID " + post.getPostID() + " | " + title + " | " + username + " | " + threadName);
		}
	}

	/**
	 * Populate the thread list shown to staff
	 */
	protected static void populateThreadList(List<String> threads) {
		threadData.clear();

		if (threads == null || threads.isEmpty()) {
			threadData.add("General");
			return;
		}

		if (!threads.contains("General")) {
			threadData.add("General");
		}

		for (String thread : threads) {
			if (thread != null && !thread.isBlank() && !threadData.contains(thread)) {
				threadData.add(thread);
			}
		}
	}

	/**
	 * Show alert dialog
	 */
	protected static void showAlert(String title, String message) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
			javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
