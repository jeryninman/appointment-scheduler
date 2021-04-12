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
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jeryn
 */
public class ReportsController implements Initializable {
    
    @FXML
    private RadioButton rdType;
    
    @FXML
    private RadioButton rdConsultant;
    
    @FXML
    private RadioButton rdLocation;
    
    @FXML
    private TextArea areaReport;
    
 
    
    private user user;
    private String report;
    
    ReportsController(user user) {
        this.user = user;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
    }    
    
    @FXML
    private void generateReport() throws Exception{
        if (rdType.isSelected()){
            generateType(); 
        }
        else if (rdConsultant.isSelected()){
            generateConsult();
        }
        else if (rdLocation.isSelected()){
            generateLocation();
        }
    }
    
    @FXML
    private void toMain(MouseEvent event) throws IOException{
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
    
    
    
   
    
   
    
    private void generateType() throws SQLException, Exception{
        String sqlStatement = "SELECT COUNT(DISTINCT type), MONTH(start) FROM appointment GROUP BY MONTH(start);";
        
        DBConnection.makeConnection();
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        report = "Number of appointment types by month \n";
        while(result.next()){
            report = report + "Month: " + result.getString(2) + ": " + result.getInt(1) + "\n";
        }
        
        areaReport.setText(report);
        DBConnection.closeConnection();
    }
    
    private void generateConsult() throws Exception{
        report = "Consultant Schedules: \n";
        DBConnection.makeConnection();
        int i = 1;
        
        String lastContact = "";
        String sqlStatement = "SELECT contact, DATE(start), TIME(start), TIME(end) FROM appointment ORDER BY contact, start;";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        int colCount = result.getMetaData().getColumnCount();
        
        while(result.next()){
            
            if (i == 1)
            {
                report = report + result.getString("contact") + ": \n" + result.getString(2) + " "
                         + result.getString(3) + " - " + result.getString(4) + "\n";
                
            }
            else if (!(result.getString("contact").equals(lastContact))){
                report = report + result.getString("contact") + ": \n" + result.getString(2)  + " " 
                      + result.getString(3) + " - " + result.getString(4) + "\n";
            }
            else {
                report = report + result.getString(2) + " " + result.getString(3) 
                    + " - " + result.getString(4) + "\n";
            }
            lastContact = result.getString("contact");
            i++;
        }
        
        areaReport.setText(report);
        DBConnection.closeConnection();
    }
    
    private void generateLocation() throws SQLException, Exception{
        report = "Appointment numbers by location: \n";
        DBConnection.makeConnection();
        String sqlStatement = "SELECT COUNT(appointmentId), location FROM appointment GROUP BY location;";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        while(result.next()){
            report = report + result.getString("location") + ": " + result.getInt(1)  + "\n";
        }
        areaReport.setText(report);
        
        
        DBConnection.closeConnection();
        
    }
    
}
