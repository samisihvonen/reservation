# PROMPTIT.md - Projektin Kehityshistoria

## Yleiskatsaus
Tämä dokumentti sisältää kaikki promptit (kysymykset) ja vastaukset, jotka johtivat nykyisen kokovaraussysteemin luomiseen. Dokumentti on yhdessä ANALYYSI.md:n kanssa projektin täydellinen kehityspolku.

---

## 📋 Kehote 1: Alkuperäinen koodipohja

**Pyyntö:**
```
"Käytä Java Springboot ja React Typescript tämän kanssa [viitaten aiempaan tehtävänantoon]. 
Luo yksinkertainen kokoushuoneiden varausrajapinta, jossa on varauksen luonti, peruutus ja listaus. 
Käytä in-memory-tietokantaa."
```

**Tulos:** 
- Perusrakenne Spring Boot -sovellukselle (Controller + Model)
- Yksinkertainen React-frontend (TypeScript)
- In-memory ArrayList -ratkaisu varausten tallentamiseen
- Peruslogiikka päällekkäisyyksien tarkistukseen

**Commit #1:**
```bash
git commit -m "feat: alkuperäinen AI-generoitu Spring Boot + React pohja"
```

---

## 📋 Kehote 2: PostgreSQL-integraatio

**Pyyntö:**
```
"Haluan käyttää postgres"
```

**Tulos:**
- Lisätty `spring-boot-starter-data-jpa` riippuvuus
- Lisätty PostgreSQL-ajuri (postgresql)
- Konfiguroitu `application.properties` tietokantayhteydelle
- Päivitetty `Reservation.java` JPA-annotaatioilla (@Entity, @Table, @Column)
- Luotu `ReservationRepository` -rajapinta JpaRepository:stä laajentaen
- Päivitetty `ReservationController` käyttämään repositorya ArrayList:in sijaan

**Tekniset muutokset:**
- Vaihdettu kenttä `user` → `username` (varattuja sanoja PostgreSQL:ssä)
- Hibernate automaattisesti loi tietokannan taulun `reservations`

**Commit #2:**
```bash
git commit -m "refactor: siirretty varaukset ArrayList:ista PostgreSQL-tietokantaan"
```

**Huomio:** `user` on varattu sana SQL:ssä, joten sitä ei voi käyttää sarakeena. Tämä on tärkeä oppitunti SQL-kehityksestä.

---

## 📋 Kehote 3: Service-kerros, DTOs ja Global Exception Handler

**Pyyntö:**
```
"Luo Service-kerros, DTOs (CreateReservationRequest, ReservationResponse) 
ja globaali exception handler (@ControllerAdvice)"
```

**Tulos:**

### Uudet tiedostot:

1. **DTOs** (`src/main/java/com/example/backend/dto/`)
   - `CreateReservationRequest.java` - validoitu input
   - `ReservationResponse.java` - structuroitu output

2. **Custom Exceptions** (`src/main/java/com/example/backend/exception/`)
   - `ReservationException.java` - kanta-luokka
   - `RoomAlreadyBookedException.java` - 409 Conflict
   - `InvalidReservationTimeException.java` - 400 Bad Request

3. **Service-kerros** (`src/main/java/com/example/backend/service/`)
   - `ReservationService.java` - kaikki bisneslogiikka tässä
   
4. **Exception Handler** (`src/main/java/com/example/backend/exception/`)
   - `GlobalExceptionHandler.java` - @ControllerAdvice

5. **Päivitetty** `ReservationController.java`
   - Nyt pelkästään HTTP-pyynnöt, ei bisneslogiikkaa

### Arkkitehtuurin parannus:

```
Ennen (Legacy):
┌─────────────────────────────────────┐
│  ReservationController              │
│  - HTTP-käsittely                   │
│  - Validointi                       │
│  - Päällekkäisyyden tarkistus       │
│  - Tietokantakutsu                  │
│  - Virhekäsittely                   │
└─────────────────────────────────────┘

Jälkeen (Professional):
┌────────────────────────────────────────┐
│  ReservationController                 │
│  - HTTP-pyynnöt (GET, POST, DELETE)   │
│  - @Valid validointi                   │
└────────────────────────────────────────┘
           ↓
┌────────────────────────────────────────┐
│  ReservationService                    │
│  - Validointi (aika, päällekkäisyys)  │
│  - DTO-konversio                       │
│  - Bisneslogiikka                      │
└────────────────────────────────────────┘
           ↓
┌────────────────────────────────────────┐
│  ReservationRepository (JPA)           │
│  - Tietokantakysely                    │
└────────────────────────────────────────┘
           ↓
┌────────────────────────────────────────┐
│  PostgreSQL Database                   │
└────────────────────────────────────────┘

Virhekäsittely:
┌────────────────────────────────────────┐
│  GlobalExceptionHandler (@ControllerAdvice)
│  - Kaikki exceptiot käsitellään tässä  │
│  - Johdonmukainen error-formaatti      │
└────────────────────────────────────────┘
```

### Lisätyt ominaisuudet:

- **Audit-kentät:** `createdAt` ja `updatedAt` (automaattisesti)
- **Validointi:** @NotBlank, @NotNull annotaatiot DTO:issa
- **Consistency:** Kaikki HTTP 400/409/500 virheet samassa muodossa
- **Separation of Concerns:** Controller != Service != Repository

**Commit #3:**
```bash
git commit -m "refactor: Service-kerros, DTOs ja @ControllerAdvice"
```

---

## 🔄 Seuraavat vaiheet (Vaihe 3+)

Kun tämä pohja on kunnossa, ammattimaiset parannukset olisivat:

1. **Unit-testit** (JUnit 5 + Mockito)
   - ReservationService -testit
   - ReservationController -testit
   - GlobalExceptionHandler -testit

2. **Integraatiotestit** (@SpringBootTest)
   - Testaa koko REST-API-polkua
   - Käytä TestContainers PostgreSQL:lle

3. **Frontend-parannukset** (React)
   - Todellinen lomake varauksen luomiseen
   - Error-käsittely API-virheistä
   - Loading-indikaattorit
   - Kalenterinäkymä varausten visualisoimiseen

4. **Docker Compose** -kehitysympäristö
   - PostgreSQL-kontti
   - Spring Boot -kontti
   - React-kontti

5. **API-dokumentaatio** (Swagger/OpenAPI)
   - Automaattisesti generoitu dokumentaatio
   - Swagger UI for testing

6. **Logging** (SLF4J + Logback)
   - Strukturoitu loggaus
   - JSON-loggaus production-ympäristölle

7. **Security** (Spring Security)
   - JWT-tokenit
   - Käyttäjätodentus
   - Rooli-perustainen pääsy (RBAC)

---

## 📊 Yhteenveto kehityksestä

| Vaihe | Focus | Status |
|-------|-------|--------|
| 1 | In-memory pohja | ✅ Valmis |
| 2 | PostgreSQL + Arkkitehtuuri | ✅ Valmis |
| 3 | Service + DTOs + Exception Handler | ✅ Valmis |
| 4 | Testit | ⏳ Seuraavaksi |
| 5 | Frontend-parantelu | ⏳ Seuraavaksi |
| 6 | Docker Compose | ⏳ Valinnainen |
| 7 | API-dokumentaatio | ⏳ Valinnainen |

---