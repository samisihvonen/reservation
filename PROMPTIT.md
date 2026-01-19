1. PROMPTIT.md sisällysluettelo
Tämä on ensimmäinen sisältö, jonka tallennat tiedostoosi. Se kuvaa, miten pyysit tekoälyä luomaan Spring Boot + React -kokonaisuuden.

Kehote 1:

"Käytä Java Springboot ja React Typescript tämän kanssa [viitaten aiempaan tehtävänantoon]. Luo yksinkertainen kokoushuoneiden varausrajapinta, jossa on varauksen luonti, peruutus ja listaus. Käytä in-memory-tietokantaa."

Tekoälyn vastaus:

[Sisältää yllä annetun Java- ja TypeScript-koodin, jossa on peruslogiikka päällekkäisyyksien tarkistukseen, mutta puutteellinen virhekäsittely ja kova koodaus frontendissä.]

2. Projektirakenne (Ehdotus)
Suosittelen organisoimaan repositoryn seuraavasti ennen ensimmäistä committia:

/backend (Spring Boot -projekti)

/frontend (React + Vite/CRA -projekti)

PROMPTIT.md

ANALYYSI.md

3. Huomioita Vaiheeseen 2 (Ihmisen vuoro)
Kun olet tehnyt ensimmäisen commitin, tässä on muutama "juniorin" tekemä moka, jotka kannattaa korjata ammattimaisen lopputuloksen saamiseksi:

Säikeistys (Concurrency): ArrayList ei ole lankaturvallinen. Jos kaksi pyyntöä tulee samaan aikaan, varauslista voi korruptoitua tai päällekkäisyyden tarkistus pettää. Käytä esim. CopyOnWriteArrayList tai lukkoja.

DTO-mallit: Controller ottaa nyt suoraan sisään Reservation-olion, jossa on id. Käyttäjän ei pitäisi pystyä päättämään ID:tä; käytä erillistä CreateReservationRequest -luokkaa.

Frontendin tilanhallinta: App.tsx on tällä hetkellä hyvin raaka. Lomake puuttuu, ja päivämäärät on kovakoodattu.

Globaali virhekäsittely: Spring Bootissa kannattaa käyttää @ControllerAdvice-luokkaa siistimpien virheilmoitusten lähettämiseen.

Seuraava askel
Tee nyt seuraavat asiat:

Alusta Git-repository: git init.

Lisää yllä oleva koodi tiedostoihin.

Luo PROMPTIT.md.

Tee Commit #1: git add . && git commit -m "Alkuperäinen AI-generoitu commit".