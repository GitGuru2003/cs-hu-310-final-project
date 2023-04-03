import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {
	// 5 done
	public static void getAllClassesByInstructor(String first_name, String last_name) throws SQLException {

		Connection connection = Database.getDatabaseConnection();
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			String query = "SELECT\n" + "  instructors.first_name,\n" + "  instructors.last_name,\n"
					+ "  academic_titles.title,\n" + "  classes.code,\n" + "  classes.name as class_name,\n"
					+ "  terms.name as term\n" + "FROM class_sections\n"
					+ "JOIN classes ON class_sections.class_id = classes.class_id\n"
					+ "JOIN instructors ON class_sections.instructor_id = instructors.instructor_id\n"
					+ "JOIN academic_titles ON instructors.academic_title_id = academic_titles.academic_title_id\n"
					+ "JOIN terms ON class_sections.term_id = terms.term_id\n" + "WHERE first_name ='" + first_name
					+ "' AND " + "last_name='" + last_name + "'";

			sqlStatement = connection.createStatement();

			ResultSet rs = sqlStatement.executeQuery(query);

			// iterate through the java resultset
			System.out.println("Class ID | Name | Description | Code");
			while (rs.next()) {

				String f_name = rs.getString("first_name");
				String l_name = rs.getString("last_name");
				String title = rs.getString("title");
				String code = rs.getString("code");
				String name = rs.getString("class_name");
				String term = rs.getString("term");

				// print the results
				System.out.format("%s | %s | %s | %s |%s |%s\n", f_name, l_name, title, code, name, term);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to get class sections");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

	}

	
	public static void submitGrade(String studentId, String classSectionID, String grade) throws SQLException {
		Connection connection = Database.getDatabaseConnection();
		PreparedStatement preparedStatement = null;

		try {
		    connection = Database.getDatabaseConnection();

		    String query = "UPDATE class_registrations " +
		                   "SET grade_id = (SELECT grade_id FROM grades WHERE letter_grade = ?) " +
		                   "WHERE student_id = ? AND class_section_id = ?;";

		    preparedStatement = connection.prepareStatement(query);
		    preparedStatement.setString(1, grade);
		    preparedStatement.setString(2, studentId);
		    preparedStatement.setString(3, classSectionID);

		    int rowsAffected = preparedStatement.executeUpdate();

		    if (rowsAffected > 0) {
		        System.out.println("Grade has been submitted!");
		    } else {
		        System.out.println("Failed to submit grade.");
		    }
		} catch (SQLException sqlException) {
			System.out.println("Failed to submit grade");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public static void registerStudent(String studentId, String classSectionID) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet generatedKeys = null;
		try {
		    connection = Database.getDatabaseConnection();

		    String query = "INSERT INTO class_registrations (student_id, class_section_id) VALUES (?, ?);";

		    preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		    preparedStatement.setString(1, studentId);
		    preparedStatement.setString(2, classSectionID);

		    int rowsAffected = preparedStatement.executeUpdate();

		    if (rowsAffected > 0) {
		        generatedKeys = preparedStatement.getGeneratedKeys();
		        if (generatedKeys.next()) {
		            long registrationId = generatedKeys.getLong(1);
		            System.out.println("Class Registration ID | Student ID | Class Section ID");
		            System.out.println(registrationId + " | "
		                               + studentId + " | " + classSectionID);
		        }
		    } else {
		        System.out.println("Failed to register student.");
		    }
		} catch (SQLException sqlException) {
			System.out.println("Failed to register student");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public static void deleteStudent(String studentId) {
	    Connection connection = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        connection = Database.getDatabaseConnection();

	        String query = "DELETE FROM students WHERE student_id = ?;";

	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, studentId);

	        int rowsAffected = preparedStatement.executeUpdate();

	        if (rowsAffected > 0) {
	            System.out.println("Student with ID: " + studentId + " deleted successfully.");
	        } else {
	            System.out.println("Failed to delete student. No student found with ID: " + studentId);
	        }
	    } catch (SQLException sqlException) {
	        System.out.println("Failed to delete student");
	        System.out.println(sqlException.getMessage());

	    } finally {
	        try {
	            if (preparedStatement != null)
	                preparedStatement.close();
	        } catch (SQLException se2) {
	        }
	        try {
	            if (connection != null)
	                connection.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	    }
	}


	public static void createNewStudent(String firstName, String lastName, String birthdate) throws SQLException {
		Connection connection = Database.getDatabaseConnection();
		PreparedStatement preparedStatement = null;

		try {
		    String query = "INSERT INTO students (first_name, last_name, birthdate) VALUES (?, ?, ?);";

		    preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		    preparedStatement.setString(1, firstName);
		    preparedStatement.setString(2, lastName);
		    preparedStatement.setString(3, birthdate);

		    int rowsAffected = preparedStatement.executeUpdate();

		    if (rowsAffected > 0) {
		        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		                int studentId = generatedKeys.getInt(1);
		                System.out.println("Student ID | First Name | Last Name | Birthdate");
		                System.out.println(studentId + " | " + firstName + " | " + lastName + " | " + birthdate);
		            } else {
		                System.out.println("Failed to get generated student ID.");
		            }
		        }
		    } else {
		        System.out.println("Failed to create student.");
		    }
		} catch (SQLException sqlException) {
			System.out.println("Failed to create student");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

	}

	public static void listAllClassRegistrations() throws SQLException {
		Connection connection = Database.getDatabaseConnection();
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			String query = "SELECT " +
				    "s.student_id, " +
				    "cs.class_section_id, " +
				    "s.first_name, " +
				    "s.last_name, " +
				    "c.code, " +
				    "c.name AS class_name, " +
				    "t.name AS term, " +
				    "g.letter_grade " +
				  "FROM class_registrations AS cr " +
				  "JOIN students AS s ON cr.student_id = s.student_id " +
				  "JOIN class_sections AS cs ON cr.class_section_id = cs.class_section_id " +
				  "JOIN classes AS c ON cs.class_id = c.class_id " +
				  "JOIN terms AS t ON cs.term_id = t.term_id " +
				  "LEFT JOIN grades AS g ON cr.grade_id = g.grade_id;";
;
			sqlStatement = connection.createStatement();
			;

			ResultSet rs = sqlStatement.executeQuery(query);

			// iterate through the java resultset
			System.out.println("Student ID, class_section_id, First Name, Last Name, Code, Name, Term, Letter Grade");
			while (rs.next()) {
				int student_id = rs.getInt("student_id");
				int cs_id = rs.getInt("class_section_id");
				String f_name = rs.getString("first_name");
				String l_name = rs.getString("last_name");
				String code = rs.getString("code");
				String name = rs.getString("class_name");
				String term = rs.getString("term");
				String letterGrade = rs.getString("letter_grade");
				

				// print the results
				System.out.format("%s | %s | %s | %s %s | %s | %s | %s\n", student_id, cs_id, f_name, l_name, code, name, term, letterGrade);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to get class sections");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public static void listAllClassSections() throws SQLException {
		Connection connection = Database.getDatabaseConnection();
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			String query = "SELECT cs.class_section_id, c.code, c.name AS class_name, t.name AS term " +
                    "FROM class_sections cs " +
                    "JOIN classes c ON cs.class_id = c.class_id " +
                    "JOIN terms t ON cs.term_id = t.term_id;";

					;
			sqlStatement = connection.createStatement();
			;

			ResultSet rs = sqlStatement.executeQuery(query);

			// iterate through the java resultset

			System.out.println("Class Section ID | Code | Name | term");

			while (rs.next()) {

				int id = rs.getInt("class_section_id");
				String code = rs.getString("code");
				String name = rs.getString("class_name");
				String term = rs.getString("term");

				// print the results
				System.out.format("%s | %s | %s | %s\n", id, code, name, term);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to get class sections");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public static void listAllClasses() throws SQLException {
		Connection connection = Database.getDatabaseConnection();
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			String query = "SELECT * FROM classes";
			sqlStatement = connection.createStatement();
			;

			ResultSet rs = sqlStatement.executeQuery(query);

			// iterate through the java resultset
			System.out.println("Class ID | Name | Description | Code");
			while (rs.next()) {
				int id = rs.getInt("class_id");
				String name = rs.getString("name");
				String desc = rs.getString("description");
				String code = rs.getString("code");
				int max = rs.getInt("maximum_students");

				// print the results
				System.out.format("%s | %s | %s | %s\n", id, code, name, desc);
			}
		} catch (SQLException sqlException) {
			System.out.println("Failed to get students");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public static void listAllStudents() throws SQLException {
		Connection connection = Database.getDatabaseConnection();
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			// throw new SQLException(); // REMOVE THIS (this is just to force it to
			// compile)
			String query = "SELECT * FROM students";
			sqlStatement = connection.createStatement();
			;

			ResultSet rs = sqlStatement.executeQuery(query);

			// iterate through the java resultset
			System.out.println("Student ID | First Name | Last Name | Birthdate");
			while (rs.next()) {
				int id = rs.getInt("student_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				Date birthdate = rs.getDate("birthdate");

				// print the results
				System.out.format("%s, %s, %s, %s\n", id, firstName, lastName, birthdate);
			}

		} catch (SQLException sqlException) {
			System.out.println("Failed to get students");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	/***
	 * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
	 *
	 * @param command - School Management System cli command
	 * @return splits a string by spaces.
	 */
	public static List<String> parseArguments(String command) {
		List<String> commandArguments = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
		while (m.find())
			commandArguments.add(m.group(1).replace("\"", ""));
		return commandArguments;
	}

	public static void main(String[] args) throws SQLException {
		System.out.println("Welcome to the School Management System");
		System.out.println("-".repeat(80));

		Scanner scan = new Scanner(System.in);
		String command = "";

		do {
			System.out.print("Command: ");
			command = scan.nextLine();
			;
			List<String> commandArguments = parseArguments(command);
			command = commandArguments.get(0);
			commandArguments.remove(0);

			if (command.equals("help")) {
				System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
				System.out.println("test connection \n\tTests the database connection");

				System.out.println("list students \n\tlists all the students");
				System.out.println("list classes \n\tlists all the classes");
				System.out.println("list class_sections \n\tlists all the class_sections");
				System.out.println("list class_registrations \n\tlists all the class_registrations");
				System.out.println(
						"list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");

				System.out.println("delete student <studentId> \n\tdeletes the student");
				System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
				System.out.println(
						"register student <student_id> <class_section_id>\n\tregisters the student to the class section");

				System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
				System.out.println("help \n\tlists help information");
				System.out.println("quit \n\tExits the program");
			} else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
				Database.testConnection();
			} else if (command.equals("list")) {
				if (commandArguments.get(0).equals("students"))
					listAllStudents();
				if (commandArguments.get(0).equals("classes"))
					listAllClasses();
				if (commandArguments.get(0).equals("class_sections"))
					listAllClassSections();
				if (commandArguments.get(0).equals("class_registrations"))
					listAllClassRegistrations();

				if (commandArguments.get(0).equals("instructor")) {
					getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
				}
			} else if (command.equals("create")) {
				if (commandArguments.get(0).equals("student")) {
					createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
				}
			} else if (command.equals("register")) {
				if (commandArguments.get(0).equals("student")) {
					registerStudent(commandArguments.get(1), commandArguments.get(2));
				}
			} else if (command.equals("submit")) {
				if (commandArguments.get(0).equals("grade")) {
					submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
				}
			} else if (command.equals("delete")) {
				if (commandArguments.get(0).equals("student")) {
					deleteStudent(commandArguments.get(1));
				}
			} else if (!(command.equals("quit") || command.equals("exit"))) {
				System.out.println(command);
				System.out.println("Command not found. Enter 'help' for list of commands");
			}
			System.out.println("-".repeat(80));
		} while (!(command.equals("quit") || command.equals("exit")));
		System.out.println("Bye!");
	}
}
