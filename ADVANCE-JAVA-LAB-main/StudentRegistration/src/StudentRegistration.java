// package StudentRegistration.src;
// import javax.swing.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;

// public class StudentRegistration extends JFrame implements ActionListener {

//     // Labels
//     JLabel lblName, lblRoll, lblGender, lblBranch;

//     // Text Fields
//     JTextField txtName, txtRoll, txtBranch;

//     // Radio Buttons
//     JRadioButton male, female;
//     ButtonGroup genderGroup;

//     // Check Box
//     JCheckBox terms;

//     // Buttons
//     JButton submit, reset;

//     public StudentRegistration() {

//         setTitle("Student Registration Form");
//         setSize(450, 450);
//         setLayout(null);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLocationRelativeTo(null);

//         // Student Name
//         lblName = new JLabel("Student Name");
//         lblName.setBounds(30, 30, 120, 30);
//         add(lblName);

//         txtName = new JTextField();
//         txtName.setBounds(170, 30, 180, 30);
//         add(txtName);

//         // Roll Number
//         lblRoll = new JLabel("Roll Number");
//         lblRoll.setBounds(30, 80, 120, 30);
//         add(lblRoll);

//         txtRoll = new JTextField();
//         txtRoll.setBounds(170, 80, 180, 30);
//         add(txtRoll);

//         // Gender
//         lblGender = new JLabel("Gender");
//         lblGender.setBounds(30, 130, 120, 30);
//         add(lblGender);

//         male = new JRadioButton("Male");
//         male.setBounds(170, 130, 80, 30);
//         add(male);

//         female = new JRadioButton("Female");
//         female.setBounds(260, 130, 100, 30);
//         add(female);

//         genderGroup = new ButtonGroup();
//         genderGroup.add(male);
//         genderGroup.add(female);

//         // Branch
//         lblBranch = new JLabel("Branch");
//         lblBranch.setBounds(30, 180, 120, 30);
//         add(lblBranch);

//         txtBranch = new JTextField();
//         txtBranch.setBounds(170, 180, 180, 30);
//         add(txtBranch);

//         // Terms
//         terms = new JCheckBox("I accept Terms & Conditions");
//         terms.setBounds(30, 230, 250, 30);
//         add(terms);

//         // Buttons
//         submit = new JButton("Submit");
//         submit.setBounds(80, 290, 100, 35);
//         submit.addActionListener(this);
//         add(submit);

//         reset = new JButton("Reset");
//         reset.setBounds(220, 290, 100, 35);
//         reset.addActionListener(this);
//         add(reset);

//         setVisible(true);
//     }

//     @Override
//     public void actionPerformed(ActionEvent e) {

//         if (e.getSource() == reset) {
//             txtName.setText("");
//             txtRoll.setText("");
//             txtBranch.setText("");
//             genderGroup.clearSelection();
//             terms.setSelected(false);
//             return;
//         }

//         if (txtName.getText().trim().isEmpty()) {
//             JOptionPane.showMessageDialog(this, "Please enter Student Name");
//             return;
//         }

//         if (txtRoll.getText().trim().isEmpty()) {
//             JOptionPane.showMessageDialog(this, "Please enter Roll Number");
//             return;
//         }

//         if (!male.isSelected() && !female.isSelected()) {
//             JOptionPane.showMessageDialog(this, "Please select Gender");
//             return;
//         }

//         if (txtBranch.getText().trim().isEmpty()) {
//             JOptionPane.showMessageDialog(this, "Please enter Branch");
//             return;
//         }

//         if (!terms.isSelected()) {
//             JOptionPane.showMessageDialog(this, "Please accept Terms & Conditions");
//             return;
//         }

//         String gender = male.isSelected() ? "Male" : "Female";

//         JOptionPane.showMessageDialog(
//                 this,
//                 "Registration Successful\n\n"
//                         + "Name : " + txtName.getText()
//                         + "\nRoll Number : " + txtRoll.getText()
//                         + "\nGender : " + gender
//                         + "\nBranch : " + txtBranch.getText());
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new StudentRegistration());
//     }
// }
package StudentRegistration.src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentRegistration extends JFrame implements ActionListener {

    // Labels
    JLabel lblName, lblRoll, lblGender, lblBranch;

    // Text Fields
    JTextField txtName, txtRoll, txtBranch;

    // Radio Buttons
    JRadioButton male, female;
    ButtonGroup genderGroup;

    // Check Box
    JCheckBox terms;

    // Buttons
    JButton submit, reset;

    // JDBC
    static final String URL = "jdbc:mysql://localhost:3306/studentdb";
    static final String USER = "root";
    static final String PASSWORD = "Pawan@6377"; // apna MySQL password

    public StudentRegistration() {

        setTitle("Student Registration Form");
        setSize(450, 450);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Student Name
        lblName = new JLabel("Student Name");
        lblName.setBounds(30, 30, 120, 30);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(170, 30, 180, 30);
        add(txtName);

        // Roll Number
        lblRoll = new JLabel("Roll Number");
        lblRoll.setBounds(30, 80, 120, 30);
        add(lblRoll);

        txtRoll = new JTextField();
        txtRoll.setBounds(170, 80, 180, 30);
        add(txtRoll);

        // Gender
        lblGender = new JLabel("Gender");
        lblGender.setBounds(30, 130, 120, 30);
        add(lblGender);

        male = new JRadioButton("Male");
        male.setBounds(170, 130, 80, 30);
        add(male);

        female = new JRadioButton("Female");
        female.setBounds(260, 130, 100, 30);
        add(female);

        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);

        // Branch
        lblBranch = new JLabel("Branch");
        lblBranch.setBounds(30, 180, 120, 30);
        add(lblBranch);

        txtBranch = new JTextField();
        txtBranch.setBounds(170, 180, 180, 30);
        add(txtBranch);

        // Terms
        terms = new JCheckBox("I accept Terms & Conditions");
        terms.setBounds(30, 230, 250, 30);
        add(terms);

        // Buttons
        submit = new JButton("Submit");
        submit.setBounds(80, 290, 100, 35);
        submit.addActionListener(this);
        add(submit);

        reset = new JButton("Reset");
        reset.setBounds(220, 290, 100, 35);
        reset.addActionListener(this);
        add(reset);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Reset Button
        if (e.getSource() == reset) {

            txtName.setText("");
            txtRoll.setText("");
            txtBranch.setText("");

            genderGroup.clearSelection();
            terms.setSelected(false);

            return;
        }

        // Validation
        if (txtName.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Student Name"
            );

            return;
        }

        if (txtRoll.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Roll Number"
            );

            return;
        }

        if (!male.isSelected() && !female.isSelected()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select Gender"
            );

            return;
        }

        if (txtBranch.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Branch"
            );

            return;
        }

        if (!terms.isSelected()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please accept Terms & Conditions"
            );

            return;
        }

        // Get Gender
        String gender = male.isSelected() ? "Male" : "Female";

        // ================= JDBC CODE =================

        String sql = "INSERT INTO students(name, roll_no, gender, branch) "
                   + "VALUES (?, ?, ?, ?)";

        try {

            // Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connection
            Connection con = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Connection Created");

            // PreparedStatement
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, txtName.getText());
            ps.setString(2, txtRoll.getText());
            ps.setString(3, gender);
            ps.setString(4, txtBranch.getText());

            // Execute Query
            int result = ps.executeUpdate();

            if (result > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration Successful\n\n"
                        + "Name : " + txtName.getText()
                        + "\nRoll Number : " + txtRoll.getText()
                        + "\nGender : " + gender
                        + "\nBranch : " + txtBranch.getText()
                );
            }

            // Close
            ps.close();
            con.close();

        } catch (ClassNotFoundException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "JDBC Driver Not Found!"
            );

            ex.printStackTrace();

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + ex.getMessage()
            );

            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new StudentRegistration()
        );
    }
}