package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;



@Path("users/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Users{
    //get to read data
    @GET
    @Path("list")
    public String UsersList() {
        //additional print statements make debugging easier. Appears in server console
        System.out.println("Invoked Users.UsersList()");
        //new JSON array constructed
        JSONArray response = new JSONArray();
        //try...catch as I'm working with external file to program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Name FROM Users");
            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                //JSON objects made from each row and inserted into array
                JSONObject row = new JSONObject();
                row.put("UserID", results.getInt(1));
                row.put("UserName", results.getString(2));
                response.add(row);
            }
            //this message will be shown on GitBash
            return response.toString();
        } catch (Exception exception) {
            //additional print statements make debugging easier. Appears in server console
            System.out.println("Database error: " + exception.getMessage());
            //this message will be shown on GitBash
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @GET
    //PathParam in method signature to capture a value on end of URL
    @Path("get/{UserID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)

    public String GetUser(@PathParam("UserID") Integer UserID) {
        //additional print statements make debugging easier. Appears in server console
        System.out.println("Invoked Users.GetUser() with UserID " + UserID);
        //try...catch as I'm working with external file to program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed
            PreparedStatement ps = Main.db.prepareStatement("SELECT Name, Email, SessionToken FROM Users WHERE UserID = ?");
            ps.setInt(1, UserID);
            ResultSet results = ps.executeQuery();
            //No need for array as only one item
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                response.put("UserID", UserID);
                response.put("Name", results.getString(1));
                response.put("Email", results.getString(2));
                response.put("Session Token", results.getString(3));
            }
            //this message is shown in GitBash
            return response.toString();
        } catch (Exception exception) {
            //additional print statements make debugging easier. Appears in server console
            System.out.println("Database error: " + exception.getMessage());
            //this message will be shown on GitBash
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("add")
    public String UsersAdd(@FormDataParam("Email") String Email, @FormDataParam("Name") String Name, @FormDataParam("Password") String Password) {
        //additional print statements make debugging easier. Appears in server console
        System.out.println("Invoked Users.UsersAdd()");
        //try...catch as I'm working with external file to program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (Email, Name, Admin, Password) VALUES (?, ?, ?, ?)");
            ps.setString(1, Email);
            ps.setString(2, Name);
            ps.setInt(3, 0);
            ps.setString(4, Password);
            ps.execute();
            //this message will be shown on GitBash
            return "{\"OK\": \"Added user.\"}";
        } catch (Exception exception) {
            //additional print statements make debugging easier. Appears in server console
            System.out.println("Database error: " + exception.getMessage());
            //this message will be shown on GitBash
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }
    }

    @POST
    //PathParam in method signature to capture a value added to end of URL
    @Path("delete/{UserID}")
    public String DeleteUser(@PathParam("UserID") Integer UserID) throws Exception {
        System.out.println("Invoked Users.DeleteUser");
        //First checks to see if the UserID exists
        if (UserID==null) {
            //if not the method finishes
            throw new Exception("UserID is missing in the HTTP request's URL");
        }
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed.
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Users WHERE UserID=?");
            ps.setInt(1, UserID);
            ps.execute();
            //this message is shown in GitBash
            return "{\"OK\": \"Deleted user.\"}";
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message is shown in GitBash
            return "{\"Error\": \"Unable to delete item, please see server console for more info\"}";
        }
    }

    @POST
    @Path("login")
    public String UsersLogin(@FormDataParam("Email") String Email, @FormDataParam("Password") String Password) {
        //additional print statements make debugging easier. Appears in server console
        System.out.println("Invoked loginUser() on path users/login");
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT Password FROM Users WHERE Email = ?");
            ps1.setString(1, Email);
            ResultSet loginResults = ps1.executeQuery();
            if (loginResults.next() == true) {
                String correctPassword = loginResults.getString(1);
                if (Password.equals(correctPassword)) {
                    String SessionToken = UUID.randomUUID().toString();
                    PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET SessionToken = ? WHERE Email = ?");
                    ps2.setString(1, SessionToken);
                    ps2.setString(2, Email);
                    ps2.executeUpdate();
                    JSONObject userDetails = new JSONObject();
                    userDetails.put("Email", Email);
                    userDetails.put("SessionToken", SessionToken);
                    //this message will be shown on GitBash
                    return userDetails.toString();
                } else {
                    //this message will be shown on GitBash
                    return "{\"Error\": \"Incorrect password!\"}";
                }
            } else {
                //this message will be shown on GitBash
                return "{\"Error\": \"Incorrect username.\"}";
            }
        } catch (Exception exception) {
            //additional print statements make debugging easier. Appears in server console
            System.out.println("Database error during /users/login: " + exception.getMessage());
            //this message will be shown on GitBash
            return "{\"Error\": \"Server side error!\"}";
        }
    }

    //returns the userID with the token value
    public static int validToken(String SessionToken) {
        //additional print statements make debugging easier. Appears in server console
        System.out.println("Invoked User.validateToken(), Token value " + SessionToken);
        try {
            PreparedStatement statement = Main.db.prepareStatement("SELECT UserID FROM Users WHERE SessionToken = ?");
            statement.setString(1, SessionToken);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("userID is " + resultSet.getInt("UserID"));
            //this message will be shown on GitBash
            return resultSet.getInt("UserID");  //Retrieve by column name  (should really test we only get one result back!)
        } catch (Exception e) {
            //additional print statements make debugging easier. Appears in server console
            System.out.println(e.getMessage());
            //this message will be shown on GitBash
            return -1;  //rogue value indicating error
        }
    }

    @POST
    @Path("logout")
    public static String logout(@CookieParam("SessionToken") String SessionToken){
        try{
            //additional print statements make debugging easier. Appears in server console
            System.out.println("users/logout "+ SessionToken);
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID FROM Users WHERE SessionToken=?");
            ps.setString(1, SessionToken);
            ResultSet logoutResults = ps.executeQuery();
            if (logoutResults.next()){
                int UserID = logoutResults.getInt(1);
                //Set the token to null to indicate that the user is not logged in
                PreparedStatement ps1 = Main.db.prepareStatement("UPDATE Users SET SessionToken = NULL WHERE UserID = ?");
                ps1.setInt(1, UserID);
                ps1.executeUpdate();
                //this message will be shown on GitBash
                return "{\"status\": \"OK\"}";
            } else {
                //this message will be shown on GitBash
                return "{\"error\": \"Invalid token!\"}";

            }
        } catch (Exception ex) {
            //additional print statements make debugging easier. Appears in server console
            System.out.println("Database error during /users/logout: " + ex.getMessage());
            //this message will be shown on GitBash
            return "{\"error\": \"Server side error!\"}";
        }
    }










}
