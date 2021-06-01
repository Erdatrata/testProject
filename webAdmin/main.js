// window.open("./manu.html")
let User=null;
document.addEventListener("DOMContentLoaded", () => {

    if(firebase.auth().currentUser){
        window.open("./manu.html");
    }

});




firebase.auth().onAuthStateChanged(function(user) {

    if (user) {
        window.open("./manu.html")

    }
});

function login(){

    var userEmail = document.getElementById("email_field").value;
    var userPass = document.getElementById("password_field").value;

    firebase.auth().signInWithEmailAndPassword(userEmail, userPass).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;

        window.alert("Error : " + errorMessage);

        // ...
    });
    if(firebase.auth().currentUser){
        User=firebase.auth().currentUser;
        window.open("./manu.html");
    }

}

function logout(){
    firebase.auth().signOut();
}
