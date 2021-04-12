/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import DAO.DBConnection;
import DAO.Query;
import Model.appointment;
import Model.user;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import timeInterface.convertTime;
import timeInterface.length;


/**
 * FXML Controller class
 *
 * @author Jeryn
 */
public class AppointmentController implements Initializable {

    ObservableList<String> yearList = FXCollections.observableArrayList("2019", "2020", "2021", "2022");
    ObservableList<String> monthList = FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
    ObservableList<String> dayList = FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31");
    ObservableList<String> hourList = FXCollections.observableArrayList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18" , "19", "20", "21", "22", "23");
    ObservableList<String> minuteList = FXCollections.observableArrayList("00", "15", "30", "45");
    ObservableList<String> locationList = FXCollections.observableArrayList("Phoenix", "New York", "London");
    ObservableList<String> lengthList = FXCollections.observableArrayList("15", "30", "45", "60");
    ObservableList<String> weekList = FXCollections.observableArrayList("01-07", "08-14", "15-21", "22-28", "29-31");
    
    @FXML
    private TableView<appointment> AppointmentTable;
    
    @FXML
    private ComboBox bxMonthSort;
    
    @FXML
    private ComboBox bxWeekSort;
    
    @FXML
    private ComboBox bxYear;
    
    @FXML
    private ComboBox bxMonth;
    
    @FXML
    private ComboBox bxDay;
    
    @FXML
    private ComboBox bxHour;
    
    @FXML
    private ComboBox bxMinute;
    
    @FXML
    private ComboBox bxLocation;
    
    @FXML
    private ComboBox bxLength;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField contactField;
    
    @FXML
    private TextField typeField;
    
    @FXML
    private TextField urlField;
    
    @FXML
    private TextField customerField;
    
    @FXML
    private TextArea descriptionField;
    
    @FXML
    private RadioButton rdMonth;
    
    @FXML
    private RadioButton rdWeek;
    
    @FXML
    private Button btnCancel;
    
    ObservableList<appointment> allAppointments = FXCollections.observableArrayList();
    ObservableList<appointment> sortAppointments = FXCollections.observableArrayList();
    
    
    private appointment currentAppointment;
    private String username;
    private user user;
    private int customerId = 0;
    private boolean edit = false;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    ZoneId zid = ZoneId.systemDefault();
    ZoneId utc = ZoneId.of("UTC");
    
    AppointmentController(user user, int customerId) {
        this.user = user;
        this.username = user.getUserName();
        this.customerId = customerId;
    }

    AppointmentController(user user) {
        this.username = user.getUserName();
        this.user = user;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bxYear.setValue("2019");
        bxYear.setItems(yearList);
        bxMonth.setValue("01");
        bxMonth.setItems(monthList);
        bxDay.setValue("01");
        bxDay.setItems(dayList);
        bxHour.setValue("08");
        bxHour.setItems(hourList);
        bxMinute.setValue("00");
        bxMinute.setItems(minuteList);
        bxLength.setValue("15");
        bxLength.setItems(lengthList);
        bxLocation.setValue("Phoenix");
        bxLocation.setItems(locationList);
        bxMonthSort.setItems(monthList);
        bxMonthSort.setValue("01");
        bxWeekSort.setItems(weekList);
        bxWeekSort.setValue("01-07");
        
        
        
        
        try {
            updateTable();
        } catch (Exception ex) {
            Logger.getLogger(AppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
    @FXML
    private void commitChange() throws SQLException, Exception{
        String appointmentDate;
        String end;
        String location = bxLocation.getSelectionModel().getSelectedItem().toString();
        
        LocalDateTime ldtUpdate = LocalDateTime.now();
        ldtUpdate = convertTime.convert(ldtUpdate, ZoneId.systemDefault(),ZoneId.of("UTC"));
        String now = ldtUpdate.format(format);
        int editId = 0;
        if (edit){
            editId = currentAppointment.getAppointmentId();
        }
        
        
        appointmentDate = bxYear.getSelectionModel().getSelectedItem().toString() 
                 + "-" + bxMonth.getSelectionModel().getSelectedItem().toString()
                 + "-" + bxDay.getSelectionModel().getSelectedItem().toString() 
                 + " " + bxHour.getSelectionModel().getSelectedItem().toString()
                 + ":" + bxMinute.getSelectionModel().getSelectedItem().toString()
                 + ":00";
        
         LocalDateTime startTime;
        try{
        startTime = LocalDateTime.parse(appointmentDate, format);
        }catch (DateTimeParseException e){
            System.out.println(appointmentDate); //remove
            appointmentDate = appointmentDate + ".0";
            startTime = LocalDateTime.parse(appointmentDate, format);
        }
        
        //convert to UTC for DB if new time
        
        startTime = convertTime.convert(startTime, ZoneId.systemDefault(), ZoneId.of("UTC"));
        
        appointmentDate = startTime.format(format);
        
        end = bxLength.getSelectionModel().getSelectedItem().toString();
         
        LocalDateTime endTime = startTime.plusMinutes(Integer.parseInt(end));
        
        end = endTime.format(format);
        
        DBConnection.makeConnection();
        if (validFields()){     //validate fields are filled correctly
            if (validTime(startTime, endTime, editId) && withinBusinessHours(startTime)){ //check for overlapping appointments and during business hours
               
                String sqlStatement = "INSERT INTO appointment(customerId, userId, title, "
                + "description, location, contact, type, url, start, end, "
                + "createDate, createdBy, lastUpdate, lastUpdateBy ) VALUES ('"
                + customerField.getText() + "', '" + user.getUserId() + "',  '" + titleField.getText()
                + "', '" + descriptionField.getText() + "', '" + location + "', '"
                + contactField.getText() + "', '" + typeField.getText() + "', '"
                + urlField.getText() + "', '" + appointmentDate + "', '"
                + end
                        
                        + "', '" + now + "', '" 
                + username + "',  '" + now + "', '" + username + "');";
                
                
                if (edit){
                    sqlStatement = "UPDATE appointment SET title = '" + titleField.getText()
                            + "', description = '" +  descriptionField.getText() + "', location = '"
                            + location + "', contact = '" + contactField.getText() + "', type = '"
                            + typeField.getText()+ "', url =  '" + urlField.getText() 
                            + "', start = '" + appointmentDate + "', end = '" + end
                            + "', lastUpdate =  '" + now + "', lastUpdateBy = '" + username
                            + "' WHERE appointmentid = '" + currentAppointment.getAppointmentId() + "';";

                
                    edit = false;
                    customerField.setDisable(false);
                    btnCancel.setDisable(true);
                }
                Query.makeQuery(sqlStatement);
                DBConnection.closeConnection(); 
                resetFields();   
                updateTable();
            }
            
        }
        
    }
    
    @FXML
    private void deleteAppt() throws SQLException, Exception{
        DBConnection.makeConnection();
        appointment toDelete = AppointmentTable.getSelectionModel().getSelectedItem();
        int appointmentId = toDelete.getAppointmentId();
        
        String sqlStatement = "DELETE FROM appointment WHERE appointmentId = '"
                + appointmentId + "';";
        Query.makeQuery(sqlStatement);
        DBConnection.closeConnection();
        updateTable();
    }
    
    @FXML
    private void editAppt(){
        edit = true;
        try{
            currentAppointment = AppointmentTable.getSelectionModel().getSelectedItem();

            String date = currentAppointment.getStart().toString();
            bxYear.setValue(date.substring(0,4));
            bxMonth.setValue(date.substring(5,7));
            bxDay.setValue(date.substring(8,10));
            bxHour.setValue(date.substring(11,13));
            bxMinute.setValue(date.substring(14,16));
            currentAppointment = AppointmentTable.getSelectionModel().getSelectedItem();
            titleField.setText(currentAppointment.getTitle());
            bxLocation.setValue(currentAppointment.getLocation());
            bxLength.setValue(Integer.toString(calculateLength.calculateLength(currentAppointment.getStart(), currentAppointment.getEnd())));
            descriptionField.setText(currentAppointment.getDescription());
            urlField.setText(currentAppointment.getUrl());
            typeField.setText(currentAppointment.getType());
            contactField.setText(currentAppointment.getContact());
            customerField.setText(Integer.toString(currentAppointment.getCustomerId()));
            customerField.setDisable(true); // no reason to change customer for appointments, delete instead
            btnCancel.setDisable(false);
        }catch (Exception e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("You must select an appointment to edit");
            alert.show();
        }
    }
    
    @FXML 
    private void updateTable() throws Exception{
        
        allAppointments = getAllAppointments();
        AppointmentTable.setItems(allAppointments);
    }
    
    @FXML
    private void monthFilter() throws Exception{
        
        String selection = bxMonthSort.getSelectionModel().getSelectedItem().toString();
        String start;
        String month;
        sortAppointments.clear();
        
        
        for (appointment a : allAppointments){
            start = a.getStart().toString();
            month = start.substring(5,7);
            if (selection.equals(month)){
                if (!sortAppointments.contains(a)){
                    sortAppointments.add(a);
                }
            }
        }
        
        AppointmentTable.setItems(sortAppointments);
        if (sortAppointments.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("No results found, try a different range.");
            alert.show();
        }
    }
    
    @FXML 
    private void weekFilter(){
        String selection = bxWeekSort.getSelectionModel().getSelectedItem().toString();
        //split string to get important dates,
        String startMonth = bxMonthSort.getSelectionModel().getSelectedItem().toString();
        String startDate = selection.substring(0,2);
        String endDate = selection.substring(3,5);
        String date;
        String month;
        int start = Integer.parseInt(startDate);
        int end = Integer.parseInt(endDate);
        int dt;
        
        sortAppointments.clear();
        
        for (appointment a : allAppointments){
            date = a.getStart().toString().substring(8,10);
            month = a.getStart().toString().substring(5,7);
            dt = Integer.parseInt(date);
            
            if (dt > start && dt < end && month.equals(startMonth)){ 
                sortAppointments.add(a);
            }
        }
        
        AppointmentTable.setItems(sortAppointments);
        if (sortAppointments.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("No results found, note: Month must be selected in conjunction with date range.");
            alert.show();
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
    
    @FXML
    private void filter() throws Exception{
        if (rdMonth.isSelected()){
            monthFilter();
        }
        else if (rdWeek.isSelected()){
            weekFilter();
        }
    }
    
    private ObservableList<appointment> getAllAppointments() throws SQLException, Exception{
        DBConnection.makeConnection();
        allAppointments.clear();
        String sqlStatement;
        
        if (customerId != 0){
            //populate with only appointments for customer
            sqlStatement = "SELECT * FROM appointment WHERE customerid = '" + customerId + "';";
        }
        else{
            sqlStatement="SELECT * FROM appointment;";
        }
        
        
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        while(result.next()){
            int appointmentId = result.getInt("appointmentId");
            int custId = result.getInt("customerId");
            int userId = result.getInt("userId");
            String title = result.getString("title");
            String desc = result.getString("description");
            String location = result.getString("location");
            String contact = result.getString("contact");
            String type = result.getString("type");
            String url = result.getString("url");
            String strStart = result.getString("start");
            String strEnd = result.getString("end");
            String strCrDate = result.getString("createDate");
            String crBy = result.getString("createdBy");
            String strLaUp = result.getString("lastUpdate");
            String laUpBy = result.getString("lastUpdateBy");
            LocalDateTime start = LocalDateTime.parse(strStart, format);
            start = convertTime.convert(start, ZoneId.of("UTC"), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.parse(strEnd, format);
            end = convertTime.convert(end, ZoneId.of("UTC"), ZoneId.systemDefault());
            LocalDateTime crDate = LocalDateTime.parse(strCrDate, format);
            crDate = convertTime.convert(crDate, ZoneId.of("UTC"), ZoneId.systemDefault());
            LocalDateTime laUp = LocalDateTime.parse(strLaUp, format);
            laUp = convertTime.convert(laUp, ZoneId.of("UTC"), ZoneId.systemDefault());
            appointment apt = new appointment(appointmentId, custId, userId, title, desc, location, contact, type, url, start, end, crDate, crBy, laUp, laUpBy);
            allAppointments.add(apt);
        }
         
        DBConnection.closeConnection();
        return allAppointments;
    }
    
    length calculateLength = (start, end) -> {
        int length;
        String s, e;
        s = start.toString();
        e = end.toString();
        
        length = Integer.parseInt(e.substring(14,16)) - Integer.parseInt(s.substring(14,16));  
        if (length <= 0){
            length = length + 60;
        }
        return length;
    };
    
    private boolean validFields(){
        boolean isValid = true;
        Alert invalid = new Alert(AlertType.ERROR);
        invalid.setHeaderText("Invalid Field/s!");
        String cause = "";
        
        if (titleField.getText().equals("")){
            isValid = false;
            cause = cause + "Title field cannot be empty. \n";  
        }
        if (descriptionField.getText().equals("")){
            isValid = false;
            cause = cause + "Desciption field cannot be empty. \n";
        }
        
        if (typeField.getText().equals("")){
            isValid = false;
            cause = cause + "Type field cannot be empty. \n";
        }
        if (contactField.getText().equals("")){
            isValid = false;
            cause = cause + "Contact field cannot be empty. \n";
        }
        if (customerField.getText().equals("")){
            isValid = false;
            cause = cause + "Customer ID field cannot be empty. \n";
        }
        if (bxYear.getValue().equals("Year")){
            isValid = false;
            cause = cause + "Year field must be set. \n";
        }
        if (bxMonth.getValue().equals("Month")){
            isValid = false;
            cause = cause + "Month field must be set. \n";
        }
        if (bxDay.getValue().equals("Day")){
            isValid = false;
            cause = cause + "Year field must be set. \n";
        }
        if (bxHour.getValue().equals("Hour")){
            isValid = false;
            cause = cause + "Hour field must be set. \n";
        }
        if (bxMinute.getValue().equals("Minute")){
            isValid = false;
            cause = cause + "Minute field must be set. \n";
        }
        try{
            Integer.parseInt(customerField.getText());
        }catch(NumberFormatException e){
            isValid = false;
            cause = cause + "Customer ID field must be an Integer. \n";
        }
        
        if (!isValid){
            invalid.setContentText(cause);
            invalid.show();
        }
        return isValid;
    }
    
    private boolean validTime(LocalDateTime start, LocalDateTime end, int editId){
        boolean isValid = true;
        String apDate = start.toString().substring(0,10); 
        
        start = convertTime.convert(start, ZoneId.of("UTC"), ZoneId.systemDefault());
        end = convertTime.convert(end, ZoneId.of("UTC"), ZoneId.systemDefault());
        
        for (appointment a: allAppointments){
            //if appointment is the one being changed no reason to check its time 
            if (editId == a.getAppointmentId()){
                //skip
                
            }
            else{
                if (a.getDate().equals(apDate)){
                    //check if start is same
                    if (start.isEqual(a.getStart())){
                        isValid = false;
                        break;
                    }
                    //check if start is between existing start and end
                    if (start.isAfter(a.getStart()) && start.isBefore(a.getEnd())){ 
                        isValid = false;
                        break;
                    }
                     //check if end is between existing start and end
                    if (end.isAfter(a.getStart()) && (end.isEqual(a.getEnd()) || end.isBefore(a.getStart()))){
                        isValid = false;
                        break;
                    }
                    
                }
            }
            
        }
        if (isValid == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("This appointment takes place during an existing appointment");
            alert.show(); 
        }
        return isValid;
    }
    
    private boolean withinBusinessHours(LocalDateTime start){
        boolean isValid = true;
        int hour;
        int businessOpen = 8;
        int businessClose = 18;
         Alert alert = new Alert(AlertType.ERROR);
        String location = bxLocation.getSelectionModel().getSelectedItem().toString();
        
        if (location.equals("Phoenix")){
            start = convertTime.convert(start, ZoneId.of("UTC"), ZoneId.of("America/Phoenix"));
            hour = Integer.parseInt(start.toString().substring(11,13));
            
            if (!((hour >= businessOpen) && (hour <= businessClose))){ 
                alert.setContentText("Appointment must be set within business hours (8-18:00 Local Time) Chosen Time: " + start.toString().substring(12));
                alert.show();
                isValid = false;
            }
            
        }
        else if (location.equals("New York")){
            start = convertTime.convert(start, ZoneId.of("UTC"), ZoneId.of("EST"));
            hour = Integer.parseInt(start.toString().substring(11,13));
            
            if (!((hour >= businessOpen) && (hour <= businessClose))){ 
                alert.setContentText("Appointment must be set within business hours (8-18:00 Local Time) Chosen Time: " + start.toString().substring(12));
                alert.show();
                isValid = false;
            }
        }
        //no need to convert to utc, already in
        else if (location.equals("London")){
            
            hour = Integer.parseInt(start.toString().substring(11,13));
            
            if (!((hour >= businessOpen) && (hour <= businessClose))){ 
                alert.setContentText("Appointment must be set within business hours (8-18:00 Local Time) Chosen Time: " + start.toString().substring(12));
                alert.show();
                isValid = false;
            }
        }
        else {
                        alert.setContentText("Location Error: please try again");
            alert.show();
            isValid = false;
        }
        
        return isValid;
    }
    
    private void resetFields(){
        titleField.setText("");
        bxLocation.setValue("");
        bxLength.setValue("");
        descriptionField.setText("");
        urlField.setText("");
        typeField.setText("");
        contactField.setText("");
        customerField.setText("");
        bxYear.setValue("Year");
        bxMonth.setValue("Month");
        bxDay.setValue("Day");
        bxHour.setValue("Hour");
        bxMinute.setValue("Minute");
    }
    
    @FXML
    private void cancel(){
        edit = false;
        resetFields();
        btnCancel.setDisable(true);
    }
    
    //LAMBDA FOR CHANGING Local Time to UTC zone that DB is stored in, then can be used to swap back for output
    
    convertTime convertTime = (time, zid, zid2) ->{
        
        ZonedDateTime zCurrent = time.atZone(zid);
    
        zCurrent = zCurrent.withZoneSameInstant(zid2);
       
        time = zCurrent.toLocalDateTime();
        
        return time;
    };
    
}
