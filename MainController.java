/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import DAO.DBConnection;
import DAO.Query;
import Model.user;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import timeInterface.convertTime;

/**
 * FXML Controller class
 *
 * @author Jeryn
 */
public class MainController implements Initializable {
    private user user;
    
    @FXML
    private Label userLabel;
    
    @FXML
    private Button btnCustomer;
    
    @FXML
    private Button btnAppointment;
    
    @FXML
    private Button btnReports;
    
    private LocalDateTime now;
    
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    
    MainController(user user) {
        this.user = user;
    }

    
    
    @FXML
    private void switchToCustomers(MouseEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Customer.fxml"));
        View_Controller.CustomerController controller = new View_Controller.CustomerController(user);
        loader.setController(controller);
        Parent root = loader.load();
        Scene modify = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(modify);
        stage.setResizable(false);
        stage.show();
    }
    
    @FXML
    private void switchToAppointment(MouseEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Appointment.fxml"));
        View_Controller.AppointmentController controller = new View_Controller.AppointmentController(user);
        loader.setController(controller);
        Parent root = loader.load();
        Scene modify = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(modify);
        stage.setResizable(false);
        stage.show();
    }
    
    @FXML
    private void switchToReports(MouseEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Reports.fxml"));
        View_Controller.ReportsController controller = new View_Controller.ReportsController(user);
        loader.setController(controller);
        Parent root = loader.load();
        Scene modify = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(modify);
        stage.setResizable(false);
        stage.show();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        userLabel.setText(user.getUserName());
        now = convertTime.convert(LocalDateTime.now(), ZoneId.systemDefault(), ZoneId.of("UTC"));
        try {
            checkAppointments(now);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    //LAMBDA FOR CHANGING Local Time to UTC zone that DB is stored in, then can be used to swap back for output
    
    convertTime convertTime = (time, zid, zid2) ->{
        
        ZonedDateTime zCurrent = time.atZone(zid);
        zCurrent = zCurrent.withZoneSameInstant(zid2);
        time = zCurrent.toLocalDateTime();
        return time;
    };
    
    private void checkAppointments(LocalDateTime now) throws SQLException, Exception{
        DBConnection.makeConnection();
        String year = now.toString().substring(0, 4);
        String date = now.toString().substring(5,10);
        String time = now.toString().substring(11, 16);
        String appointment;
        
        
        int difference = 0;
        
        String sqlStatement = "SELECT start FROM appointment WHERE '" + now + "' < start;";  
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        
        if (result.first()){
            appointment = result.getString("start");
            
            if (year.equals(appointment.substring(0,4)) && date.equals(appointment.substring(5,10)) && time.substring(0,2).equals(appointment.substring(11,13)))
            {
                
                difference = Integer.parseInt(appointment.substring(14,16)) - Integer.parseInt(time.substring(3,5));
                
                //if negative because of hour changes make positive
                if (difference < 0){
                    difference += 60;
                }
                if (difference <= 15){
                    displayWarning(appointment);
                }
            }
        }
    }
    
    private void displayWarning(String appointment){
        LocalDateTime appointmentTime = LocalDateTime.parse(appointment, format);
        appointmentTime = convertTime.convert(appointmentTime, ZoneId.of("UTC"), ZoneId.systemDefault());
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Appointment within 15 minutes");
        alert.setTitle("Appointment Notification");
        alert.setContentText("There is an appointment at: " + appointmentTime.toString().substring(11));
        alert.show(); 

    }
}
