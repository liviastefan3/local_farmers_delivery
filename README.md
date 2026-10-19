# Local Farmers Delivery

Aplicație Android care conectează cultivatorii locali cu clienții,
permițând postarea recoltei zilnice și comandarea cu livrare urmărită pe hartă.

## Autor
Ștefan Livia, grupa 3.2C, AC CTI-RO

## Descrierea proiectului
Cultivatorii postează zilnic poze cu ce au cules și cantitatea disponibilă (kg).
Clienții văd produsele din apropiere, comandă, iar cultivatorul livrează,
clientul urmărind poziția pe hartă.

## Senzori utilizați
| Cameră  -» Fotografierea recoltei de către cultivator (și, opțional, recunoaștere produs cu ML Kit) 
| GPS     -» Locația fermei, distanța până la client, urmărirea livrării în timp real 

## Funcționalități
- Înregistrare și autentificare (cultivator / client)
- Postare produs: poză, cantitate, preț
- Listă produse cu distanță față de client
- Plasare comandă cu actualizare automată a stocului
- Urmărirea livrării pe hartă

## Tehnologii
- Kotlin, Android Studio
- Firebase (Authentication, Firestore, Storage)
- Google Maps SDK sau OSMdroid

## Dezvoltări viitoare
- Livrator separat, plăți online, rating pentru cultivatori
- Confirmarea livrării prin cod QR 
