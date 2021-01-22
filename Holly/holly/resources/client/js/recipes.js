function pageLoad(){
    listRecipes();
}

function listRecipes(){
    console.log("Invoked listRecipes() ");
    let url = "/recipe/list";

    fetch(url, {
        method: 'get',
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            let recipesHTML = `<div>`;
            recipesHTML += `<table id="tRecipes"><th>Recipe ID</th><th>Recipe Name</th><th>Category</th><th>URL</th><th>Author</th>`;
            for(let r of response.recipes){
                recipesHTML += `<tr class="recipe_${r.RecipeID}">`

                    + `<td>${r.RecipeID}</td>`
                    + `<td>${r.RecipeName}</td>`
                    + `<td>${r.CategoryName}</td>`
                    + `<td><a href="${r.pathURL}">${r.pathURL}</a></td>`
                    + `<td>${r.Author}</td>`
                    + `<td>`
                    + `<div class="rate">`
                    + `<input type="radio" id="star5" name="rate" value = "5"/>`
                    + `<label for="star5" title="text">5 stars</label>`
                    + `<input type="radio" id="star4" name="rate" value="4"/>`
                    + `<label for="star4" title="text">4 stars</label>`
                    + `<input type="radio" id="star3" name="rate" value="3"/>`
                    + `<label for="star3" title="text">3 stars</label>`
                    + `<input type="radio" id="star2" name="rate" value="2"/>`
                    + `<label for="star2" title="text">2 stars</label>`
                    + `<input type="radio" id="star1" name="rate" value="1"/>`
                    + `<label for="star1" title="text">1 star</label>`
                    +`</div>`
                    + `</td>`
                    + `<td id="recipe"+${r.RecipeID}><button onclick="fetchRecipe(${r.RecipeID})">Show Recipe</button> </td>`
                    + `<td><button onclick="deleteRecipe(${r.RecipeID})">Delete Recipe</button></td>`

                    + `</tr>`;
            }
            recipesHTML += `</div>`;
            document.getElementById('recipes').innerHTML = recipesHTML;
        }
    });
}

function fetchRecipe(id){
    document.getElementById('recipe'+id);
    console.log("invoked fetchRecipe() with button 'recipe"+id+"'");
    //let recipe = '{"id":id}';
    let recipe = new FormData();
    recipe.append("RecipeID",id);
    let url = "/recipe/get";

    fetch(url, {
        method: 'post',
        body: recipe,
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            let recipeDetailsHTML = "<h2>Ingredients</h2>";
            recipeDetailsHTML += `<div id="ingredients"><table>`;
            for(let m of response.methods){
                recipeDetailsHTML += `<tr><td>${m}</td></tr>`;
            }
            recipeDetailsHTML += `</table></div>`;
            recipeDetailsHTML += `<h2>Methods</h2><div id="methods"><table>`;

            for(i of response.ingredients){
                recipeDetailsHTML += `<tr><td>${i}</td></tr>`;
            }
            recipeDetailsHTML += `</table></div><button onclick="listRecipes()">Back</button>`;

            document.getElementById('recipes').innerHTML=recipeDetailsHTML;
        }
    });

}

function openAddRecipe(){
    window.open("AddRecipe.html", "_self");
}

function back(){
    window.open("recipes.html","_self")
}

function addRecipe(){
    console.log("Invoked addRecipe()");
    let formData = new FormData(document.getElementById('recipes'));
    let url = "/recipe/add";
    fetch(url, {
        method: 'POST',
        body: formData,
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            alert(JSON.stringify(response));
            window.open("recipes.html","_self")
        }
    });
}



function deleteRecipe(){
    console.log("Invoked deleteRecipe()");

}

