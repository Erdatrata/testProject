// const cafeList = document.querySelector('#cafe-list');
// const form = document.querySelector('#add-cafe-form');
//
// // create element & render cafe
// function renderCafe(doc){
//     let li = document.createElement('li');
//     let name = document.createElement('span');
//     let city = document.createElement('span');
//
//     li.setAttribute('data-id', doc.id);
//     name.textContent = doc.data().name;
//     city.textContent = doc.data().city;
//
//     li.appendChild(name);
//     li.appendChild(city);
//
//     cafeList.appendChild(li);
// }
//
// // getting data
// db.collection('cafes').get().then(snapshot => {
//     snapshot.docs.forEach(doc => {
//         renderCafe(doc);
//     });
// });
//
// // saving data
// form.addEventListener('submit', (e) => {
//     e.preventDefault();
//     db.collection('cafes').add({
//         name: form.name.value,
//         city: form.city.value
//     });
//     form.name.value = '';
//     form.city.value = '';
// });
function getTextFrom(from, userDoc) {
    console.log(userDoc.id);
    var docRef = db.collection(from).doc(userDoc.id);
    let re;

    await docRef.get().then((doc) => {
        if (doc.exists) {
            re=doc.data();
            return;
        } else {
            // doc.data() will be undefined in this case
            console.log("No such document!");
        }
    }).catch((error) => {
        console.log("Error getting document:", error);
    });
    let str="";
    for (const e in re) {
        str=str+e+":"+re[e]+'<br>';
    }

    return str;


}