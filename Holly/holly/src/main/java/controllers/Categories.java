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
    @GET
    @Path("list")
    public String CategoriesList() {
        System.out.println("Invoked Users.UsersList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Categories");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("CategoryID", results.getInt(1));
                row.put("Name", results.getString(2));
                row.put("Description", results.getString(3));
                row.put("URL", results.getString(4));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @GET
    @Path("get/{CategoryID}")
    public String GetCategory(@PathParam("CategoryID") Integer CategoryID) {
        System.out.println("Invoked Categories.GetCategory() with CategoryID " + CategoryID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Name FROM Categories WHERE CategoryID = ?");
            ps.setInt(1, CategoryID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                response.put("CategoryID", CategoryID);
                response.put("Name", results.getString(1));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

}