/*Ukupni broj preuzetih polazaka*/
SELECT COUNT(*) AS "Broj dolazaka" FROM AERODROMI_DOLASCI ad; 

/*Ukupni broj preuzetih dolazaka*/
SELECT COUNT(*) AS "Broj polazaka" FROM AERODROMI_POLASCI ap; 

/*Broj pracenih aerodroma*/
SELECT COUNT(*) AS "Broj pracenih aerodroma" FROM AERODROMI_PRACENI ap;

/*Ispis pracenih aerodroma*/
SELECT ap.ident AS "ICAO" FROM AERODROMI_PRACENI ap;

/*Prikaz broja dolazaka po danima za sve pracene aerodrome*/
SELECT DATE(FROM_UNIXTIME(ad.firstSeen)) AS Datum, COUNT(DATE(FROM_UNIXTIME(ad.firstSeen))) AS "Ukupno dolazaka"
FROM AERODROMI_DOLASCI ad GROUP BY Datum ORDER BY Datum ASC;

/*Prikaz broja polazaka po danima za sve pracene aerodrome*/
SELECT DATE(FROM_UNIXTIME(ap.firstSeen)) AS Datum, COUNT(DATE(FROM_UNIXTIME(ap.firstSeen))) AS "Ukupno polazaka"
FROM AERODROMI_POLASCI ap GROUP BY Datum ORDER BY Datum ASC;


/*LDZA polasci*/
SELECT DATE(FROM_UNIXTIME(ap.firstSeen)) AS Datum, COUNT(*) AS "LDZA polasci"
FROM AERODROMI_POLASCI ap WHERE ap.estDepartureAirport ="LDZA" GROUP BY Datum ORDER BY Datum ASC;

/*LDZA dolasci*/
SELECT DATE(FROM_UNIXTIME(ad.firstSeen)) AS Datum, COUNT(*) AS "LDZA dolasci"
FROM AERODROMI_DOLASCI ad WHERE ad.estArrivalAirport  ="LDZA" GROUP BY Datum ORDER BY Datum ASC;

/*Po svakom danu, za svaku aerodrom, broj dolazaka*/
SELECT DATE(FROM_UNIXTIME(ad.firstSeen)) AS Datum, ad.estArrivalAirport AS aerodromDolaska, COUNT(*) AS "Ukupno dolazaka"
FROM AERODROMI_DOLASCI ad GROUP BY Datum, aerodromDolaska ORDER BY Datum ASC;

/*Po svakom danu, za svaku aerodrom, broj polazaka*/
SELECT DATE(FROM_UNIXTIME(ap.firstSeen)) AS Datum, ap.estDepartureAirport  AS aerodromPolaska, COUNT(*) AS "Ukupno polazaka"
FROM AERODROMI_POLASCI ap GROUP BY Datum, aerodromPolaska ORDER BY Datum ASC;

/*Broj korisnika*/
SELECT COUNT(*) "Broj korisnika" FROM KORISNICI k;
/*Broj uloga*/
SELECT COUNT(*) "Broj uloga" FROM ULOGE k;
/*Broj aerodroma*/
SELECT COUNT(*) "Broj uloga" FROM airports k;







