package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2. Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.) This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00        2025-04-29 Updated and expanded from the version produce by on a previous
 *                             version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01        2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	// Shared variables used within this class
	private Connection connection = null;        // Singleton to access the database
	private Statement statement = null;          // The H2 Statement is used to construct queries

	// Post/reply ID counter
	private int nextPostID = 1;

	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	public Database() {

	}

	/*******
	 * <p> Method: connectToDatabase </p>
	 * 
	 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
	 * storage.</p>
	 *
	 * @throws SQLException when the DriverManager is unable to establish a connection
	 * 
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables(); // Create the necessary tables if they don't exist
			initializeNextPostID();
			ensureGeneralThreadExists();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/*******
	 * <p> Method: createTables </p>
	 * 
	 * <p> Description: Used to create new instances of the database tables used by this class.</p>
	 * 
	 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);

		// Create the invitation codes table with expiration time.
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "emailAddress VARCHAR(255), "
				+ "role VARCHAR(10), "
				+ "expirationTime TIMESTAMP)";
		statement.execute(invitationCodesTable);

		String otpTable = "CREATE TABLE IF NOT EXISTS oneTimePasses ("
				+ "otp VARCHAR(255) PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE)";
		statement.execute(otpTable);

		// Create the discussion post/reply table
		String postTable = "CREATE TABLE IF NOT EXISTS postDB ("
				+ "postID INT PRIMARY KEY, "
				+ "parentPostID INT DEFAULT -1, "
				+ "userName VARCHAR(255), "
				+ "title VARCHAR(255), "
				+ "body CLOB, "
				+ "threadName VARCHAR(255), "
				+ "timeStamp TIMESTAMP, "
				+ "isDeleted BOOL DEFAULT FALSE, "
				+ "keywords VARCHAR(255), "
				+ "feedback CLOB, "
				+ "feedbackAuthor VARCHAR(255), "
				+ "isFlagged BOOL DEFAULT FALSE, "
				+ "reason CLOB)";
		statement.execute(postTable);

		// Create thread table
		String threadTable = "CREATE TABLE IF NOT EXISTS threadDB ("
				+ "threadName VARCHAR(255) PRIMARY KEY, "
				+ "createdBy VARCHAR(255), "
				+ "createdAt TIMESTAMP)";
		statement.execute(threadTable);
	}

	/*******
	 * <p> Method: isDatabaseEmpty </p>
	 * 
	 * <p> Description: If the user database has no rows, true is returned, else false.</p>
	 * 
	 * @return true if the database is empty, else it returns false
	 * 
	 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/*******
	 * <p> Method: getNumberOfUsers </p>
	 * 
	 * <p> Description: Returns an integer of the number of users currently in the user database. </p>
	 * 
	 * @return the number of user records in the database.
	 * 
	 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}

	/*******
	 * <p> Method: register(User user) </p>
	 * 
	 * <p> Description: Creates a new row in the database using the user parameter. </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param user specifies a user object to be added to the database.
	 * 
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);

			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);

			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);

			currentMiddleName = user.getMiddleName();
			pstmt.setString(4, currentMiddleName);

			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);

			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);

			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);

			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);

			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);

			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);

			pstmt.executeUpdate();
		}
	}

	/*******
	 * <p> Method: List getUserList() </p>
	 * 
	 * <P> Description: Generate a List of Strings, one for each user in the database,
	 * starting with "<Select User>" at the start of the list. </p>
	 * 
	 * @return a list of userNames found in the database.
	 */
	public List<String> getUserList() {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			return null;
		}
		return userList;
	}

	/*******
	 * <p> Method: boolean loginAdmin(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Admin role.
	 * 
	 * @return true if the specified user has been logged in as an Admin else false.
	 * 
	 */
	public boolean loginAdmin(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole1(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Student role.
	 * 
	 * @return true if the specified user has been logged in as a Student else false.
	 * 
	 */
	public boolean loginRole1(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as a Reviewer else false.
	 * 
	 */
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean doesUserExist(String userName) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */
	public int getNumberOfRoles(User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}

	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a role, this method establishes an invitation
	 * code and adds a record to the InvitationCodes table. </p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * @param role specifies the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	public String generateInvitationCode(String emailAddress, String role) {
		String code = UUID.randomUUID().toString().substring(0, 6);

		java.sql.Timestamp expirationTime = new java.sql.Timestamp(
				System.currentTimeMillis() + (30 * 60 * 1000)
		);

		String query = "INSERT INTO InvitationCodes (code, emailAddress, role, expirationTime) VALUES (?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, role);
			pstmt.setTimestamp(4, expirationTime);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}

	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 * 
	 * @return the number of invitations in the table.
	 * 
	 */
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/******
	 * generate a one time password and insert links it to a user
	 */
	public String generateOneTimePassword(String userName) {
		String otp = UUID.randomUUID().toString().substring(0, 10);
		String query = "INSERT INTO oneTimePasses (otp, userName) VALUES (?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, otp);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return otp;
	}

	/****
	 * 
	 */
	public boolean otpForUserHasBeenGenerated(String userName) {
		String query = "SELECT COUNT(*) AS count FROM oneTimePasses WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been used to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 * 
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	public boolean emailaddressHasBeenUsed(String emailAddress) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the role for the code or an empty string.
	 * 
	 */
	public String getRoleGivenAnInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 */
	public String getUserNameGivenOtp(String otp) {
		String query = "SELECT * FROM oneTimePasses WHERE otp = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, otp);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("userName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 * @return the email address for the code or an empty string.
	 * 
	 */
	public String getEmailAddressUsingCode(String code) {
		String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("emailAddress");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 * 
	 */
	public void removeInvitationAfterUse(String code) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int counter = rs.getInt(1);
				if (counter > 0) {
					query = "DELETE FROM InvitationCodes WHERE code = ?";
					try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
						pstmt2.setString(1, code);
						pstmt2.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	public boolean deleteUser(String username) {
		String query = "DELETE FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			int rows = pstmt.executeUpdate();

			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*****
	 * 
	 */
	public void removeOTPAfterUse(String otp) {
		String query = "SELECT COUNT(*) AS count FROM oneTimePasses WHERE otp = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, otp);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int counter = rs.getInt(1);
				if (counter > 0) {
					query = "DELETE FROM oneTimePasses WHERE otp = ?";
					try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
						pstmt2.setString(1, otp);
						pstmt2.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}

	/****
	 * 
	 */
	public void updatePassword(String username, String password) {
		String query = "UPDATE userDB SET password = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, password);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPassword = password;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username
	 * 
	 */
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("firstName");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 */
	public void updateFirstName(String username, String firstName) {
		String query = "UPDATE userDB SET firstName = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentFirstName = firstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 */
	public String getMiddleName(String username) {
		String query = "SELECT middleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("middleName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 */
	public void updateMiddleName(String username, String middleName) {
		String query = "UPDATE userDB SET middleName = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, middleName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentMiddleName = middleName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: String getLastName(String username) </p>
	 */
	public String getLastName(String username) {
		String query = "SELECT lastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("lastName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 */
	public void updateLastName(String username, String lastName) {
		String query = "UPDATE userDB SET lastName = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lastName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentLastName = lastName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 */
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("preferredFirstName");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 */
	public void updatePreferredFirstName(String username, String preferredFirstName) {
		String query = "UPDATE userDB SET preferredFirstName = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, preferredFirstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPreferredFirstName = preferredFirstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 */
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("emailAddress");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 */
	public void updateEmailAddress(String username, String emailAddress) {
		String query = "UPDATE userDB SET emailAddress = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentEmailAddress = emailAddress;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 */
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) return false;

			currentUsername = rs.getString("userName");
			currentPassword = rs.getString("password");
			currentFirstName = rs.getString("firstName");
			currentMiddleName = rs.getString("middleName");
			currentLastName = rs.getString("lastName");
			currentPreferredFirstName = rs.getString("preferredFirstName");
			currentEmailAddress = rs.getString("emailAddress");
			currentAdminRole = rs.getBoolean("adminRole");
			currentNewRole1 = rs.getBoolean("newRole1");
			currentNewRole2 = rs.getBoolean("newRole2");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 */
	public boolean updateUserRole(String username, String role, String value) {

		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentAdminRole = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentNewRole1 = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				currentNewRole2 = value.compareTo("true") == 0;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}

	// Attribute getters for the current user
	public String getCurrentUsername() { return currentUsername; }
	public String getCurrentPassword() { return currentPassword; }
	public String getCurrentFirstName() { return currentFirstName; }
	public String getCurrentMiddleName() { return currentMiddleName; }
	public String getCurrentLastName() { return currentLastName; }
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName; }
	public String getCurrentEmailAddress() { return currentEmailAddress; }
	public boolean getCurrentAdminRole() { return currentAdminRole; }
	public boolean getCurrentNewRole1() { return currentNewRole1; }
	public boolean getCurrentNewRole2() { return currentNewRole2; }

	/*******
	 * <p> Debugging method</p>
	 */
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
			for (int i = 0; i < meta.getColumnCount(); i++) {
				System.out.println(
						meta.getColumnLabel(i + 1) + ": " +
						resultSet.getString(i + 1));
			}
			System.out.println();
		}
		resultSet.close();
	}

	/*******
	 * <p> Method: getAllUsers() </p>
	 */
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String query = "SELECT * FROM userDB ORDER BY userName";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String userName = rs.getString("userName");
				String password = rs.getString("password");
				String firstName = rs.getString("firstName");
				String middleName = rs.getString("middleName");
				String lastName = rs.getString("lastName");
				String preferredFirstName = rs.getString("preferredFirstName");
				String emailAddress = rs.getString("emailAddress");
				boolean adminRole = rs.getBoolean("adminRole");
				boolean newRole1 = rs.getBoolean("newRole1");
				boolean newRole2 = rs.getBoolean("newRole2");

				User user = new User(userName, password, firstName, middleName, lastName,
						preferredFirstName, emailAddress, adminRole, newRole1, newRole2);
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}

	/********
	 * <p> Method: getAllInvitations </p>
	 */
	public List<String[]> getAllInvitations() {
		deleteExpiredInvitations();
		List<String[]> invitations = new ArrayList<>();
		String query = "SELECT code, emailAddress, role, expirationTime FROM InvitationCodes ORDER BY emailAddress";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String[] invitation = new String[4];
				invitation[0] = rs.getString("code");
				invitation[1] = rs.getString("emailAddress");
				invitation[2] = rs.getString("role");

				java.sql.Timestamp expiration = rs.getTimestamp("expirationTime");
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm");
				invitation[3] = sdf.format(expiration);

				invitations.add(invitation);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return invitations;
	}

	/********
	 * Method: deleteInvitationCode(String code)
	 */
	public boolean deleteInvitationCode(String code) {
		String query = "DELETE FROM InvitationCodes WHERE code = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********
	 * <p> Method: deleteExpiredInvitations() </p>
	 */
	public void deleteExpiredInvitations() {
		String query = "DELETE FROM InvitationCodes WHERE expirationTime < CURRENT_TIMESTAMP";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			int deleted = pstmt.executeUpdate();
			if (deleted > 0) {
				System.out.println("Deleted " + deleted + " expired invitation(s)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*********
	 * <p> Method: initializeNextPostID() </p>
	 */
	private void initializeNextPostID() {
		String query = "SELECT MAX(postID) AS maxID FROM postDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				nextPostID = rs.getInt("maxID") + 1;
				if (nextPostID <= 0) nextPostID = 1;
			}
		} catch (SQLException e) {
			nextPostID = 1;
		}
	}

	/*********
	 * <p> Method: generatePostID() </p>
	 */
	public int generatePostID() {
		return nextPostID++;
	}

	/*********
	 * <p> Method: getAllPosts() </p>
	 */
	public List<Post> getAllPosts() {
		List<Post> posts = new ArrayList<>();
		String query = "SELECT * FROM postDB ORDER BY timeStamp DESC";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int postID = rs.getInt("postID");
				int parentPostID = rs.getInt("parentPostID");
				String userName = rs.getString("userName");
				String title = rs.getString("title");
				String body = rs.getString("body");
				String threadName = rs.getString("threadName");
				Timestamp timeStamp = rs.getTimestamp("timeStamp");
				boolean isDeleted = rs.getBoolean("isDeleted");
				String keywords = rs.getString("keywords");
				String feedback = rs.getString("feedback");
				String feedbackAuthor = rs.getString("feedbackAuthor");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reason = rs.getString("reason");

				Post post;
				if (parentPostID == -1) {
					post = new Post(userName, title, body, keywords, threadName);
				} else {
					post = new Reply(parentPostID, userName, body);
					post.setThreadName(threadName);
					post.setKeywords(keywords);
				}

				post.setPostID(postID);
				post.setParentPostID(parentPostID);
				if (timeStamp != null) {
					post.setTimestamp(timeStamp.toLocalDateTime());
				}
				post.setDeleted(isDeleted);
				post.setFeedback(feedback);
				post.setFeedbackAuthor(feedbackAuthor);
				post.setFlagged(isFlagged);
				post.setReason(reason);

				posts.add(post);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return posts;
	}

	/*********
	 * <p> Method: getPostByID() </p>
	 */
	public Post getPostByID(int postID) {
		String query = "SELECT * FROM postDB WHERE postID = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int parentPostID = rs.getInt("parentPostID");
				String userName = rs.getString("userName");
				String title = rs.getString("title");
				String body = rs.getString("body");
				String threadName = rs.getString("threadName");
				Timestamp timeStamp = rs.getTimestamp("timeStamp");
				boolean isDeleted = rs.getBoolean("isDeleted");
				String keywords = rs.getString("keywords");
				String feedback = rs.getString("feedback");
				String feedbackAuthor = rs.getString("feedbackAuthor");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reason = rs.getString("reason");

				Post post;
				if (parentPostID == -1) {
					post = new Post(userName, title, body, keywords, threadName);
				} else {
					post = new Reply(parentPostID, userName, body);
					post.setThreadName(threadName);
					post.setKeywords(keywords);
				}

				post.setPostID(postID);
				post.setParentPostID(parentPostID);
				if (timeStamp != null) {
					post.setTimestamp(timeStamp.toLocalDateTime());
				}
				post.setDeleted(isDeleted);
				post.setFeedback(feedback);
				post.setFeedbackAuthor(feedbackAuthor);
				post.setFlagged(isFlagged);
				post.setReason(reason);

				return post;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/*********
	 * <p> Method: getRepliesForPost() </p>
	 */
	public List<Reply> getRepliesForPost(int parentPostID) {
		List<Reply> replies = new ArrayList<>();
		String query = "SELECT * FROM postDB WHERE parentPostID = ? ORDER BY timeStamp ASC";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, parentPostID);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Reply reply = new Reply(
						rs.getInt("parentPostID"),
						rs.getString("userName"),
						rs.getString("body")
				);

				reply.setPostID(rs.getInt("postID"));
				reply.setThreadName(rs.getString("threadName"));
				reply.setKeywords(rs.getString("keywords"));

				Timestamp replyTimeStamp = rs.getTimestamp("timeStamp");
				if (replyTimeStamp != null) {
					reply.setTimestamp(replyTimeStamp.toLocalDateTime());
				}

				reply.setDeleted(rs.getBoolean("isDeleted"));
				reply.setFeedback(rs.getString("feedback"));
				reply.setFeedbackAuthor(rs.getString("feedbackAuthor"));
				reply.setFlagged(rs.getBoolean("isFlagged"));
				reply.setReason(rs.getString("reason"));

				replies.add(reply);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return replies;
	}

	/*********
	 * <p> Method: createPost() </p>
	 */
	public Post createPost(String username, String title, String body, String keywords, String threadName) throws SQLException {
		if (title == null || title.trim().isEmpty()) {
			throw new IllegalArgumentException("Post title cannot be empty.");
		}

		if (body == null || body.trim().isEmpty()) {
			throw new IllegalArgumentException("Post body cannot be empty.");
		}

		if (threadName == null || threadName.trim().isEmpty()) {
			threadName = "General";
		}

		ensureThreadExists(threadName, username);

		Post post = new Post(username, title.trim(), body.trim(), keywords, threadName);
		post.setPostID(generatePostID());
		post.setParentPostID(-1);

		String insertPost = "INSERT INTO postDB "
				+ "(postID, parentPostID, userName, title, body, threadName, timeStamp, isDeleted, keywords, feedback, feedbackAuthor, isFlagged, reason) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertPost)) {
			pstmt.setInt(1, post.getPostID());
			pstmt.setInt(2, post.getParentPostID());
			pstmt.setString(3, post.getUsername());
			pstmt.setString(4, post.getTitle());
			pstmt.setString(5, post.getBody());
			pstmt.setString(6, post.getThreadName());
			pstmt.setTimestamp(7, Timestamp.valueOf(post.getTimestamp()));
			pstmt.setBoolean(8, post.isDeleted());
			pstmt.setString(9, post.getKeywords());
			pstmt.setString(10, post.getFeedback());
			pstmt.setString(11, post.getFeedbackAuthor());
			pstmt.setBoolean(12, post.isFlagged());
			pstmt.setString(13, post.getReason());
			pstmt.executeUpdate();
		}

		return post;
	}

	/*********
	 * <p> Method: createReply() </p>
	 */
	public Reply createReply(String username, String body, String keywords, String threadName, int parentPostID) throws SQLException {
		if (body == null || body.trim().isEmpty()) {
			throw new IllegalArgumentException("Reply body cannot be empty.");
		}

		Post parentPost = getPostByID(parentPostID);
		if (parentPost == null) {
			throw new IllegalArgumentException("Parent post not found.");
		}

		Reply reply = new Reply(parentPostID, username, body.trim());
		reply.setPostID(generatePostID());
		reply.setThreadName((threadName == null || threadName.isBlank()) ? parentPost.getThreadName() : threadName);
		reply.setKeywords(keywords);

		ensureThreadExists(reply.getThreadName(), username);

		String insertReply = "INSERT INTO postDB "
				+ "(postID, parentPostID, userName, title, body, threadName, timeStamp, isDeleted, keywords, feedback, feedbackAuthor, isFlagged, reason) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertReply)) {
			pstmt.setInt(1, reply.getPostID());
			pstmt.setInt(2, reply.getParentPostID());
			pstmt.setString(3, reply.getUsername());
			pstmt.setString(4, reply.getTitle());
			pstmt.setString(5, reply.getBody());
			pstmt.setString(6, reply.getThreadName());
			pstmt.setTimestamp(7, Timestamp.valueOf(reply.getTimestamp()));
			pstmt.setBoolean(8, reply.isDeleted());
			pstmt.setString(9, reply.getKeywords());
			pstmt.setString(10, reply.getFeedback());
			pstmt.setString(11, reply.getFeedbackAuthor());
			pstmt.setBoolean(12, reply.isFlagged());
			pstmt.setString(13, reply.getReason());
			pstmt.executeUpdate();
		}

		return reply;
	}
	
	public boolean editPost(int postID, String newTitle, String newBody, String currentUser) {

	    String query = "SELECT userName, parentPostID FROM postDB WHERE postID = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postID);
	        ResultSet rs = pstmt.executeQuery();

	        if (!rs.next()) return false;

	        String owner = rs.getString("userName");

	        
	        if (!owner.equals(currentUser)) return false;

	        int parentID = rs.getInt("parentPostID");

	        //edit reply if it no title found
	        String updateQuery;
	        if (parentID == -1) {
	            updateQuery = "UPDATE postDB SET title = ?, body = ? WHERE postID = ?";
	        } else {
	            updateQuery = "UPDATE postDB SET body = ? WHERE postID = ?";
	        }

	        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

	            if (parentID == -1) {
	                updateStmt.setString(1, newTitle);
	                updateStmt.setString(2, newBody);
	                updateStmt.setInt(3, postID);
	            } else {
	                updateStmt.setString(1, newBody);
	                updateStmt.setInt(2, postID);
	            }

	            return updateStmt.executeUpdate() > 0;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	/*********
	 * <p> Method: softDeletePostById() </p>
	 * 
	 * <p> Description: Marks a post or reply as deleted in the database. </p>
	 * 
	 */
	public boolean softDeletePostById(int postID) {
		String query = "UPDATE postDB SET isDeleted = TRUE WHERE postID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postID);
			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********
	 * <p> Method: ensureGeneralThreadExists() </p>
	 */
	private void ensureGeneralThreadExists() {
		try {
			String query = "SELECT COUNT(*) AS count FROM threadDB WHERE threadName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, "General");
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt("count") == 0) {
					String insert = "INSERT INTO threadDB (threadName, createdBy, createdAt) VALUES (?, ?, ?)";
					try (PreparedStatement insertStmt = connection.prepareStatement(insert)) {
						insertStmt.setString(1, "General");
						insertStmt.setString(2, "System");
						insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
						insertStmt.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*********
	 * <p> Method: ensureThreadExists(String threadName, String createdBy) </p>
	 */
	private void ensureThreadExists(String threadName, String createdBy) {
		try {
			String query = "SELECT COUNT(*) AS count FROM threadDB WHERE threadName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, threadName);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt("count") == 0) {
					String insert = "INSERT INTO threadDB (threadName, createdBy, createdAt) VALUES (?, ?, ?)";
					try (PreparedStatement insertStmt = connection.prepareStatement(insert)) {
						insertStmt.setString(1, threadName);
						insertStmt.setString(2, createdBy);
						insertStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
						insertStmt.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*********
	 * <p> Method: getAllThreads() </p>
	 */
	public List<String> getAllThreads() {
		List<String> threads = new ArrayList<>();
		String query = "SELECT threadName FROM threadDB ORDER BY threadName ASC";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				threads.add(rs.getString("threadName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!threads.contains("General")) {
			threads.add(0, "General");
		}

		return threads;
	}

	/*********
	 * <p> Method: createThread(String threadName, String createdBy) </p>
	 */
	public boolean createThread(String threadName, String createdBy) {
		if (threadName == null || threadName.trim().isEmpty()) {
			return false;
		}

		String trimmedThreadName = threadName.trim();

		try {
			String check = "SELECT COUNT(*) AS count FROM threadDB WHERE threadName = ?";
			try (PreparedStatement checkStmt = connection.prepareStatement(check)) {
				checkStmt.setString(1, trimmedThreadName);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next() && rs.getInt("count") > 0) {
					return false;
				}
			}

			String insert = "INSERT INTO threadDB (threadName, createdBy, createdAt) VALUES (?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insert)) {
				pstmt.setString(1, trimmedThreadName);
				pstmt.setString(2, createdBy);
				pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				pstmt.executeUpdate();
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********
	 * <p> Method: deleteThread(String threadName) </p>
	 */
	public boolean deleteThread(String threadName) {
		if (threadName == null || threadName.trim().isEmpty()) {
			return false;
		}

		if (threadName.equals("General")) {
			return false;
		}

		try {
			String deletePosts = "DELETE FROM postDB WHERE threadName = ?";
			try (PreparedStatement pstmtPosts = connection.prepareStatement(deletePosts)) {
				pstmtPosts.setString(1, threadName);
				pstmtPosts.executeUpdate();
			}

			String deleteThread = "DELETE FROM threadDB WHERE threadName = ?";
			try (PreparedStatement pstmtThread = connection.prepareStatement(deleteThread)) {
				pstmtThread.setString(1, threadName);
				int rowsAffected = pstmtThread.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	public void closeConnection() {
		try {
			if (statement != null) statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null) connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
