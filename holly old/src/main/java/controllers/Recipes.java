package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.JSONP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("recipe/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class Recipes {
    //uses @GET to read data (SQL select)
    @GET
    @Path("list")

    public String RecipeList() {
        //additional print statement makes debugging easier. Appears on server console
        System.out.println("Invoked Recipes.RecipeList()");
        //JSON prepared using the Simple JSON library to construct new JSON array
        JSONArray recipe = new JSONArray();
        //try...catch is used to find any errors as I am using a file external to the program
        try {
            //uses prepared statements to avoid SQL injection. Parameters treated like data and can't be executed
            PreparedStatement ps = Main.db.prepareStatement("SELECT Recipes.RecipeID, Recipes.Name, Recipes.CategoryID ,Recipes.URL, Recipes.AuthorID FROM Recipes ORDER BY Recipes.Name");
            ResultSet results = ps.executeQuery();

            //loops through results while there is a next row
            while (results.next()) {
                //JSON objects made from each row of the results
                JSONObject row = new JSONObject();
                row.put("RecipeID", results.getInt(1));
                //finds corresponding category from Categories table using specified CategoryID
                PreparedStatement getCategory = Main.db.prepareStatement("SELECT Name FROM Categories WHERE CategoryID = ?");

                getCategory.setInt(1, results.getInt(3));
                ResultSet rsCat = getCategory.executeQuery();
                if (!rsCat.next()) {
                    return "{\"Error\": \"Unable to find category.\"}";
                }
                //finds corresponding category from Authors table using specified AuthorID
                PreparedStatement getAuthorID = Main.db.prepareStatement("SELECT FirstName,LastName FROM Authors WHERE AuthorID = ?");
                getAuthorID.setInt(1, results.getInt(5));
                ResultSet rsAuth = getAuthorID.executeQuery();
                if (!rsAuth.next()) {
                    return "{\"Error\": \"Unable to find author.\"}";
                }
                row.put("Name", results.getString(2));
                row.put("CategoryName", rsCat.getString(1));
                row.put("pathURL", results.getString(4));
                row.put("Author", rsAuth.getString(1) + " " + rsAuth.getString(2));
                //Objects added to JSONArray
                recipe.add(row);
            }
            JSONObject response = new JSONObject();
            response.put("recipes", recipe);
            return response.toString();
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @POST
    @Path("get")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)

    public String GetRecipe(@FormDataParam("RecipeID") Integer RecipeID) {
        System.out.println("Invoked Recipes.GetRecipe() with RecipeID of " + RecipeID);
        JSONObject jso = new JSONObject();
        JSONArray ingredients = new JSONArray();
        JSONArray methods = new JSONArray();
        try {
            PreparedStatement psIngredients = Main.db.prepareStatement("SELECT Ingredient FROM Ingredients WHERE RecipeID=?");
            PreparedStatement psMethods = Main.db.prepareStatement("SELECT Method FROM Methods WHERE RecipeID = ?");
            psIngredients.setInt(1,RecipeID);
            psMethods.setInt(1,RecipeID);
            ResultSet rsIngredients = psIngredients.executeQuery();
            ResultSet rsMethods = psMethods.executeQuery();
            /*if(!rsIngredients.next()){
                return "{\"Error\": \"Unable to find matching ingredients for this recipe.\"}";
            }else if(!rsMethods.next()){
                return "{\"Error\": \"Unable to find matching methods for this recipe.\"}";
            }*/
            while(rsIngredients.next()){
                ingredients.add(rsIngredients.getString(1));
            }
            while(rsMethods.next()){
                methods.add(rsMethods.getString(1));
            }
            JSONObject response = new JSONObject();
            response.put("ingredients",ingredients);
            response.put("methods", methods);
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

    @POST
    //uses a PathParam to capture a value added to the end of the URL
    @Path("delete/{RecipeID}")
    public String DeleteRecipe(@PathParam("RecipeID") Integer RecipeID) throws Exception {
        System.out.println("Invoked Recipes.DeleteRecipe()");
        if (RecipeID == null) {
            throw new Exception("RecipeID is missing in the HTTP request's URL.");
        }
        //try...catch is used to catch any errors that may appear in the external program
        try {
            //prepared statement selects the recipe, binding the ? to the actual value, RecipeID, before it's executed
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Recipes WHERE RecipeID = ?");
            ps.setInt(1, RecipeID);
            ps.execute();
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Recipe successfully deleted");
            return "{\"OK\": \"Recipe deleted\"}";
        } catch (Exception exception) {
            //additional print statement makes debugging easier. Appears on server console
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String addRecipe(@FormDataParam("Name") String name,@FormDataParam("category") String category, @FormDataParam("description") String description, @FormDataParam("url") String url, @FormDataParam("author") String author){
        try{
            System.out.println("Invoked /recipe/add");
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Recipes(Name, CategoryID, Description, URL, AuthorID) VALUES (?,?,?,?,?)");
            PreparedStatement getCategoryID = Main.db.prepareStatement("SELECT CategoryID FROM Categories WHERE Name = ?");
            getCategoryID.setString(1,category);
            ResultSet rsCat = getCategoryID.executeQuery();
            if(!rsCat.next()){
                return "{\"Error\": \"Unable to find category.\"}";
            }
            String authorNames[] = author.split(" ");
            PreparedStatement getAuthorID = Main.db.prepareStatement("SELECT AuthorID FROM Authors WHERE FirstName = ? AND LastName = ?");
            getAuthorID.setString(1,authorNames[0]);
            getAuthorID.setString(2,authorNames[1]);
            ResultSet rsAuth = getAuthorID.executeQuery();
            if(!rsAuth.next()){
                return "{\"Error\": \"Unable to find author.\"}";
            }
            ps.setString(1,name);
            ps.setInt(2, rsCat.getInt(1));
            ps.setString(3,description);
            ps.setString(4,url);
            ps.setInt(5,rsAuth.getInt(1));
            ps.executeUpdate();
            return "{\"Success\": \"Recipe successfully added\"}";
        }catch(Exception e){
            System.out.println(e.getMessage());
            return "{\"Error\": \"Unable to add recipe.\"}";
        }
    }
}


