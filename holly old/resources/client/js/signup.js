function AddUser() {
    console.log("Invoked AddUser()");
    const formData = new FormData(document.getElementById('SignUpForm'));
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

