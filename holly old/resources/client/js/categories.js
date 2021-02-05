function pageLoad(){
    listCategories();
}

function listCategories(){
    console.log("Invoked listCategories() ");
    let url = "/category/list";

    fetch(url, {
        method: 'get',
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            let categoriesHTML = `<div>`;
            categoriesHTML += `<table id="tCategories"><th>Category Name</th><th>Description</th><th>More</th><th>Delete</th>`;
            for(let r of response.recipes){
                categoriesHTML += `<tr class="category_${r.CategoryID}">`

                    //+ `<td>${r.RecipeID}</td>`
                    + `<td>${r.Name}</td>`
                    + `<td>${r.Description}</td>`
                    + `<td><a href="${r.pathURL}">${r.pathURL}</a></td>`
                    + `<td id="recipe"+${r.RecipeID}><button onclick="fetchCategory(${r.CategoryID})">Show Category</button> </td>`
                    + `<td><button onclick="deleteCategory(${r.CategoryID})">Delete Category</button></td>`

                    + `</tr>`;
            }
            categoriesHTML += `</div>`;
            document.getElementById('categories').innerHTML = categoriesHTML;
        }
    });
}

function fetchCategory(id){
    document.getElementById('category'+id);
    console.log("invoked fetchCategory() with button 'category"+id+"'");
    //let category = '{"id":id}';
    let category = new FormData();
    category.append("CategoryID",id);
    let url = "/category/get";

    fetch(url, {
        method: 'post',
        body: category,
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            let categoryDetailsHTML = "<h2>Ingredients</h2>";
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