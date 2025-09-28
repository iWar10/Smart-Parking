# Smart Parking

## Descriere

Smart Parking este o aplicație Android fictivă, aflată în stadiu de dezvoltare (versiune de bază). Aplicația permite utilizatorilor să vizualizeze parcări pe hartă, să rezerve locuri disponibile și să gestioneze un sold fictiv. Proiectul reprezintă un proof of concept, demonstrând cum pot fi integrate tehnologii moderne pentru a crea un sistem inteligent de gestionare a parcărilor.

## Funcționalități implementate

* Afișarea parcărilor și locurilor disponibile folosind Google Maps SDK.
* Rezervarea unui loc de parcare cu debitarea automată a soldului fictiv.
* Navigare către locul rezervat prin Google Maps.
* Creare și autentificare conturi utilizatori prin Firebase Authentication.
* Administrarea soldului și adăugarea de fonduri fictive.
* Gestionarea datelor utilizatorilor și a rezervărilor prin Firebase Firestore.

## Tehnologii utilizate

* Limbaj: Kotlin (Android).
* Google Maps SDK pentru afișarea hărții și a markerelor.
* Firebase Authentication pentru gestionarea conturilor utilizatorilor.
* Firebase Firestore pentru stocarea datelor și a rezervărilor.

## Stadiul proiectului

Proiectul este într-o fază incipientă, cu funcționalități de bază implementate. Aplicația nu are scop comercial și nu folosește plăți reale, ci doar simulează un flux complet de rezervare și gestionare a fondurilor. Etapele viitoare ar putea include notificări push, istoricul rezervărilor și integrarea unui sistem de plăți reale.
Configurare Firebase și API Key

## Pentru a rula aplicația, este necesar să configurezi Firebase și să adaugi cheia API.

1. Obține cheia API din consola Firebase.

* În fișierul AndroidManifest.xml, caută locul unde apare placeholder-ul "cheieapi" și înlocuiește-l cu cheia ta reală.
    android:name="com.google.android.geo.API_KEY"
    android:value="cheieapi"

2. Obține fișierul google-services.json din consola Firebase.

* Pune fișierul descărcat în directorul app/.

* În repo există un fișier de exemplu: app/google-services.json.example pentru structură.

* Trebuie să îl copiezi și să completezi câmpurile cu datele proprii Firebase.
