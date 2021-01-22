function AddUser() {
    console.log("Invoked AddUser()");
    const formData = new FormData(document.getElementById('LoginForm'));
    let url = "/users/add";
    fetch(url, {
        method: "POST",
        body: formData,
    }).then(response => {
        return response.json()
    }).then(response => {
        if (response.hasOwnProperty("Error")) {
            alert(JSON.stringify(response));
        } else {
            window.open("/client/login.html", "_self");   //URL replaces the current page.  Create a new html file
        }                                                  //in the client folder called index.html
    });
}

function UsersSignup() {
    //debugger;
    console.log("Invoked SignUp() ");
    let url = "/users/signup";
    window.open("signup.html", "_self");
}

function ReturnLogin() {
    //debugger;
    console.log("Invoked ReturnLogin() ");
    let url = "/users/login";
    window.open("login.html", "_self");
}