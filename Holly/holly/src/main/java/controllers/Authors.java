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
    @Path("list")
    public String UsersList() {
        System.out.println("Invoked Users.AuthorsList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM Authors");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("AuthorID", results.getInt(1));
                row.put("FirstName", results.getString(2));
                row.put("LastName", results.getString(3));
                row.put("Bio", results.getString(4));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @GET
    @Path("get/{AuthorID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)

    public String GetUser(@PathParam("AuthorID") Integer AuthorID) {
        System.out.println("Invoked Users.GetUser() with UserID " + AuthorID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT FirstName, LastName, Bio FROM Authors WHERE AuthorID = ?");
            ps.setInt(1, AuthorID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("AuthorID", AuthorID);
                response.put("First Name", results.getString(1));
                response.put("Last Name", results.getString(2));
                response.put("Bio", results.getString(3));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }


    @GET
    @Path("delete/{AuthorID}")
    public String DeleteAuthor(@PathParam("AuthorID") Integer AuthorID, @CookieParam("SessionToken") Cookie SessionToken) throws Exception {
        System.out.println("Invoked Authors.DeleteAuthor()");
        if (AuthorID == null) {
            throw new Exception("No AuthorID specified");
        }
        PreparedStatement ps = Main.db.prepareStatement("SELECT Admin FROM Users WHERE SessionToken = ?");
        ps.setString(1, SessionToken.getValue());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            if (rs.getBoolean(1) == false) {
                return("{\"Success\":\"User is not an admin - access denied\"}");
            } else {

                try {
                    PreparedStatement Ps = Main.db.prepareStatement("DELETE FROM Authors WHERE AuthorID = ?");
                    Ps.setInt(1, AuthorID);
                    Ps.execute();

                } catch (Exception exception) {
                    System.out.println("Database error: " + exception.getMessage());
                    return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
                }
            }
        }
        return "{\"OK\": \"Author deleted\"}";}
}




