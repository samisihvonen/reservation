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

Prompti:
"Haluan käyttää postgres"

Tekoälyn vastaus:

Jos haluat vaihtaa yksinkertaisesta listasta PostgreSQL-tietokantaan, meidän on tehtävä muutama muutos. Koska kyseessä on ennakkotehtävä, suosittelen käyttämään Dockeria tai Docker Composea PostgreSQL-tietokannan pyörittämiseen, jotta ympäristön pystytys pysyy tarkastajalle helppona.

Tässä on askeleet, joilla muutat "juniorin" koodin käyttämään oikeaa tietokantaa. Tämä on loistava sisältö Vaiheeseen 2.

1. Lisää riippuvuudet (pom.xml)
Lisää PostgreSQL-ajuri ja Spring Data JPA:

XML

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
2. Määrittele yhteystiedot (application.properties)
Lisää nämä asetukset (tai muokkaa omiasi):

Properties

spring.datasource.url=jdbc:postgresql://localhost:5432/reservations
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
3. Päivitä Malli (Reservation.java)
Lisää JPA-annotaatiot, jotta Spring tietää miten tallentaa olio kantaan:

Java

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    private String id = UUID.randomUUID().toString();
    
    @Column(nullable = false)
    private String roomId;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private String userName; // 'user' on varattu sana SQL:ssä, vaihdetaan nimeä

    // Muista tyhjä konstruktori JPA:lle, getterit ja setterit
}
4. Luo Repository-rajapinta
Poista ArrayList kontrollerista ja luo uusi rajapinta:

Java

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByRoomId(String roomId);
}
5. Päivitä Kontrolleri
Injektoi repository ja muuta logiikka käyttämään sitä. Huomaa, että nyt haku tapahtuu suoraan tietokannasta:

Java

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository repository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Reservation req) {
        // Logiikka pysyy samana, mutta tarkistus tehdään kannasta
        List<Reservation> existing = repository.findByRoomId(req.getRoomId());
        
        boolean overlap = existing.stream().anyMatch(r -> 
            req.getStartTime().isBefore(r.getEndTime()) && 
            req.getEndTime().isAfter(r.getStartTime())
        );

        if (overlap) return ResponseEntity.status(409).body("Päällekkäinen varaus!");

        return ResponseEntity.ok(repository.save(req));
    }
}
Dokumentointi ja Commit-ehdotus:
Commit-viesti: refaktorointi: siirretty varaukset ArrayList-muistista PostgreSQL-tietokantaan

PROMPTIT.md: Kirjaa ylös, että pyysit tekoälyä vaihtamaan toteutuksen in-memory-listasta PostgreSQL:ään.

ANALYYSI.md: Mainitse, että user on varattu sana monissa tietokannoissa (kuten Postgres), joten kentän nimen muuttaminen userNameksi oli tarpeellinen tekninen korjaus.

Vinkki: Jos haluat tehdä tästä todella ammattimaisen, voit luoda projektin juureen docker-compose.yml tiedoston, jolla PostgreSQL-kontti käynnistyy yhdellä komennolla. Haluatko, että luon sinulle sellaisen?
