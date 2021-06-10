// var db;

// If you are using v7 or any earlier version of the JS SDK, you should import firebase using namespace import
// import * as firebase from "firebase/app"

// If you enabled Analytics in your project, add the Firebase SDK for Analytics


// Add the Firebase products that you want to use

let SCENARIO='Scenarios';
let FILLED="filled";
let ACCEPTED='accepted';
let NEWUSER="newuser";

let acceptInScenerios="Will move the scenerio down,and will open the option to delete it (only after the scenerio is done and u want to remove it)";
let signatureInScenerio="Will open the list of members who registerd to this scenerio";
let filledInScenerio="Will show the list of members who filled the scenerio plus what they did";
let deleteInScenerio="It will be removed from the server";
let moveReport="Will move the report and give the ability to remove it";
let userInfo="Will show the information about the user";
let deleteUser="Will move the user at the bottom with and the option to delete it will open";
let removeUserFromServer="Will delete the user from the server";
let approveUser="Will authorize the newely registered user as a volunteer";
let reportInfo="Will show the information about the report";
let showInformationAboutEvent="Will show the information about the scenerio";

function refreshListOfS() {
    $("#ListOfS").remove();
    $("#data").html(ListEvent());
    ListEventFun();

}
async function getTextFrom(from, userDoc) {
  // console.log(userDoc.id);
    var docRef = db.collection(from).doc(userDoc.id);
    let re;

    await docRef.get().then((doc) => {
        if (doc.exists) {
            re=doc.data();
            return;
        } else {
            // doc.data() will be undefined in this case
          // console.log("No such document!");
        }
    }).catch((error) => {
      // console.log("Error getting document:", error);
    });
    // let str="";
    // for (const e in re) {
    //     str=str+e+":"+re[e]+'<br>';
    // }

    // return str;
    return re;


}


function createModal(toFill) {
    // toFill=toFill.replace('\n','<br/>');
    // toFill=toFill.replace('\\n','<br/>');
    $("#data").append("" +
        "<!-- The Modal -->\n" +
        "<div id=\"myModal\" class=\"modal\">\n" +
        "\n" +
        "  <!-- Modal content -->\n" +
        "  <div class=\"modal-content\">\n" +
        "    <span class=\"close\">&times;</span>\n" + //this is the x button
        "<div class=\"modal-guts\">"+toFill.innerHTML+"</div>\n" + //this is the content itself
        "  </div>\n" +
        "\n" +
        "</div>");

    // Get the modal
    var modal = document.getElementById("myModal");

// Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

// When the user clicks on the button, open the modal

    modal.style.display = "block";

    let deleteData = function (){
        let modalBase = $(".modal")[0];
        let modalParent = modalBase.parentNode;
        modalParent.removeChild(modalBase);
    }

// When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        modal.style.display = "none";
        deleteData();
    }

// When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
            deleteData();
        }
    }
}

async function openModalFromFirestore(from,userDoc,type) {
    let toFill=await getTextFrom(from,userDoc);
    let toPut=""
    if(!type)
        toPut =Prettify(toFill);
    else
        toPut =Prettify2(toFill,userDoc)
    
    createModal(toPut);
}

//for prettifying reports
function Prettify(toFill){
  // console.log(toFill);
    
    let toReturn = document.createElement("div")
    //title
    let title = document.createElement("h1")
    title.innerText=toFill.title
    toReturn.appendChild(title)
    //table
    let table = document.createElement("table")
    table.style="border=1px solid black"
    toReturn.appendChild(table)
    
    {//description
        let tr = document.createElement("tr")
        let descHead = document.createElement("td")
        descHead.innerText="Description"
        tr.appendChild(descHead);
        let desc =  document.createElement("td")
        desc.innerText=toFill['description']
        tr.appendChild(desc)

        table.appendChild(tr)
    }
    {//Wants credit
        let tr = document.createElement("tr")
        let descHead = document.createElement("td")
        descHead.innerText="Wants credit?"
        tr.appendChild(descHead);
        let desc =  document.createElement("td")
        desc.innerText=(toFill['credit']) ? `Yes` : `No`
        tr.appendChild(desc)
        table.appendChild(tr)
    }
    {//url data
        let tr = document.createElement("tr")
        let descHead = document.createElement("td")
        descHead.innerText="Media URLs"
        tr.appendChild(descHead);
        let desc =  document.createElement("td")
        let anchors=document.createElement("div")
        
        {//looper through file media
            let i=0;
            while(true){
                if(!(toFill['media url'+i] || toFill['media url '+i]))
                    break
                let anchor = ""
                if(toFill['media url'+i])
                    anchor = toFill['media url'+i]
                else
                    anchor = toFill['media url '+i]
                desc.innerHTML+=`<a href="${anchor}" target="_blank">Link ${i+1}</a>`
                anchors.innerHTML+=`<a href="${anchor}" target="_blank" download/>`

                desc.innerHTML+="<br/>"
                if(!toFill['media url'+i] || toFill['media url '+i])
                    break
                i++
            }
            let dlBTN = document.createElement("button")
            dlBTN.innerText="Download All"
            dlBTN.addEventListener('click',function(){
                let cNodes = desc.childNodes.filter(v=>v.nodeName=='A')
                for(node in cNodes){
                    node.click()
                }
            })
            
            desc.appendChild(dlBTN)

        }
        tr.appendChild(desc)
        table.appendChild(tr)

    }
    
    
    
    
    
    return toReturn    
}

//for prettifying event details
function Prettify2(toFill,userDoc){
    let toReturn = document.createElement("div")
    console.log(toFill)

    let title = document.createElement("h1")
    title.innerText=userDoc.id
    toReturn.appendChild(title)
    //table
    let table = document.createElement("table")
    toReturn.appendChild(table)
    //changes to toFill
    toFill["דחיפות"]=(toFill["דחיפות"] == true) ? "Yes" : "No"
    toFill["timeCreated"]=toFill["timeCreated"].toDate()
    //info arrays
    let forFilter= ["דחיפות","מיקום","סוג האירוע","עיר","timeCreated"]//for data
    let forScreen= ["Urgent?","Location","Type of Event","City","Creation Time"]//for descriptors
    for(let i=0;i<forFilter.length;i++){
        let tr=document.createElement("tr")
        let descHead=document.createElement("td")
        let desc=document.createElement("td")

        descHead.innerText=forScreen[i]
        if(i!=1)
            desc.innerText=toFill[forFilter[i]]
        else
            desc.innerHTML=`<a href="https://www.latlong.net/c/?lat=${toFill[forFilter[1]]._lat}&long=${toFill[forFilter[1]]._long}" target="_blank">
            (${toFill[forFilter[1]]._lat},${toFill[forFilter[1]]._long})
            </a>`
            // console.log(toFill[forFilter[i]]._lat)

        tr.appendChild(descHead)
        tr.appendChild(desc)
        table.appendChild(tr)
    }


    return toReturn;
}
async function getUser(id) {
    const snapshot = await firebase.database().ref('Users/' + id).once('value');
    let user = (snapshot.val());
    return user["First Name:"]+" "+user["Sec Name:"];
}

async function openUserInfo(id) {
    const snapshot = await firebase.database().ref('Users/' + id).once('value');
    let user = (snapshot.val());
    // console.log(user)
    // let toFill=user["First Name:"]+" "+user["Sec Name:"]+"\n"
    // toFill=toFill+user["Email:"]+"\n";
    // toFill=toFill+user["City:"]+"\n";
    // toFill=toFill+user["Phone:"]+"\n";
    let toPut = document.createElement("table")
    let forFilter= ["First Name:","Sec Name:","Email:","City:","Phone:"]
    let forScreen= ["First Name","Last Name","Email","City","Phone"]
    for(let i=0;i<forFilter.length;i++){
        let tr=document.createElement("tr")
        let descHead=document.createElement("td")
        let desc=document.createElement("td")

        descHead.innerText=forScreen[i]
        desc.innerText=user[forFilter[i]]

        tr.appendChild(descHead)
        tr.appendChild(desc)
        toPut.appendChild(tr)
    }
  // console.log(toPut)
    
    let toReturn = document.createElement("div")
    toReturn.innerHTML="<h1>&emsp;</h1>"
    toReturn.appendChild(toPut)

    createModal(toReturn);

}

async function createTab(from,userDoc,filled) {
    let id = userDoc.id.replace(/\s+/g, '');
    let x = await getUser(userDoc.id);
    var task = $("<div class='task' id=" + id + "></div>").text(x);

    var checkUser = $("<i class='fas fa-user tooltip'><span class='tooltiptext'>"+userInfo+"</span></i>").click(function () {
            openUserInfo(userDoc.id);
    });

    var check = $("<i class='fas fa-times tooltip'><span class='tooltiptext'>"+moveReport+"</span></i>").click(function(){
        var del = $("<i class='fas fa-trash-alt tooltip'><span class='tooltiptext'>"+deleteInScenerio+"</span></i>").click(function(){
            var p = $(this).parent();
            p.fadeOut(function(){
                makeOld(from+"/"+userDoc.id,userDoc.id);
                //get into Scenerio
                p.remove();
            });
        });
        task.append(del);
        $(".notcomp").append(task);
        var p = $(this).parent();
        p.fadeOut(function(){

            //set as complete
            $(".comp").append(p);
            p.fadeIn();
        });
        $(this).remove();
    });
    if (filled == FILLED) {
        var open = $("<i class='fas fa-folder-open tooltip'><span class='tooltiptext'>"+reportInfo+"</span></i>").click(function () {
            openModalFromFirestore(from, userDoc);
        });
        task.append( open,checkUser,check);
    } else {
        task.append(checkUser,check);
    }
    $(".notcomp").append(task);
    //to clear the input


    //userDoc contains all metadata of Firestore object, such as reference and id
  // console.log(userDoc.id)

    //If you want to get doc data
    var userDocData = userDoc.data()
    console.dir(userDocData)

}

function openList(from,filled) {
    let ref=db.collection(from);

    ref.get().then((querySnapshot) => {

        //querySnapshot is "iteratable" itself
        querySnapshot.forEach((userDoc) => {
            createTab(from,userDoc,filled);


        })});

    createEnterFilter();
}

function refreshFilled(userDocid) {
    $("#ListOfS").remove();
    openFilled(userDocid);
}

function openFilled(userDocid) {
    $("#ListOfS").remove();
    $("#data").html(ListEvent());

    openList(SCENARIO+"/"+userDocid+"/"+FILLED,FILLED);

    document.getElementById("btn btn-cancel").addEventListener("click",refreshListOfS);
    document.getElementById("refForListS").addEventListener("click", function(){
        refreshFilled(userDocid);
    }, false);
}

function refreshAccepted(userDocid) {
    $("#ListOfS").remove();
    openAccepted(userDocid);
}

function openAccepted(userDocid) {
    $("#ListOfS").remove();
    $("#data").html(ListEvent());

    openList(SCENARIO+"/"+userDocid+"/"+ACCEPTED);
    document.getElementById("btn btn-cancel").addEventListener("click",refreshListOfS);
    document.getElementById("refForListS").addEventListener("click", function(){
        refreshAccepted(userDocid);
    }, false);
}

function makeOld(path, name) {
    let ref=db.doc(path);
    ref.get().then((doc) => {
        if (doc.exists) {
            let save=doc.data();
            let remove =ref.delete().then(() => {
              // console.log(name+",Document successfully deleted!\nin path:"+path);
            });
            db.collection("OldInfo").doc(name+"_"+Date.now()).set(save).then(() => {
              // console.log("Document successfully written!");
            });
            return remove;
        } else {
            // doc.data() will be undefined in this case
          // console.log(name+",No such document! \nin path:"+path);
        }
    }).catch((error) => {
      // console.log("Error getting document:", error);
    });
}

function createEnterFilter() {
    $(".txtb").on("keyup",function(e){

        //look for

        if(e.keyCode == 13 && $(".txtb").val() != "") {

            let lookFor = $(".txtb").val();
            let list = document.getElementById("notcomp",).getElementsByTagName("*");

            for (var i = 0; i < list.length; i++) {
              // console.log("---------"+list[i].textContent+"---------");
                if (!list[i].textContent.startsWith(lookFor)&&list[i].id!="") {
                    document.getElementById(list[i].id).remove();
                    i=i-1;
                }
            }


        }


        {
        }
    });

}

function getInformation(id) {
    openModalFromFirestore(SCENARIO,id,1);

}

function ListEventFun(){
    let ref=db.collection(SCENARIO);

    ref.get().then((querySnapshot) => {

        //querySnapshot is "iteratable" itself
        querySnapshot.forEach((userDoc) => {
            let id=userDoc.id.replace(/\s+/g,'');
            var task= $("<div class='task' id="+id+"></div>").text(userDoc.id);


            var filled = $("<i class='fas fa-signature tooltip'><span class='tooltiptext'>"+filledInScenerio+"</span></i>").click(function(){
                openAccepted(userDoc.id);
            });
            var accepted = $("<i class='fas fa-file-signature tooltip'><span class='tooltiptext'>"+signatureInScenerio+"</span></i>").click(function(){
                openFilled(userDoc.id);
            });
            var information = $("<i class='fas fa-question tooltip'><span class='tooltiptext'>"+showInformationAboutEvent+"</span></i>").click(function(){
                getInformation(userDoc);
            });

            var check = $("<i class='fas fa-times tooltip'><span class='tooltiptext'>"+acceptInScenerios+"</span></i>").click(function(){
                var del = $("<i class='fas fa-trash-alt tooltip'><span class='tooltiptext'>"+deleteInScenerio+"</span></i>").click(function(){
                    var p = $(this).parent();
                    p.fadeOut(function(){
                        makeOld(SCENARIO+"/"+userDoc.id,userDoc.id);
                        //get into Scenerio
                        p.remove();
                    });
                });
                task.append(del);
                $(".notcomp").append(task);
                var p = $(this).parent();
                p.fadeOut(function(){

                    //set as complete
                    $(".comp").append(p);
                    p.fadeIn();
                });
                $(this).remove();
            });

            task.append(filled,accepted,information,check);
            $(".notcomp").append(task);
            //to clear the input




            //userDoc contains all metadata of Firestore object, such as reference and id
          // console.log(userDoc.id)

            //If you want to get doc data
            var userDocData = userDoc.data()
            console.dir(userDocData)

        })});

    createEnterFilter();
    document.getElementById("btn btn-cancel").addEventListener("click",removeListOfS);
    document.getElementById("refForListS").addEventListener("click",refreshListOfS);


}
function removeListOfS(){
    document.getElementById("ListOfS").remove();


}
function removeNewEvent(){
    document.getElementById("newEventLoaded").remove();


}
function ListOfV() {
    return undefined;
}

function InList(city,arr) {
    for(let i=0;i<arr.length;i++){
        if(city==arr[i]){return true;}
    }
    return false;
}

function sendToDataBaseNewEvent() {
    let ScenerioName=document.querySelector('#ScenerioName').value;
    let locationGps=document.querySelector('#geoCode').value;
    let des=document.querySelector('#des').value;
    let city =document.querySelector('#city').value;
    let importent=document.querySelector('#importentEvent').value;
    if(importent==undefined||des==undefined||locationGps==undefined||ScenerioName==undefined||
        importent==""||des==""||locationGps==""||ScenerioName==""
    ){ window.alert("Error : " + "some field are empty");return;}
    if(city==""||city==undefined){city="לא בעיר";}
    else if(!InList(city,countries)){ window.alert("Error : " + "city mush be writen correct");return;}

    locationGps=locationGps.replace('(','');
    locationGps=locationGps.replace(')','');
    locationGps=locationGps.replace(' ','');
    var res = locationGps.split(",");
    let latitude=res[0];
    let longitude=res[1];
  // console.log(latitude);
  // console.log(longitude);
    let docRef = db.doc("Scenarios/"+ScenerioName);
    docRef.set({

        "מיקום": new firebase.firestore.GeoPoint(Number(latitude), Number(longitude)),
        "סוג האירוע": des,
        "עיר": city,
        "דחיפות": Boolean(importent),
        "timeCreated":new Date()

    }).then(function (){
      // console.log("status saved");
        window.alert("was sended");
    }).catch(function (error){
        window.alert("Error : " + error);
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



        "<form autocomplete=\"off\" action=\"/action_page.php\">\n" +
        "          <input id=\"city\" type=\"text\" required/>\n" +
        "          <label for=\"input1\">עיר</label>\n" +
        "          <span class=\"underline\"></span>\n" +"" +
        "</form>\n"+
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
    let re="  <div id=\"ListOfS\">\n" +
        "    <div class=\"container\">\n" +
        "      <input type=\"text\" class=\"txtb\" placeholder=\"Search Bar\">\n" +
        "      <div class=\"notcomp\" id='notcomp'>\n" +
        "" +
        "\n" +
        "\n" +
        "\n" +
        "      </div>\n" +
        "\n" +
        "      <div class=\"comp\">\n" +
        "        <h3 id=\"Completed\">Completed</h3>\n" +
        "      </div>\n" +
        "<button class=\"refForListS\" id=\"refForListS\">רענן</button>\n" +
        "<button class=\"cancelForListS\" id=\"btn btn-cancel\">חזור</button>\n"+"<style>\n" +

        ".cancelForListS {\n" +
        "  padding: 15px 32px;\n" +
        "  text-align: center;\n" +
        "  text-decoration: none;\n" +
        "  display: inline-block;\n" +
        "  font-size: 16px;\n" +
        "  margin: 4px 2px;\n" +
        "  cursor: pointer;\n" +"    " +
        "    border: 1px solid #373739;\n" +
        "    background: #393a3c;\n" +
        "    color: #fff;"+"}"+

        ".refForListS {\n" +
        "  padding: 15px 32px;\n" +
        "  text-align: center;\n" +
        "  text-decoration: none;\n" +
        "  display: inline-block;\n" +
        "  font-size: 16px;\n" +
        "  margin: 4px 2px;\n" +
        "  cursor: pointer;\n" +"    " +
        "    border: 1px solid #2962ff;\n" +
        "    background: #2962ff;\n" +
        "    color: #fff;" +"}"+


        "</style>"+
        "    </div>\n" +
        "\n" +
        "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.css\">\n" +
        "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js\"></script>\n" +
        "    <link rel=\"stylesheet\" href=\"forScenerio.css\">\n" +
        "  \n" +"" + +

        "  </div>";
    return re;
}

function logoff() {
    firebase.auth().signOut();
    window.open("./main.html");
    window.open('','_self').close()
}

function addButtonToEvent() {
    document.getElementById("btn btn-confirm").addEventListener("click", sendToDataBaseNewEvent);
    document.getElementById("btn btn-cancel").addEventListener("click",removeNewEvent);

}



function removeUser(userid) {
     firebase.database().ref('Users/' + userid).remove();
}

function updatePermission(id) {//last thing to do


}

function RegisterUser(id) {
    firebase.database().ref('Users/' + id).update({
        volunteer: "true"
    });

    updatePermission(id);

}

async function createTabFromUserData(userid,newuser) {
    let id = userid.replace(/\s+/g, '');
    let x = await getUser(userid);
    var task = $("<div class='task' id=" + id + "></div>").text(x);

    var checkUser = $("<i class='fas fa-user tooltip'><span class='tooltiptext'>"+userInfo+"</span></i>").click(function () {
        openUserInfo(id);
    });

    var delFirst = $("<i class='fas fa-user-slash tooltip'><span class='tooltiptext'>"+deleteUser+"</span></i>").click(function () {
        var delSec = $("<i class='fas fa-trash-alt tooltip'><span class='tooltiptext'>"+removeUserFromServer+"</span></i>").click(function () {
            var p = $(this).parent();
            p.fadeOut(function () {
                removeUser(userid);
                //get into Scenerio
                p.remove();
            });
        });
        task.append(delSec);
        $(".notcomp").append(task);
        var p = $(this).parent();
        p.fadeOut(function () {

            //set as complete
            $(".comp").append(p);
            p.fadeIn();
        });
        $(this).remove();
    });
        if(newuser==NEWUSER){
            var agree = $("<i class='fas fa-check tooltip'><span class='tooltiptext'>"+approveUser+"</span></i>").click(function () {
                var p = $(this).parent();
                p.fadeOut(function () {
                    RegisterUser(id);
                    //get into Scenerio
                    p.remove();
                });
            });
            task.append(delFirst, checkUser,agree);
        }
        else {
            task.append(delFirst, checkUser);
        }
    $(".notcomp").append(task);
    //to clear the input
}

async function isItNewUser(userId) {
    const snapshot = await firebase.database().ref('Users/' + userId).once('value');
    let user = (snapshot.val());
    if(user["volunteer"]==="false"){
        return true;
    }
    else if(user["volunteer"]==="true"){
      // console.log("truetruetruetruetruetruetruetruetruetrue");

        return false;}

    return true;
}

async function ListVolFun() {
    const snapshot = await firebase.database().ref('Users').once('value', (snapshot) => {
        snapshot.forEach((childSnapshot) => {
            firebase.database().ref('Users/' + childSnapshot.key).once('value',(snapshot)=>{
                let user= (snapshot.val());
                if(user["volunteer"]=="true"){
                    // console.log("1111111111111111111111111111");
                    createTabFromUserData(childSnapshot.key,"");
                  // console.log(childSnapshot.key);
                }
                // else{console.log("22222222222222222222");}
            });

        });

});
    snapshot.forEach((childSnapshot) => {
        //let isItNew=await isItNewUser(childSnapshot.key);
        // if(!isItNew){
        //   // console.log("1111111111111111111111111111");
        //     createTabFromUserData(childSnapshot.key,"");
        //   // console.log(childSnapshot.key);}

    });
    document.getElementById("btn btn-cancel").addEventListener("click",removeListOfS);
    document.getElementById("refForListS").addEventListener("click",function () {
        $("#ListOfS").remove();
        $("#data").html(ListEvent());
        ListVolFun();
    });
    createEnterFilter();

}

async function ListUsersFun() {
    const snapshot = await firebase.database().ref('Users').once('value', (snapshot) => {
        snapshot.forEach((childSnapshot) => {
            firebase.database().ref('Users/' + childSnapshot.key).once('value',(snapshot)=>{
                let user= (snapshot.val());
                if(user["volunteer"]!="true"){
                    // console.log("1111111111111111111111111111");
                    createTabFromUserData(childSnapshot.key, NEWUSER);
                  // console.log(childSnapshot.key);
                }
                else{console.log("22222222222222222222");}
            });


        });});

    document.getElementById("btn btn-cancel").addEventListener("click",removeListOfS);
    document.getElementById("refForListS").addEventListener("click",function () {
        $("#ListOfS").remove();
        $("#data").html(ListEvent());
        ListUsersFun();
    });
    createEnterFilter();
}

function jsonFileToArray(json) {
    $.getJSON(json, function(data) {
        for(let i=0;i<data.length;i++){
            countries[i]=data[i].cityName;
        }
    });

}

document.addEventListener("DOMContentLoaded", () => {
    jsonFileToArray("./citys.json");


    $("#ListOfV").click(function(){
        $("#data").html(ListEvent());
        ListVolFun();



    });
    $("#newEvent").click(function(){
        $("#data").html(newEvent());
        addButtonToEvent();
        autocomplete(document.getElementById("city"), countries);

    });
    $("#newUsers").click(function(){
        $("#data").html(ListEvent());
        ListUsersFun();

    });
    $("#ListEvent").click(function(){
        $("#data").html(ListEvent());
        ListEventFun();

    });

    document.getElementById("logoff").addEventListener("click", logoff);


});
function autocomplete(inp, arr) {
    /*the autocomplete function takes two arguments,
    the text field element and an array of possible autocompleted values:*/
    var currentFocus;
    /*execute a function when someone writes in the text field:*/
    inp.addEventListener("input", function(e) {
        var a, b, i, val = this.value;
        /*close any already open lists of autocompleted values*/
        closeAllLists();
        if (!val) { return false;}
        currentFocus = -1;
        /*create a DIV element that will contain the items (values):*/
        a = document.createElement("DIV");
        a.setAttribute("id", this.id + "autocomplete-list");
        a.setAttribute("class", "autocomplete-items");
        /*append the DIV element as a child of the autocomplete container:*/
        this.parentNode.appendChild(a);
        /*for each item in the array...*/
        for (i = 0; i < arr.length; i++) {
            /*check if the item starts with the same letters as the text field value:*/
            if (arr[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
                /*create a DIV element for each matching element:*/
                b = document.createElement("DIV");
                /*make the matching letters bold:*/
                b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
                b.innerHTML += arr[i].substr(val.length);
                /*insert a input field that will hold the current array item's value:*/
                b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
                /*execute a function when someone clicks on the item value (DIV element):*/
                b.addEventListener("click", function(e) {
                    /*insert the value for the autocomplete text field:*/
                    inp.value = this.getElementsByTagName("input")[0].value;
                    /*close the list of autocompleted values,
                    (or any other open lists of autocompleted values:*/
                    closeAllLists();
                });
                a.appendChild(b);
            }
        }
    });
    /*execute a function presses a key on the keyboard:*/
    inp.addEventListener("keydown", function(e) {
        var x = document.getElementById(this.id + "autocomplete-list");
        if (x) x = x.getElementsByTagName("div");
        if (e.keyCode == 40) {
            /*If the arrow DOWN key is pressed,
            increase the currentFocus variable:*/
            currentFocus++;
            /*and and make the current item more visible:*/
            addActive(x);
        } else if (e.keyCode == 38) { //up
            /*If the arrow UP key is pressed,
            decrease the currentFocus variable:*/
            currentFocus--;
            /*and and make the current item more visible:*/
            addActive(x);
        } else if (e.keyCode == 13) {
            /*If the ENTER key is pressed, prevent the form from being submitted,*/
            e.preventDefault();
            if (currentFocus > -1) {
                /*and simulate a click on the "active" item:*/
                if (x) x[currentFocus].click();
            }
        }
    });
    function addActive(x) {
        /*a function to classify an item as "active":*/
        if (!x) return false;
        /*start by removing the "active" class on all items:*/
        removeActive(x);
        if (currentFocus >= x.length) currentFocus = 0;
        if (currentFocus < 0) currentFocus = (x.length - 1);
        /*add class "autocomplete-active":*/
        x[currentFocus].classList.add("autocomplete-active");
    }
    function removeActive(x) {
        /*a function to remove the "active" class from all autocomplete items:*/
        for (var i = 0; i < x.length; i++) {
            x[i].classList.remove("autocomplete-active");
        }
    }
    function closeAllLists(elmnt) {
        /*close all autocomplete lists in the document,
        except the one passed as an argument:*/
        var x = document.getElementsByClassName("autocomplete-items");
        for (var i = 0; i < x.length; i++) {
            if (elmnt != x[i] && elmnt != inp) {
                x[i].parentNode.removeChild(x[i]);
            }
        }
    }
    /*execute a function when someone clicks in the document:*/
    document.addEventListener("click", function (e) {
        closeAllLists(e.target);
    });
}

/*An array containing all the country names in the world:*/

var countries=[];
/*initiate the autocomplete function on the "myInput" element, and pass along the countries array as possible autocomplete values:*/



