function UsersLogin() {
    //debugger;
    console.log("Invoked UsersLogin() "); //prints during run time
    let url = "/users/login"; //this is the end of the website url
    let formData = new FormData(document.getElementById('LoginForm'));

    fetch(url, {
        method: "post",
        body: formData,
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            Cookies.set("SessionToken", response.SessionToken);   //session token created is unique and added to db
            Cookies.set("Email", response.Email);
            window.open("recipes.html", "_self");       //open index.html in same tab
        }
    });
}

function logout() {
    debugger;
    console.log("Invoked logout");
    let url = "/users/logout"; //end of the website url
    fetch(url, {method: "POST"
    }).then(response => {
        return response.json();                 //now return that promise to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            Cookies.remove("SessionToken", response.SessionToken);    //UserName and Token are removed
            Cookies.remove("Email", response.Email);
            window.open("login.html", "_self");       //open index.html in same tab
        }
    });
}






