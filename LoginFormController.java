/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Model.user;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import DAO.DBConnection;
import DAO.Query;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * FXML Controller class
 *
 * @author Jeryn
 */
public class LoginFormController implements Initializable {
    
    private String language = "ENGLISH";
    @FXML
    private Label userName;
    
    @FXML 
    private Label password;
    
    @FXML
    private Label instruct; 
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
    
    
    ObservableList<user> allUsers = FXCollections.observableArrayList();

    
    
    @FXML
    private void changeToSpanish(){
        language = "SPANISH";
        instruct.setText("Por favor, introduzca sus credenciales.");
        userName.setText("Nombre de usuario: ");
        password.setText("Contraseña: ");
        
    }
    
   
    
    @FXML
    private void checkLogIn(MouseEvent event) throws IOException, SQLException, Exception{
        String username = userNameField.getText();
        
        String passWord = passwordField.getText();
        user user = new user();
        
        
         //connect to db
        DBConnection.makeConnection();
        ResultSet result;
        //log attempt
        logLogInAttempt(username);
        //validate user
        String sqlStatement = "SELECT USERID, USERNAME FROM user WHERE USERNAME = '" + username + "' AND PASSWORD = '" + passWord + "'";
        Query.makeQuery(sqlStatement);
        result = Query.getResult();
        
        
        
        

         
        if (result.first()) //should be only one result
        { 
            user.setUserId(result.getInt("userId"));
            user.setUserName(result.getString("userName"));
            DBConnection.closeConnection();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
            View_Controller.MainController controller = new View_Controller.MainController(user);
            loader.setController(controller);
            Parent root = loader.load();
            Scene modify = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(modify);
            stage.setResizable(false);
            stage.show();
        }
        else{
           errorMessage();
           DBConnection.closeConnection();
        }
        
    } 
    @FXML
    private void errorMessage(){
        Alert alert = new Alert(AlertType.INFORMATION);
        
        if (language.equals("SPANISH")){
            
            alert.setHeaderText("Combinación inválida");
            alert.setContentText("Combinación inválida del nobre usuario y contraseña.");
            alert.show();
        }
        else {
            alert.setHeaderText("Invalid Combination");
            alert.setContentText("Invalid combination of username and password.");
            alert.show();
        }
    }
    
    private void logLogInAttempt(String user) throws IOException{
        String filename = "src/files/loginlog.txt";
        FileWriter fwriter = new FileWriter(filename, true);
        PrintWriter outputFile = new PrintWriter(fwriter);
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime loginTime = now.atZone(ZoneId.systemDefault());
        
        outputFile.println(loginTime.toString() + " " + user);
        outputFile.close();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //Locale.setDefault(new Locale("es"));
        Locale currentLocale = Locale.getDefault();
        Locale es = new Locale("es");
        if (currentLocale.equals(es)){
            changeToSpanish();
        }
        
    }    
    
}
