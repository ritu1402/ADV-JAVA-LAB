import java.sql.*;

// public class App {
//     public static void main(String[] args) throws Exception {
//         Connection con=DriverManager.getConnection(
//             "jdbc:mysql://localhost:3306/college",
//             "root",
//             "Pawan@6377"      
//         );

//         // PreparedStatement pds=con.prepareStatement(
//         //     "insert into student value(?,?,?)"
//         // );
//         System.out.print(con);
//         System.out.println("Connection created");
//         con.close();

//     }
// }
// import java.sql.*;

public class App {

    public static void main(String args[]) throws Exception {

        // Step 1 : Load Driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Step 2 : Make Connection
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/college",
                "root",
                "Pawan@6377"
        );

        System.out.println("Connection Created");

        // Step 3 : Create PreparedStatement
        PreparedStatement ps = con.prepareStatement(
                "insert into student values(?,?,?)"
        );

        // Step 4 : Set Values
        ps.setInt(1, 11);
        ps.setString(2, "Ram");
        ps.setString(3, "CSE");

        // Step 5 : Execute Insert
        ps.executeUpdate();

        // Step 6 : Print
        System.out.println("Record Inserted");

        // Step 7 : Create Statement
        Statement st = con.createStatement();

        // Step 8 : Execute Query
        ResultSet rs = st.executeQuery("select * from student");

        // Step 9 : Read ResultSet
        while (rs.next()) {

            System.out.println(
                    rs.getInt("student_id") + " " +
                    rs.getString("student_name") + " " +
                    rs.getString("student_branch")
            );
        }

        // Step 10 : Close Resources
        rs.close();
        st.close();
        ps.close();
        con.close();

        System.out.println("Connection Closed");
    }
}