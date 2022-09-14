# MMAHCA
Tar en sats-liste fra flexlab og genererer et excelark som fungerer som input til LCMS. (Høyst labspesifikt og ikke nyttig for noen andre)

Kompileres ved å kjøre sh compile.sh. Da kommer jar-fila her: MMAHCA/build/libs/

For eksempel på den nye instrument-pcen ved LCMS fungerer det ikke å åpne jar-filen direkte. Løsningen på det var å lage en bat-fil som åpner det, eller skrive inn i kommandolinjen. java -jar App.groovy. OBS! App.groovy må ha tilgang til mappen testfiles og filene som ligger i den, hvis man åpner vha bat-fil er det som om programmet åpner seg fra lokasjonen til bat-filen. Pass derfor på å ha cd /path_til_build i bat-filen før java -jar etc.
