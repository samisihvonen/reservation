# ANALYYSI.md - Tekninen analyysi ja design-päätökset

## Johdanto

Tämä dokumentti selittää tekniset ratkaisut ja arkkitehtuuriset päätökset, jotka tehtiin kokovaraussysteemin kehityksen aikana. Tavoitteena on dokumentoida, *miksi* tietyt valinnat tehtiin, ei vain *mitä* tehtiin.

---

## 1. Arkkitehtuurikerrokset (Layered Architecture)

### Rationale (Perustelu)

Alkuperäinen "juniori" -koodi laitti kaiken `ReservationController`-luokkaan. Tämä johtaa seuraaviin ongelmiin:

```java
// ❌ Huono: Controller tekee liikaa
@RestController
public class ReservationController {
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Reservation req) {
        // Validointi
        if (req.getStartTime().isBefore(LocalDateTime.now())) { ... }
        
        // Päällekkäisyyden tarkistus
        List<Reservation> existing = repository.findByRoomId(req.getRoomId());
        boolean overlap = existing.stream().anyMatch(...);
        
        // Virhekäsittely
        if (overlap) return ResponseEntity.status(409).body(...);
        
        // Tallennus
        return ResponseEntity.ok(repository.save(req));
    }
}
```

**Ongelmat:**
1. **Testaaminen on vaikeaa** - Ei voi testata bisneslogiikkaa ilman HTTP-pyynnön tekemistä
2. **Uudelleenkäyttö on vaikeaa** - Logiikka on sidottu HTTP-käyttöliittymään
3. **Huomattavuus heikko** - Vaikea nähdä, mitä koodi tekee
4. **Virhekäsittely sekava** - Eri paikoissa eri tavalla käsitelty

### Ratkaisu: Kerrosarkkitehtuuri

```java
// ✅ Hyvä: Erottelu huoleista (Separation of Concerns)

// 1. Controller = HTTP-interface
@RestController
public class ReservationController {
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody CreateReservationRequest req) {
        return ResponseEntity.ok(service.createReservation(req));
    }
}

// 2. Service = Business logic
@Service
public class ReservationService {
    public ReservationResponse createReservation(CreateReservationRequest req) {
        // Validointi
        if (req.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidReservationTimeException(...);
        }
        
        // Päällekkäisyyden tarkistus
        List<Reservation> existing = repository.findByRoomId(req.getRoomId());
        if (hasOverlap(req, existing)) {
            throw new RoomAlreadyBookedException(...);
        }
        
        // Tallennus ja palautus
        return toResponse(repository.save(newReservation));
    }
}

// 3. Repository = Data access
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByRoomId(String roomId);
}

// 4. Exception Handler = Centralized error handling
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleRoomAlreadyBooked(...) { ... }
}
```

**Hyödyt:**
- ✅ Yksinkertainen testata (`ReservationService` voidaan testata ilman HTTP:tä)
- ✅ Uudelleenkäytettävä (logiikkaa voidaan kutsua eri pinnoista)
- ✅ Ylläpidettävä (jokainen luokka tekee yhden asian)
- ✅ Skaalautuva (helppo lisätä uusia ominaisuuksia)

**Viite:** Tämä on soveltaminen SOLID-periaatteista, erityisesti **Single Responsibility Principle (SRP)**.

---

## 2. DTOs (Data Transfer Objects) vs Entities

### Ongelma: Entiteetin käyttäminen suoraan API:ssa

```java
// ❌ Huono: Entiteetti on suoraan API:ssa
@PostMapping
public ResponseEntity<?> create(@RequestBody Reservation req) {
    return ResponseEntity.ok(repository.save(req));
}
```

**Riskit:**
1. **ID-hallinta:** Käyttäjä voi lähettää oman `id`-arvon → tietokanta voi korruptoitua
2. **Tietokannan yksityiskohdat paljastetaan:** Frontend näkee `createdAt`, `updatedAt` -kentät, jotka vain palvelimen tulisi hallita
3. **Versioinnin vaikeus:** Jos tietokannan rakenne muuttuu, API rikkoutuu
4. **Mass Assignment -hyökkäys:** Käyttäjä voi muuttaa kenttiä, joita ei haluta muuttaa

### Ratkaisu: DTOs

```java
// ✅ Hyvä: Erillinen DTO API-sopimukselle

@Data
public class CreateReservationRequest {
    @NotBlank
    private String roomId;
    
    @NotNull
    private LocalDateTime startTime;
    
    @NotNull
    private LocalDateTime endTime;
    
    @NotBlank
    private String user;
    
    // Huomaa: Ei id-kenttää! Palvelin generoi sen.
    // Huomaa: Ei createdAt/updatedAt! Palvelin hallitsee ne.
}

@Data
public class ReservationResponse {
    private String id;
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String user;
    private LocalDateTime createdAt;  // ✅ Palvelin kontrolloi
    private LocalDateTime updatedAt;  // ✅ Palvelin kontrolloi
}
```

**Hyödyt:**
- ✅ Turvallinen: Käyttäjä voi lähettää vain hyväksyttävät kentät
- ✅ Julkinen API on erillään sisäisestä tietokantamallista
- ✅ Helppo versioida: Uusi API voi palauttaa enemmän/vähemmän kenttiä
- ✅ Validointi: @NotNull, @NotBlank -annotaatiot toimivat vain DTO:ssa

**Huomio:** Tämä on **Best Practice** REST API:ssa. Edes suuret sovellukset (Google, Microsoft) käyttävät DTOs:ia.

---

## 3. Custom Exceptions vs Generic ResponseEntity

### Ongelma: Ad-hoc virhekäsittely

```java
// ❌ Huono: Jokainen kontrolleri-metodi hallitsee virheitään eri tavoin
@PostMapping
public ResponseEntity<?> create(@RequestBody Reservation req) {
    List<Reservation> existing = repository.findByRoomId(req.getRoomId());
    boolean overlap = existing.stream().anyMatch(...);
    
    if (overlap) {
        return ResponseEntity.status(409).body("Huone on jo varattu valittuna aikana!");
    }
    
    if (req.getStartTime().isBefore(LocalDateTime.now())) {
        return ResponseEntity.status(400).body("Varaus ei voi olla menneisyydessä.");
    }
    
    return ResponseEntity.ok(repository.save(req));
}
```

**Ongelmat:**
1. **Epäyhtenäinen formaatti:** Frontend joutuu käsittelemään sekä stringejä että JSON:ia
2. **Vaikea ylläpitää:** Jokainen kontrolleri-metodi tekee saman homman
3. **HTTP-koodit sekotettu bisneslogiikkaan:** Vai pitäisikö olla 400 vai 409?
4. **Ei strukturoitua loggausta:** Vaikea seurata virheitä

### Ratkaisu: Custom Exceptions + Global Handler

```java
// ✅ Hyvä: Business logic heittää custom exceptions

// Service-kerros
@Service
public class ReservationService {
    public ReservationResponse createReservation(CreateReservationRequest req) {
        if (req.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidReservationTimeException(
                "Varaus ei voi olla menneisyydessä."
            );
        }
        
        List<Reservation> existing = repository.findByRoomId(req.getRoomId());
        if (hasOverlap(req, existing)) {
            throw new RoomAlreadyBookedException(
                "Huone on jo varattu valittuna aikana."
            );
        }
        
        return toResponse(repository.save(...));
    }
}

// Global Exception Handler
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleRoomAlreadyBooked(
            RoomAlreadyBookedException ex) {
        return ResponseEntity.status(409).body(
            new ErrorResponse(
                LocalDateTime.now(),
                409,
                "Huone varattu",
                ex.getMessage()
            )
        );
    }
    
    @ExceptionHandler(InvalidReservationTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTime(
            InvalidReservationTimeException ex) {
        return ResponseEntity.status(400).body(
            new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Virheellinen aika",
                ex.getMessage()
            )
        );
    }
}
```

**Hyödyt:**
- ✅ **Yhtenäinen formaatti:** Jokainen virhe palautetaan samassa muodossa
- ✅ **Ylläpidettävä:** Virhekäsittely on yhdessä paikassa
- ✅ **Selkeä:** HTTP-koodi vastaa bisneslogiikan exceptionia
- ✅ **Testattava:** Voidaan testata exceptionin heittämistä
- ✅ **Loggattava:** Järjestelmä voi loggata kaikki virheet samalla tavalla

**Huomio:** Spring Boot 3.0+ suosittaa `ProblemDetail` -formaattia, mutta custom `ErrorResponse` on simppelimpi ja ymmärrettävämpi.

---

## 4. Validation: @Valid vs Manual Checks

### Ongelma: Manuaalinen validointi

```java
// ❌ Huono: Validointi muistetaan tehdä jokaisessa paikassa
@PostMapping
public ResponseEntity<?> create(@RequestBody Reservation req) {
    if (req.getRoomId() == null || req.getRoomId().isEmpty()) {
        return ResponseEntity.badRequest().body("roomId puuttuu");
    }
    
    if (req.getStartTime() == null) {
        return ResponseEntity.badRequest().body("startTime puuttuu");
    }
    
    // ... jne
}
```

**Ongelmat:**
1. **Toistuva koodi:** Sama validointi tekemässä monessa paikassa
2. **Helppo unohtaa:** Voidaan unohtaa validointi jossain metodissa
3. **Vaikea ylläpitää:** Jos validointisääntö muuttuu, täytyy päivittää monta paikkaa

### Ratkaisu: JSR-303 Bean Validation

```java
// ✅ Hyvä: Declarative validation

@Data
public class CreateReservationRequest {
    @NotBlank(message = "Huoneen ID on pakollinen")
    private String roomId;
    
    @NotNull(message = "Alkamisaika on pakollinen")
    private LocalDateTime startTime;
    
    @NotNull(message = "Päättymisaika on pakollinen")
    private LocalDateTime endTime;
    
    @NotBlank(message = "Käyttäjän nimi on pakollinen")
    private String user;
}

// Controller käyttää @Valid
@PostMapping
public ResponseEntity<ReservationResponse> create(
        @Valid @RequestBody CreateReservationRequest req) {
    // Jos validation epäonnistuu, GlobalExceptionHandler käsittelee sen
    return ResponseEntity.ok(service.createReservation(req));
}
```

**Hyödyt:**
- ✅ **Declarative:** Validointisäännöt näkyvät selvästi
- ✅ **Kestävä:** Automaattisesti käytössä globaalissa exception handlerissa
- ✅ **Standardoitu:** JSR-303 on Java-standardi

---

## 5. Audit Fields (createdAt, updatedAt)

### Rationale (Perustelu)

```java
@Entity
@Table(name = "reservations")
public class Reservation {
    // ... muut kentät ...
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

**Miksi nämä kentät ovat tärkeitä:**

1. **Auditointia varten:** Voidaan nähdä, milloin varaus luotiin
2. **Debugging varten:** Voidaan nähdä, milloin varaus muutettiin viimeksi
3. **Liiketoiminnan vaatimukset:** Usein on pakollista seurata muutoksia
4. **Laillisuus:** Monissa jurisdiktioissa vaaditaan audit trail

**Tekninen toteutus:**
- `@CreationTimestamp` - Automaattisesti asettaa luontiajankohdan (Hibernate)
- `@UpdateTimestamp` - Automaattisesti päivittyy joka muutoksella
- `updatable = false` - `createdAt` ei voi muuttua

**Huomio:** Nämä kentät eivät ole käyttäjän syötteitä (DTO:ssa niitä ei ole). Ne ovat palvelimen hallitsemia.

---

## 6. Poikkeama: `user` vs `username` -kenttä

### Tekninen ongelma

SQL:ssä `USER` on varattu sana joissakin tietokannoissa (erityisesti PostgreSQL:ssä, kun käytetään tiettyjä versioita).

```sql
-- ❌ Huono: USER on varattu
CREATE TABLE reservations (
    id VARCHAR(255) PRIMARY KEY,
    user VARCHAR(255)  -- Virhe!
);

-- ✅ Hyvä: USERNAME ei ole varattu
CREATE TABLE reservations (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255)
);
```

**Ratkaisemme:**
- Entiteetissä: `private String user;` (Java-puolella käytämme intutiivisempaa nimeä)
- Tietokannassa: `@Column(name = "username")` (kertoo Hibernatelle käyttää `username` saraketta)
- Näin pystymme käyttämään `getUser()` / `setUser()` metodeja koodissa, mutta tietokannassa on `username`

```java
@Column(name = "username", nullable = false)
private String user;

// JPA:n ansiosta:
// reservation.getUser() → SELECT * FROM reservations WHERE username = ?
```

**Oppitunti:** SQL:n varattujen sanojen tunteminen on tärkeää. Yleisiä varattuja sanoja:
- `USER`, `ROLE`, `ORDER`, `GROUP`, `SELECT`, `FROM`, `WHERE`
- Ratkaisu: Käytä `username` tai `user_name` SQL:ssä

---

## 7. Repository Pattern

### Rationale

```java
// ✅ Hyvä: Spring Data JPA -repository abstraktion kautta

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByRoomId(String roomId);
}
```

**Hyödyt:**
1. **Abstraktio:** SQL-kyselyt piilotetaan
2. **Standardoitu:** Spring Data JPA generoi SQL:n automaattisesti metodin nimen perusteella
3. **Testattava:** Voidaan mock:itä testitesteissa
4. **Skaalautuva:** Voidaan vaihtaa tietokantaa ilman muutosta Business Logic -kerrokseen

**Kuinka se toimii:**
- `findByRoomId` → Spring Data JPA generoi: `SELECT * FROM reservations WHERE room_id = ?`
- Ei ole kirjoitettava SQL:ää käsin!

---

## 8. Yhteenveto: Ennen vs Jälkeen

### Ennen (Juniori-koodi)

```
❌ Kaikki logiikka controllerissa
❌ Ei DTOs:ia - entiteetti suoraan API:ssa
❌ Ad-hoc virhekäsittely
❌ Manuaalinen validointi
❌ ArrayList (thread-unsafe)
❌ Vaikea testata
❌ Vaikea uudelleenkäyttää
```

### Jälkeen (Ammattimaisesti)

```
✅ Kerrosarkkitehtuuri (Controller → Service → Repository)
✅ DTOs erotella entiteeteistä
✅ Globaali exception handler
✅ @Valid -validointi
✅ PostgreSQL (turvallinen, skalautuva)
✅ Helppo testata
✅ Helppo uudelleenkäyttää
✅ Yhteneväinen error-formaatti
✅ Audit fields
✅ Johdonmukainen nimeäminen
```

---

## 9. Seuraavat parannukset

### Lähiaikainen (1-2 viikkoa)
- [ ] Unit-testit (`ReservationService`, `GlobalExceptionHandler`)
- [ ] Integraatiotestit (REST API -polkuja)
- [ ] Frontend -parannukset (todellinen lomake, error-näyttö)

### Keskipitkä (1 kuukausi)
- [ ] Docker Compose -kehitysympäristö
- [ ] Swagger/OpenAPI dokumentaatio
- [ ] Logging (SLF4J + Logback)

### Pitkäaikainen (2-3 kuukautta)
- [ ] Spring Security + JWT
- [ ] Käyttäjänhallinnan logiikka
- [ ] Raportointi/Analytics
- [ ] Kalenterinäkymä frontendissa

---

## Lähteet ja viitteet

- [Spring Boot Best Practices](https://spring.io/guides/gs/rest-service/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [REST API Best Practices](https://restfulapi.net/)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Java Bean Validation (JSR-303)](https://beanvalidation.org/)

---

**Dokumentin versio:** 1.0  
**Päivitetty:** 2026-01-19  
**Kirjoittaja:** AI-apulainen + ihmisen ohjaus