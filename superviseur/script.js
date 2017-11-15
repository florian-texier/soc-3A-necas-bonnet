adresseduserveur = 'http://172.30.0.147:5000';

//Se lance au chargement du document
$(document).ready(function() {
    $("#liste_equipe").empty();
    actualisation_donnees();
});

// Fonction qui va actualiser les données de la page
function actualisation_donnees(){
    $.ajax({
        url: adresseduserveur + '/inscrit', 		// La ressource ciblée
        type: 'GET', 				                // Le type de la requête HTTP
        dataType: 'json'                            // Le type de données à recevoir, ici, du json.
    }).done(function (result) {
        //console.log(result);
        document.getElementById('nbequipe').innerHTML = result.length
        result.forEach(function (e) {
            ajoute_une_equipe_dans_la_liste(e)
        });
    })
}

// En cas de clic, sur la liste d'équipe
function change(n){
    //console.log('PhotoS équipe Numero -> ' + n);
    $.ajax({
        url: adresseduserveur + '/images/'+n, 		// La ressource ciblée
        type: 'GET', 				                // Le type de la requête HTTP
        dataType: 'json'                            // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        //console.log(result);
        $("#photo_equipe").empty();
        result['liste_images'].forEach(function (e) {
            ajouter_une_photo(e['i_base64'], e['i_coordx'], e['i_coordy'])
        });
    })
    $.ajax({
        url: adresseduserveur + '/objets/'+n, 		// La ressource ciblée
        type: 'GET', 				// Le type de la requête HTTP
        dataType: 'json' // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        //console.log(result);
        $("#items_found").empty();
        result['liste_objets'].forEach(function (e) {
            ajouter_un_objet  (e['o_id'],e['o_name'], e['o_found'], e['o_points'],e['e_id'])
        });
    })
}

//Ajouter une photo
function ajouter_une_photo(lapetiteimage, lat, long) {
    var elementliste = document.createElement('li');
    $("<img>", {"src": "data:image/png;base64," + lapetiteimage, "class": "is-128x128", "title": "lat : " + lat + " long : " + long}).appendTo("#photo_equipe");
}


function ajouter_point_objet(n,id_equipe){
    var bidule = $.ajax({
        url: adresseduserveur + '/objup/'+n, 		// La ressource ciblée
        type: 'GET', 				// Le type de la requête HTTP
        dataType: 'json' // Le type de données à recevoir, ici, du HTML.
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

//Ajouter un objet
function ajouter_un_objet(idobjet,name,found,points,idequipe) {
    //console.log('Fonction ajouter en cours -> ' + name)


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
    //console.log('Fonction ajouter en cours -> ' + e['e_name']);

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