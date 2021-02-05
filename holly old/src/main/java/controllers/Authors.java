package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("author/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Authors {
    @GET
    //uses get to read
    @Path("list")
    public String UsersList() {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Authors.AuthorsList()");
        //JSON prepared using SimpleJSON Library. Construct new JSON array, and creates objects from each new row
        JSONArray response = new JSONArray();
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Authors");
            ResultSet results = ps.executeQuery();
            //loops through results until there's no next row
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AuthorID", results.getInt(1));
                row.put("FirstName", results.getString(2));
                row.put("LastName", results.getString(3));
                row.put("Bio", results.getString(4));
                response.add(row);
            }
            //this message will be shown in GitBash
            return response.toString();
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message will be shown in GitBash
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @GET
    //PathParam in method signature to capture a value added to end of URL
    @Path("get/{AuthorID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)

    public String GetAuthor(@PathParam("AuthorID") Integer AuthorID) {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Authors.GetAuthor() with AuthorID " + AuthorID);
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT FirstName, LastName, Bio FROM Authors WHERE AuthorID = ?");
            ps.setInt(1, AuthorID);
            ResultSet results = ps.executeQuery();
            //No need for array as there's only one item
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                //adds each data to the item
                response.put("AuthorID", AuthorID);
                response.put("First Name", results.getString(1));
                response.put("Last Name", results.getString(2));
                response.put("Bio", results.getString(3));
            }
            return response.toString();
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }


    @POST
    //PathParam in method signature to capture a value added to end of URL
    @Path("delete/{AuthorID}")
    public String DeleteCategory(@PathParam("AuthorID") Integer AuthorID) throws Exception {
        System.out.println("Invoked Authors.DeleteAuthor()");
        //First checks to see if the AuthorID exists
        if (AuthorID==null) {
            //if not the method finishes
            throw new Exception("AuthorID is missing in the HTTP request's URL");
        }
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed.
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Authors WHERE AuthorID=?");
            ps.setInt(1, AuthorID);
            ps.execute();
            //this message is shown in GitBash
            return "{\"OK\": \"Deleted author.\"}";
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message is shown in GitBash
            return "{\"Error\": \"Unable to delete item, please see server console for more info\"}";
        }
    }

    @POST
    //PathParam in method signature to capture a value added to end of URL
    @Path("add")
    //Form data params come from HTML form sent with the fetch().
    public String AuthorsAdd(@FormDataParam("FirstName") String FirstName, @FormDataParam("LastName") String LastName, @FormDataParam("Bio") String Bio) {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Authors.AuthorsAdd()");
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed. AuthorID automatically generated
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Authors (FirstName, LastName, Bio) VALUES (?, ?, ?)");
            ps.setString(1, FirstName);
            ps.setString(2, LastName);
            ps.setString(3, Bio);
            ps.execute();
            //this message is shown in GitBash
            return "{\"OK\": \"Added author.\"}";
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message is shown in GitBash
            return "{\"Error\": \"Unable to add new item, please see server console for more info\"}";
        }
    }
}




