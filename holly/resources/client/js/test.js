function pageload(){
    //debugger;
    let Userdiv = document.getElementById("uNameDetail");
    let Email = Cookies.get("Email");
    Userdiv.innerHTML = "You are signed in as " + Email;

}