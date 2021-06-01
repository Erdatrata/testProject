// var db;

// If you are using v7 or any earlier version of the JS SDK, you should import firebase using namespace import
// import * as firebase from "firebase/app"

// If you enabled Analytics in your project, add the Firebase SDK for Analytics


// Add the Firebase products that you want to use



function removeNewEvent(){
    document.getElementById("newEventLoaded").remove();


}
function ListOfV() {
    return undefined;
}

function sendToDataBaseNewEvent() {
    let ScenerioName=document.querySelector('#ScenerioName').value;
    let locationGps=document.querySelector('#geoCode').value;
    let des=document.querySelector('#des').value;
    let city =document.querySelector('#city').value;
    let importent=document.querySelector('#importentEvent').value;
    if(importent==undefined||city==undefined||des==undefined||locationGps==undefined||ScenerioName==undefined||
        importent==""||city==""||des==""||locationGps==""||ScenerioName==""
    ){return;}
    locationGps=locationGps.replace('(','');
    locationGps=locationGps.replace(')','');
    locationGps=locationGps.replace(' ','');
    var res = locationGps.split(",");
    let latitude=res[0];
    let longitude=res[1];
    console.log(latitude);
    console.log(longitude);
    let docRef = db.doc("Scenarios/"+ScenerioName);
    docRef.set({

        "מיקום": new firebase.firestore.GeoPoint(Number(latitude), Number(longitude)),
        "סוג אירוע": des,
        "עיר": city,
        "דחיפות": Boolean(importent)

    }).then(function (){
        console.log("status saved");
    }).catch(function (error){
        console.log("gor an error: ",error);
    })
}

function newEvent() {
    let re="<div id=\"newEventLoaded\"><div id=\"wrap\" class=\"input\">\n" +
        "  <header class=\"input-header\">\n" +
        "    <h1>הכנס פרטי אירוע</h1>\n" +
        "  </header>\n" +
        "  <section class=\"input-content\">\n" +
        "    <h2>הכנס פרטים על האירוע</h2>\n" +
        "    <div class=\"input-content-wrap\">\n" +
        "      <dl class=\"inputbox\">\n" +
        "        <dt class=\"inputbox-title\">כותרת על האירוע</dt>\n" +
        "        <dd class=\"inputbox-content\">\n" +
        "          <input id=\"ScenerioName\" type=\"text\" required/>\n" +
        "          <label for=\"input0\">כותרת על האירוע</label>\n" +
        "          <span class=\"underline\"></span>\n" +
        "        </dd>\n" +
        "      </dl>\n" +
        "      <dl class=\"inputbox\">\n" +
        "        <dt class=\"inputbox-title\">פירוט מורחב</dt>\n" +
        "        <dd class=\"inputbox-content\">\n" +
        "          <input id=\"des\" type=\"text\" required/>\n" +
        "          <label for=\"input1\">פירוט מורחב</label>\n" +
        "          <span class=\"underline\"></span>\n" +
        "        </dd>\n" +
        "      </dl>\n" +
        "      <dl class=\"inputbox\">\n" +
        "        <dt class=\"inputbox-title\">עיר</dt>\n" +
        "        <dd class=\"inputbox-content\">\n" +
        "          <input id=\"city\" type=\"text\" required/>\n" +
        "          <label for=\"input1\">עיר</label>\n" +
        "          <span class=\"underline\"></span>\n" +
        "        </dd>\n" +
        "      </dl>\n" +
        "      <dl class=\"inputbox\">\n" +
        "        <dt class=\"inputbox-title\">נקודת מיקום</dt>\n" +
        "        <dd class=\"inputbox-content\">\n" +
        "          <input id=\"geoCode\" type=\"text\" required/>\n" +
        "          <label for=\"input1\">נקודת מיקום</label><a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://www.latlong.net/\">כדי לפתוח את המפה ולבחור מיקום</a>\n" +
        "          <span class=\"underline\"></span>\n" +
        "        </dd>\n" +
        "      </dl>\n" +
        "<div><input type=\"checkbox\" id=\"importentEvent\" name=\"importentEvent\" value=\"Bike\">\n" +
        "<label for=\"vehicle1\"> לעלות כדחוף</label><br></div>"+
        "      <div class=\"btns\">\n" +
        "          <button class=\"btn btn-confirm\" id=\"btn btn-confirm\">שלח</button>\n" +
        "          <button class=\"btn btn-cancel\" id=\"btn btn-cancel\">בטל</button>\n" +
        "      </div>\n" +
        "  </section>\n" +
        "</div></div>"


    // let form = document.querySelector('#btn btn-confirm');
    // form.addEventListener('click',sendToDataBaseNewEvent());
    return re;
}

function newUsers() {
    return undefined;
}

function ListEvent() {
    return undefined;
}

function logoff() {
    firebase.auth().signOut();
    window.open("./main.html");
}

function addButtonToEvent() {
    document.getElementById("btn btn-confirm").addEventListener("click", sendToDataBaseNewEvent);
    document.getElementById("btn btn-cancel").addEventListener("click",removeNewEvent);

}

document.addEventListener("DOMContentLoaded", () => {
    // require("firebase/firestore");
    // firebase.initializeApp({
    //     apiKey: 'AIzaSyCei2jEltlh-SK_H7Y95-f6IZXOrRKwuCQ',
    //     authDomain: 'tazpit-testingandprototype.firebaseapp.com',
    //     projectId: 'tazpit-testingandprototype'
    // });
    //
    // var db = firebase.firestore();
    // this.db=db;
// Required for side-effects


    $("#ListOfV").click(function(){
        $("#data").html(ListOfV());

    });
    $("#newEvent").click(function(){
        $("#data").html(newEvent());
        addButtonToEvent();

    });
    $("#newUsers").click(function(){
        $("#data").html(newUsers());

    });
    $("#ListEvent").click(function(){
        $("#data").html(ListEvent());

    });

    document.getElementById("logoff").addEventListener("click", logoff);


});

