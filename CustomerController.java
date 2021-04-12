/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import DAO.DBConnection;
import DAO.Query;
import Model.customer;
import Model.user;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jeryn
 */
public class CustomerController implements Initializable {

    @FXML
    private TableView<customer> customerTable;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField addressField;
    
    @FXML 
    private TextField addressField2;
    
    @FXML
    private TextField cityField;
    
    @FXML
    private TextField countryField;
    
    @FXML 
    private TextField postalField;
    
    @FXML 
    private TextField phoneField;
    
    @FXML
    private Button btnUpdate;
    
    ObservableList<customer> allCustomers = FXCollections.observableArrayList();
    
    private String username;
    
    private user currentUser;
    
    private int addressId;
    
    private customer currentCustomer;
    
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    
    ZoneId zid = ZoneId.systemDefault();
    ZoneId utc = ZoneId.of("UTC");
    LocalDateTime ldtUpdate;
    ZonedDateTime zdt;
    ZonedDateTime utcTime;
    String now;
    
    
    CustomerController(user user) { //keeps logged-in user
      this.username = user.getUserName();
      this.currentUser = user;
    }
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){

        try {
            updateTable();
        } catch (Exception ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    
    
    @FXML
    private void addCustomer() throws SQLException, Exception{
        
        if (validFields()){
            addressId = validateAddress();
            ldtUpdate = LocalDateTime.now();
            zdt = ZonedDateTime.of(ldtUpdate, zid);
            utcTime = zdt.withZoneSameInstant(utc);
            ldtUpdate = utcTime.toLocalDateTime();
            now = ldtUpdate.format(format);

            DBConnection.makeConnection();
            String sqlStatement ="INSERT INTO customer(customerName, addressId, acti"
                    + "ve, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ('"
                    + nameField.getText() + "', '" + addressId + "', '1', '" + now + "', '" + username + "', '" + now + "', '"
                    + username + "');";
            try{
            Query.makeQuery(sqlStatement);

            DBConnection.closeConnection();

            updateTable();

            resetFields();

            }catch(Exception e){
                System.out.println(e.getMessage());
            };
        }
    }
    
    @FXML
    private void editCustomer() throws SQLException, Exception{
        
        DBConnection.makeConnection();
        
         //declare container variables
         String name= ""; 
         String ad= "";
         String ad2= "";
         String po= "";
         String ph= "";
         String ci= "";
         String co = "";
         int adid = 0;
         int ciid = 0;
         int coid = 0;
         
        //get name and addressId for next search
        currentCustomer = customerTable.getSelectionModel().getSelectedItem();
        String sqlStatement ="SELECT * FROM customer WHERE customerId = '" 
                + currentCustomer.getCustomerId() + "';";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();

        while (result.next()){
        
            name = result.getString("customerName");
            adid = result.getInt("addressId");
        }
        DBConnection.closeConnection();
        DBConnection.makeConnection();
        //get address info and cityId for next search
        sqlStatement = "SELECT address, address2, cityId, postalCode, phone FROM address WHERE addressId = '" + adid + "';";
        Query.makeQuery(sqlStatement);
        result = Query.getResult();
        
        while (result.next()){
            ad = result.getString("address");
            ad2 = result.getString("address2");
            po = result.getString("postalCode");
            ph = result.getString("phone");
            ciid = result.getInt("cityId");
        }
        
        DBConnection.closeConnection();
        DBConnection.makeConnection();
        
        //get city and countryId for next search
        sqlStatement = "SELECT city, countryId FROM city WHERE cityId = '" + ciid + "';";
        Query.makeQuery(sqlStatement);
        result = Query.getResult();
        while (result.next()){
        
            ci = result.getString("city");
            coid = result.getInt("countryId");
        }
        
        DBConnection.closeConnection();
        DBConnection.makeConnection();
        
        sqlStatement = "SELECT country FROM country WHERE countryId = '" + coid + "';";
        Query.makeQuery(sqlStatement);
        result = Query.getResult();
        while (result.next()){
        
            co = result.getString("country");
        }
        
        DBConnection.closeConnection();
        
        //set fields
        nameField.setText(name);
        addressField.setText(ad);
        addressField2.setText(ad2);
        postalField.setText(po);
        phoneField.setText(ph);
        cityField.setText(ci);
        countryField.setText(co);
        
        btnUpdate.setDisable(false);
        
    }
    
    @FXML
    private void updateCustomer() throws SQLException, Exception{
        
        if (validFields()){
            currentCustomer = customerTable.getSelectionModel().getSelectedItem();
            addressId = validateAddress();

            ldtUpdate = LocalDateTime.now();
            zdt = ZonedDateTime.of(ldtUpdate, zid);
            utcTime = zdt.withZoneSameInstant(utc);
            ldtUpdate = utcTime.toLocalDateTime();
            now = ldtUpdate.format(format);


            DBConnection.makeConnection();
            String sqlStatement ="UPDATE customer SET customerName = '" + nameField.getText() + 
                    "', addressId = '" + addressId + "', active = '1', lastUpdate = '" + now
                    + "', lastUpdateBy = '" + username + "' WHERE customerId = '"
                    + currentCustomer.getCustomerId() + "';";



            Query.makeQuery(sqlStatement);

            DBConnection.closeConnection();

            updateTable();


            btnUpdate.setDisable(true);
            resetFields();
        }
        
    }
    
    @FXML
    private void deleteCustomer() throws SQLException, Exception{
        DBConnection.makeConnection();
        currentCustomer = customerTable.getSelectionModel().getSelectedItem();
        String sqlStatement ="DELETE FROM customer WHERE customerId = '"
                + currentCustomer.getCustomerId() + "';";
        Query.makeQuery(sqlStatement);
        DBConnection.closeConnection();
        updateTable();
        
    }
    
    @FXML
    private void customerAppointment(MouseEvent event) throws IOException{
        
        currentCustomer = customerTable.getSelectionModel().getSelectedItem();
        int customerId = currentCustomer.getCustomerId();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Appointment.fxml"));
        View_Controller.AppointmentController controller = new View_Controller.AppointmentController(currentUser, customerId);
        loader.setController(controller);
        Parent root = loader.load();
        Scene modify = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(modify);
        stage.setResizable(false);
        stage.show();
    }
    
    private ObservableList<customer> getAllCustomers() throws SQLException, Exception{
        DBConnection.makeConnection();
        
        allCustomers.clear();
        String sqlStatement="SELECT * FROM customer;";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        while(result.next()){
            int custId=result.getInt("customerId");
            String custName=result.getString("customerName");
            int addId = result.getInt("addressId");
            boolean act = result.getBoolean("active");
            String strCrDate = result.getString("createDate");
            String crBy = result.getString("createdBy");
            String strLaUp = result.getString("lastUpdate");
            String laUpBy = result.getString("lastUpdateBy");
            LocalDateTime crDate = LocalDateTime.parse(strCrDate, format);
            LocalDateTime laUp = LocalDateTime.parse(strLaUp, format);
            customer custom = new customer(custId, custName, addId, act, crDate, crBy, laUp, laUpBy);
            allCustomers.add(custom);
        }
         
        DBConnection.closeConnection();
         return allCustomers;
    }

    
    
    private int validateAddress() throws SQLException, Exception {
        
        
        int city = validateCity(cityField.getText()); //check City
        ldtUpdate = LocalDateTime.now();
        zdt = ZonedDateTime.of(ldtUpdate, zid);
        utcTime = zdt.withZoneSameInstant(utc);
        ldtUpdate = utcTime.toLocalDateTime();
        now = ldtUpdate.format(format);
        
        
        DBConnection.makeConnection();
        
        String ad;
        int adId = 1;
        String ad2 = "";
        int ciId = 0;
        String post = "";
        String phn = "";
        boolean exists = false;
        
        
        String sqlStatement = "SELECT * FROM address;";
        Query.makeQuery(sqlStatement);
        ResultSet resultAddress = Query.getResult();
        
        //check if address already exists in table
        while (resultAddress.next()){
            ad = resultAddress.getString("address");
            if (!exists){
                if (ad.equals(addressField.getText())){
                    adId = resultAddress.getInt("addressId");
                    ad2 = resultAddress.getString("address2");
                    post = resultAddress.getString("postalCode");
                    phn = resultAddress.getString("phone");
                    ciId = resultAddress.getInt("cityId");
                    
                    exists = true;
                    
                }
            }
            if (exists){
                    addressId = adId;
                    
                //check that address needs update
                if (ad.equals(addressField.getText()) && ad2.equals(addressField2.getText()) 
                    && post.equals(postalField.getText()) && phn.equals(phoneField.getText())
                    && (city == ciId)){

                }
                else{

                    sqlStatement = "UPDATE address SET address = '" + addressField.getText()
                            + "', address2 = '" + addressField2.getText() + "',"
                            + " cityId = '" + city + "', postalCode = '"
                            + postalField.getText() + "', phone = '" + phoneField.getText()
                            + "', lastUpdate = '" + now + "', lastUpdateBy = '" + username + "' WHERE "
                            + "addressId = '" + addressId + "';";

                    Query.makeQuery(sqlStatement);

                }
                
            }
            
        }
        
        DBConnection.closeConnection();
        
       
        //add address if not exists
        if (!exists){
            DBConnection.makeConnection();
            sqlStatement = "INSERT INTO address(address, address2, cityId, postalCode"
                    + ", phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + " VALUES ('" + addressField.getText() + "', '" + addressField2.getText()
                    + "', '" + city + "', '" + postalField.getText() + "', '" + phoneField.getText()
                    + "', '" + now + "', '" + username + "', '" + now + "', '" + username + "');";
            
            Query.makeQuery(sqlStatement);
            
            //get addressId;
             try {
                sqlStatement = "SELECT addressId FROM address WHERE createDate = '" + now + "';";
                
                Query.makeQuery(sqlStatement);
                resultAddress = Query.getResult();
                while(resultAddress.next()){
                    addressId = resultAddress.getInt("addressId");
                }
            }catch(Exception e){
                e.getMessage();
            }

            DBConnection.closeConnection();
        }  
        

       
        return addressId;
    }

    private int validateCity(String city) throws SQLException, Exception{
        
        
        
        String sqlStatement = "SELECT cityId FROM city WHERE city = '" + city + "';";
        int cityId = 0;
        int countryId = validateCountry(countryField.getText());
        
        ldtUpdate = LocalDateTime.now();
        zdt = ZonedDateTime.of(ldtUpdate, zid);
        utcTime = zdt.withZoneSameInstant(utc);
        ldtUpdate = utcTime.toLocalDateTime();
        now = ldtUpdate.format(format);
        
        
        DBConnection.makeConnection();
        
        Query.makeQuery(sqlStatement);
        ResultSet resultCity = Query.getResult();
        
        if (resultCity.first()){
            cityId = resultCity.getInt("cityId");
            
        }
        else{
            //add to db
            DBConnection.closeConnection();
            DBConnection.makeConnection();
            sqlStatement = "INSERT INTO city (city, countryId, createDate, "
                    + "createdBy, lastUpdate, lastUpdateBy) VALUES ('" + cityField.getText()
                    + "', '" + countryId + "','" + now + "', '" + username + "', '" + now + "', '" + username + "');";
            
            Query.makeQuery(sqlStatement);
            
            //get id
            try {
                sqlStatement = "SELECT cityId FROM city WHERE city = '" + city + "';";
                Query.makeQuery(sqlStatement);
                resultCity = Query.getResult();
                while (resultCity.next()){
                    cityId = resultCity.getInt("cityId");
                }
            }catch(Exception e){
                e.getMessage();        
            }

                        
        }
        DBConnection.closeConnection();
        
        
        return cityId;
    }
    
    private int validateCountry(String country) throws SQLException, Exception{
        
        DBConnection.makeConnection();
        
        String sqlStatement = "SELECT countryId FROM country WHERE country = '"
                + country + "';";
        int countryId = 0;
        ldtUpdate = LocalDateTime.now();
        zdt = ZonedDateTime.of(ldtUpdate, zid);
        utcTime = zdt.withZoneSameInstant(utc);
        ldtUpdate = utcTime.toLocalDateTime();
        now = ldtUpdate.format(format);
        
        
        Query.makeQuery(sqlStatement);
        ResultSet resultCountry = Query.getResult();
        
        if (resultCountry.first()){
            countryId = resultCountry.getInt("countryId");
            
        }
        else{
            //add to db
            DBConnection.closeConnection();
            DBConnection.makeConnection();
            sqlStatement = "INSERT INTO country(country, createDate, createdBy, lastUpdate"
                    + ", lastUpdateBy) VALUES ('" + countryField.getText() + "','" + now + "', '" 
                    + username + "', '" + now + "', '" + username + "');";
            Query.makeQuery(sqlStatement);
            
            //get id
            sqlStatement = "SELECT countryId FROM country WHERE country = '" + country + "';";
            try {
                Query.makeQuery(sqlStatement);
                resultCountry = Query.getResult();
                while (resultCountry.next()){
                    countryId = resultCountry.getInt("countryId");
                }
            }catch(Exception e){
                e.getMessage();
                
            }
            
        }

        DBConnection.closeConnection();
        
        return countryId;
    }

    private void updateTable() throws Exception {
        
        allCustomers = getAllCustomers();
        customerTable.setItems(allCustomers);
        
    }
    
    private void resetFields(){
        
         //reset fields
        nameField.setText("");
        addressField.setText("");
        addressField2.setText("");
        postalField.setText("");
        phoneField.setText("");
        cityField.setText("");
        countryField.setText("");
    }
    
    @FXML
    private void toMain(MouseEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/Main.fxml"));
        View_Controller.MainController controller = new View_Controller.MainController(currentUser);
        loader.setController(controller);
        Parent root = loader.load();
        Scene modify = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(modify);
        stage.setResizable(false);
        stage.show();
    }
    
    private boolean validFields(){
        boolean isValid = true;
        Alert invalid = new Alert(AlertType.ERROR);
        invalid.setHeaderText("Invalid field/s!");
        String cause = "";
        
        if (nameField.getText().equals("")){
            isValid = false;
            cause = cause + "Name field cannot be empty. \n";
        }
        if (addressField.getText().equals("")){
            isValid = false;
            cause = cause + "Address field cannot be empty. \n";
        }
        if (cityField.getText().equals("")){
            isValid = false;
            cause = cause + "City field cannot be empty. \n";
        }
        if (countryField.getText().equals("")){
            isValid = false;
            cause = cause + "Country field cannot be empty. \n";
        }
        if (postalField.getText().equals("")){
            isValid = false;
            cause = cause + "Postal Code field cannot be empty. \n";
        }
        if (phoneField.getText().equals("")){
            isValid = false;
            cause = cause + "Phone field cannot be empty. \n";
        }
        if (!isValid){
            invalid.setContentText(cause);
            invalid.show();
        }
        return isValid;
    }
}

