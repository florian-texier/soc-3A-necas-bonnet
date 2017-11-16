/*
Made by : Jérôme BONNET :D
For : My Little Script
 */

adresseduserveur = 'http://172.30.0.147:5000';      //Adresse ip du serveur

/* La fonction se lance au chargement de la page. */
$(document).ready(function() {
    $("#liste_equipe").empty();     //Je vide la liste des équipes avant tout.
    actualisation_donnees();        // J'appelle la fonction pour actualiser les données
});



/* Actualisation de la liste des équipes, du nombre d'équipe. */
function actualisation_donnees(){
    $.ajax({
        url: adresseduserveur + '/inscrit', 		// La ressource ciblée
        type: 'GET', 				                // Le type de la requête HTTP
        dataType: 'json'                            // Le type de données à recevoir, ici, du json.
    }).done(function (result) {
        document.getElementById('nbequipe').innerHTML = result.length //Nombre équipe = la taille du tabeau retourné
        /* Pour chaque équipe dans mon JSON, j'appelle la fonction d'ajout */
        result.forEach(function (e) {
            ajoute_une_equipe_dans_la_liste(e)
        });
    })
}

/* Lors que j' clique sur une équipe */
function change(n){

    /* Chargement des images de l'équipe */
    $.ajax({
        url: adresseduserveur + '/images/'+n, 		// La ressource ciblée
        type: 'GET', 				                // Le type de la requête HTTP
        dataType: 'json'                            // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {

        $("#photo_equipe").empty();
        result['liste_images'].forEach(function (e) {
            ajouter_une_photo(e['i_base64'], e['i_coordx'], e['i_coordy'])
        });
    })


    /* Chargement des objets de l'équipe */
    $.ajax({
        url: adresseduserveur + '/objets/'+n, 		// La ressource ciblée
        type: 'GET', 				// Le type de la requête HTTP
        dataType: 'json' // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        $("#items_found").empty();
        result['liste_objets'].forEach(function (element) {
            ajouter_un_objet  (element['o_id'],element['o_name'], element['o_found'], element['o_points'],element['e_id'])
        });
    })
}



//Ajouter une photo
function ajouter_une_photo(lapetiteimage, lat, long) {
    var elementliste = document.createElement('li');
    $("<img>", {"src": "data:image/png;base64," + lapetiteimage, "class": "is-128x128", "title": "lat : " + lat + " long : " + long}).appendTo("#photo_equipe");
}

//Ajouter un point de plus à un objet
function ajouter_point_objet(n,id_equipe){
    var bidule = $.ajax({
        url: adresseduserveur + '/objup/'+n, 		// La ressource ciblée
        type: 'GET', 				// Le type de la requête HTTP
        dataType: 'json' // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        location.reload();
    })
}

//Enlever un point de plus à un objet
function enlever_point_objet(n,id_equipe){
    //console.log('Point enlevé');
    var bidule = $.ajax({
        url: adresseduserveur + '/objdown/'+n, 		// La ressource ciblée
        type: 'GET', 				                // Le type de la requête HTTP
        dataType: 'json'                            // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        location.reload();
    })
}

$('.button').on('click',function () {
    console.log("bouton pressé")
    if($(this).data("ops")==="plus"){
        console.log("bouton ajout")
        ajouter_point_objet($(this).data("oid"),$(this).data("eid"))
    }else{
        console.log("bouton enlever")
        enlever_point_objet($(this).data("oid"),$(this).data("eid"))
    }
})

//Ajouter un objet
function ajouter_un_objet(idobjet,name,found,points,idequipe) {
    var elementliste = document.createElement('li');
    if (found === 'false'){
        found = 'not found'
    }
    else{
        found = 'FOUND'
    }
    elementliste.innerHTML = '<li id ="l' + name + '" class="nav-item"> ' + name + ' ' + found + ' ' + points +' pts'+'<span class="sr-only">(current) </span></li> '
    document.getElementById('items_found').appendChild(elementliste);
    var elementliste = document.createElement('li');
    elementliste.innerHTML ="<button class='button' data-oid=\'" + idobjet + "\' data-eid=\'" + idequipe + "\' data-ops='moins' onclick='enlever_point_objet($(this).data(\"oid\"),$(this).data(\"eid\"))' >- </button> <button class='button' data-oid=\'" + idobjet + "\' data-eid=\'" + idequipe + "\'data-ops='plus' onclick='ajouter_point_objet($(this).data(\"oid\"),$(this).data(\"eid\"))' >+</button>";
    document.getElementById('items_found').appendChild(elementliste);
  }

// Fonction pour ajouter une équipe dans la liste <UI>
function ajoute_une_equipe_dans_la_liste(e) {
    var bidule = $.ajax({
        url: adresseduserveur + '/objets/'+e['e_id'], 		// La ressource ciblée
        type: 'GET', 				// Le type de la requête HTTP
        dataType: 'json' // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        sommespoints = 0
        //console.log(result['liste_objets']);
        result['liste_objets'].forEach(function (toto) {
            sommespoints = sommespoints+ toto['o_points']
        });
        console.log($('#boxnumber2').text())
        if(parseInt($('#boxnumber2').text(), 10) < sommespoints){
            $('#boxnumber1').text(e['e_name']);
            $('#boxnumber2').text(sommespoints);
        }
        else if(parseInt($('#boxnumber2').text(), 10)=== sommespoints){
            $('#boxnumber1').append(" , "+e['e_name']);
        }
        var elementliste = document.createElement('li');
        elementliste.innerHTML = ' <li id ="l' + e['e_name'] + '" class="nav-item"> <a class="nav-link active" onclick= "change(' + e['e_id'] + ')" href="#">' + e['e_id'] + ' : ' + e['e_name'] +' : '+ sommespoints +'<span class="sr-only">(current)</span></a> </li>';
        document.getElementById('liste_equipe').appendChild(elementliste);
    })
}