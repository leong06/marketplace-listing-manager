# Marketplace Listing Manager

Egy asztali számítógépekre optimalizált webes alkalmazás **Spring Boot** és **Vaadin Flow** alapokon, amelynek célja, hogy segítsen rendszerezni az online piactereken eladni kívánt tárgyakat. Egyetlen, jól átlátható felületet biztosít a kategóriák, a termékleírások, a hirdetési státuszok és a helyi fényképes mappák kezeléséhez Windowson.

## Áttekintés

A termékek különböző piactereken történő árusítása gyakran vezet szétszóródott fotómappákhoz, újraírt leírásokhoz, valamint ahhoz, hogy elveszik a fonal, hogy melyik tárgy nincs még meghirdetve, melyik az aktív, vagy melyik kelt el már. A **Marketplace Listing Manager** ezt úgy oldja meg, hogy egy helyi PostgreSQL adatbázist kombinál a strukturált fájlrendszerbeli fotókezeléssel – így minden egyes termék mellé a leírása és státusza mellett egy dedikált, a lemezen automatikusan rendszerezett fotómappa is tartozik.

## Főbb jellemzők

### Kategória- és termékkezelés
- **Kategóriarendszer:** Kategóriák létrehozása, böngészése és törlése beépített védelemmel a nem üres kategóriák törlése ellen.
- **Státuszkövetés és munkafolyamat:** A termékek nyomon követése három életciklus-stádiumon keresztül:
  - **Nincs meghirdetve (*Not Listed*)** – Újonnan létrehozott termékek, amelyek előkészítésre várnak.
  - **Aktív hirdetés (*Active Listing*)** – A kategória-rácsos nézet tetejére rögzítve, jól megkülönböztethető kék kiemelési körvonallal a jobb láthatóság érdekében.
  - **Eladva (*Sold*)** – Automatikusan átkerül az aktív kategórianézetekből egy dedikált **Eladott termékek (*Sold Items*)** archív nézetbe.
- **Kronologikus sorrend:** A termékek úgy vannak rendezve, hogy az aktív hirdetések vannak elöl, őket követik a legújabb termékek a tetején (`createdAt DESC`).

### Automatizált helyi fotótárolás
- **Drag-and-Drop többfájlos feltöltés:** Több fotó feltöltése terméklétrehozás közben, vagy további képek hozzáadása később közvetlenül a termékrészletek nézetből.
- **Automatikus mappalétrehozás és tisztítás:** Az első feltöltéskor az alkalmazás automatikusan létrehoz egy dedikált mappát a beállított gyökérútvonal alatt `<Kategória>_<TermékCíme>_<ÉÉÉÉ-HH-NN>` néven, eltávolítva a Windows által tiltott karaktereket (`\ / : * ? " < > |`).
- **Szekvenciális fájlátnevezés:** A feltöltött képek a termék mappáján belül automatikusan sorszámozást kapnak (`001.jpg`, `002.jpg`, `003.png`, …).
- **Automatikus takarítás:** Egy termék törlése megerősítő párbeszédablakon keresztül törli az adatbázisrekordot és a hozzá tartozó fotókönyvtárat is a lemezen.

### Asztali ergonómia és akadálymentesség
- **Egykattintásos Fájlkezelő integráció:** Egy termék helyi Windows fotómappájának megnyitása közvetlenül az `explorer.exe`-ben a böngészőfelületről.
- **Egykattintásos vágólapra másolás:** A többsoros termékleírás másolása a rendszer vágólapjára egyetlen gombnyomással, a piactéri űrlapokba való beillesztéshez.
- **Jól látható felhasználói felület:** Egyedi Lumo stílus, megnövelt méretekkel a kényelmes olvashatóság érdekében, kártyaalapú fotógalériákkal és dupla kattintásos rácsos navigációval.

## Technológiai stack és architektúra

| Réteg | Technológia | Részletek |
| :--- | :--- | :--- |
| **Nyelv és futtatókörnyezet** | Java 25 | Modern Java recordok, stream-ek és `java.nio.file` API-k |
| **Backend keretrendszer** | Spring Boot 4 / Spring MVC | Függőséginjekció, konfiguráció és életciklus-események |
| **UI keretrendszer** | Vaadin Flow 25 (Lumo téma) | Szerveroldali Java UI, `Binder` űrlapvalidáció, `UploadHandler` és `DownloadHandler` adatfolyam-kezelés |
| **Perzisztencia** | Spring Data JPA / Hibernate | Entitáskapcsolatok (`@ManyToOne` lusta betöltés), származtatott repository lekérdezések, tranzakciós szolgáltatások |
| **Adatbázis és migráció** | PostgreSQL + Flyway | Verziózott SQL séma-migrációk (`V1`, `V2`) `ddl-auto: validate` beállítással |
| **Csomagolás** | Maven + JDK `jpackage` | Éles Vaadin frontend csomag beágyazott JRE-vel ellátott natív Windows `.exe`-be csomagolva |

## Első lépések (Fejlesztés)

### Előfeltételek
- **JDK 25**
- **PostgreSQL** futtatása a `localhost:5432` címen egy `marketplace-postgres` nevű adatbázissal

### Futtatás helyben
```bash
./mvnw spring-boot:run
```
Az alkalmazás automatikusan lefuttatja a Flyway migrációkat, and a `http://localhost:8080` címen nyitható meg.