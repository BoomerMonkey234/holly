package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("category/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Categories {

    //this API method lists all of the categories
    @GET
    @Path("list")
    public String CategoriesList() {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Categories.CategoriesList()");
        //JSON prepared used the Simple JSON library to construct a new JSON array
        JSONArray response = new JSONArray();
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Categories");
            ResultSet results = ps.executeQuery();
            //loops through results whilst there's a next row
            while (results.next() == true) {
                //JSON objects made from each row of results
                JSONObject row = new JSONObject();
                row.put("CategoryID", results.getInt(1));
                row.put("Name", results.getString(2));
                row.put("Description", results.getString(3));
                row.put("URL", results.getString(4));
                //Objects added to JSON array
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message will be shown on GitBash
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @GET
    //PathParam in the method signature to capture a value added to end of URL
    @Path("get/{CategoryID}")
    public String GetCategory(@PathParam("CategoryID") Integer CategoryID) {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Categories.GetCategory() with CategoryID " + CategoryID);
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Prepared statement binds the ? with an actual value, CategoryID, before it's executed
            PreparedStatement ps = Main.db.prepareStatement("SELECT Name, Description FROM Categories WHERE CategoryID = ?");
            ps.setInt(1, CategoryID);
            ResultSet results = ps.executeQuery();
            //no need for array as there's just one item.
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                //adds each value to the item
                response.put("CategoryID", CategoryID);
                response.put("Name", results.getString(1));
                response.put("Description", results.getString(2));
            }
            return response.toString();
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message will be shown on GitBash
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @POST
    //PathParam in method signature to capture a value added to end of URL
    @Path("add")
    //Form data params come from HTML form sent with the fetch().
    public String CategoriesAdd(@FormDataParam("Name") String Name, @FormDataParam("Description") String Description, @FormDataParam("URL") String URL) {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Categories.CategoriesAdd()");
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed. CategoryID automatically generated
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Categories (Name, Description, URL) VALUES (?, ?, ?)");
            ps.setString(1, Name);
            ps.setString(2, Description);
            ps.setString(3, URL);
            ps.execute();
            //this message is shown in GitBash
            return "{\"OK\": \"Added category.\"}";
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message is shown in GitBash
            return "{\"Error\": \"Unable to create new item, please see server console for more info\"}";
        }
    }

    @POST
    //PathParam in method signature to capture a value added to end of URL
    @Path("delete/{CategoryID}")
    public String DeleteCategory(@PathParam("CategoryID") Integer CategoryID) throws Exception {
        System.out.println("Invoked Categories.DeleteCategory");
        //First checks to see if the CategoryID exists
        if (CategoryID==null) {
            //if not the method finishes
            throw new Exception("CategoryID is missing in the HTTP request's URL");
        }
        //try...catch is used to find any errors as I'm using a file external to the program
        try {
            //Uses prepared statements to avoid SQL injection. Parameters treated like data and can be executed.
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Categories WHERE CategoryID=?");
            ps.setInt(1, CategoryID);
            ps.execute();
            //this message is shown in GitBash
            return "{\"OK\": \"Deleted category.\"}";
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            //this message is shown in GitBash
            return "{\"Error\": \"Unable to delete item, please see server console for more info\"}";
        }
    }



}