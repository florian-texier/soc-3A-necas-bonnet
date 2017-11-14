adresseduserveur = 'http://172.30.0.147:5000';

// Fonction qui va actualiser les données de la page
function actualisation_donnees(){
    $.ajax({
        url: adresseduserveur + '/inscrit', 		// La ressource ciblée
        type: 'GET', 				                // Le type de la requête HTTP
        dataType: 'json'                            // Le type de données à recevoir, ici, du json.
    }).done(function (result) {
        console.log(result);
        document.getElementById('nbequipe').innerHTML = result.length
        result.forEach(function (e) {
            ajoute_une_equipe_dans_la_liste(e)
        });
    })
}

// Fonction pour ajouter une équipe dans la liste <UI>
function ajoute_une_equipe_dans_la_liste(e) {
    console.log('Fonction ajouter en cours -> ' + e['e_name']);
    var elementliste = document.createElement('li');
    elementliste.innerHTML = ' <li id ="l' + e['e_name'] + '" class="nav-item"> <a class="nav-link active" onclick= "change(' + e['e_id'] + ')" href="#">Numéro ' + e['e_id'] + '<span class="sr-only">(current)</span></a> </li>';
    document.getElementById('liste_equipe').appendChild(elementliste);
}

// En cas de clic, sur les UI
function change(n){
    console.log('PhotoS équipe Numero -> ' + n);
    var bidule = $.ajax({
        url: adresseduserveur + '/images/'+n, 		// La ressource ciblée
        type: 'GET', 				// Le type de la requête HTTP
        dataType: 'json' // Le type de données à recevoir, ici, du HTML.
    }).done(function (result) {
        console.log(result);
        $("#photo_equipe").empty();
        result['liste_images'].forEach(function (e) {
            ajouter_une_photo(e['i_base64'])
        });
    })
}

//Ajouter une photo
function ajouter_une_photo(lapetiteimage) {
    var elementliste = document.createElement('li');
    $("<img>", {"src": "data:image/png;base64," + lapetiteimage,"width": "250px", "height": "250px"}).appendTo("#photo_equipe");
}

//J'actualise quand je lance la page.
actualisation_donnees();
