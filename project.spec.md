# Webshop Backend - Projekt Specifikáció és Állapotkövető

Ez a dokumentum a Webshop alkalmazás backend (Spring Boot, REST API) specifikációját és az implementáció folyamatát rögzíti. A projekt a vizsgaremek követelményrendszere alapján készült.

## 1. Technológiai Stack
* **Keretrendszer:** Java Spring Boot
* **Adatbázis:** MySQL (Relációs)
* **Biztonság:** Spring Security + JWT (Állapotmentes / Stateless)
* **Fájlkezelés:** Lokális fájlrendszer (Local Storage)
* **Architektúra:** REST API (Többrétegű: Controller -> Service -> Repository)
* **Eszközök:** Maven/Gradle, Git, JUnit, IntelliJ IDEA / VS Code

---

## 2. Adatmodell és Entitások

- [x] **User:** Felhasználói adatok (id, name, email, password, phone, Role [GUEST, USER, ADMIN]).
- [x] **Product:** Termékadatok (id, name, description, price, imageUrl).
- [x] **CartItem:** A felhasználó kosarának tartalma (id, User, Product, quantity).
- [x] **Order:** Leadott rendelések (id, User, totalAmount, createdAt, shippingName, shippingEmail, shippingPhone, shippingAddress, paymentMethod, isPaid).
- [x] **OrderItem:** A rendeléshez tartozó rögzített tételek (id, Order, Product, quantity, priceAtPurchase).

---

## 3. Fejlesztési Ütemterv (Commit Tracker)

A fejlesztés a "Conventional Commits" formátumot követi (pl. `feat:`, `chore:`, `fix:`).

### Elkészült Fázisok (1-7)
- [x] Fázis 1: Projekt alapok és Konfiguráció
- [x] Fázis 2: Termékkatalógus (Core Domain)
- [x] Fázis 3: Hitelesítés (Stateless JWT)
- [x] Fázis 4: Kosár Funkciók
- [x] Fázis 5: Rendelési Folyamat (Alap)
- [x] Fázis 6: Adminisztrációs Funkciók és Jogosultságok
- [x] Fázis 7: Validáció, Hibakezelés és Tesztelés

### Hátralévő Fázisok (Refaktorálás és Hiányzó Funkciók)

- [ ] **Fázis 8: Megrendelés Refaktorálása (Adatok és Státusz törlése)**
    - `OrderStatus` enum és mező törlése.
    - `CheckoutRequestDto` kibővítése: name, email, phone, address mezőkkel.
    - `Order` entitás kibővítése a számlázási/szállítási adatok historikus tárolására.

- [ ] **Fázis 9: Egyszerűsített Lokális Képfeltöltés**
    - Lokális `uploads/` mappa konfigurálása a `application.properties`-ben.
    - `StorageService` implementálása (mentés a fájlrendszerbe).
    - `WebMvcConfigurer` beállítása a statikus fájlok kiszolgálására (`/uploads/**`).
    - `ProductController` módosítása `MultipartFile` fogadására.

- [ ] **Fázis 10: Szimulált Fizetési Rendszer**
    - `PaymentMethod` enum létrehozása (`CASH`, `CARD`).
    - Fizetési mód integrálása a `CheckoutRequestDto`-ba és az `Order` entitásba.
    - Egyszerű szimulált fizetési logika az `OrderService`-ben (kártyás fizetésnél flag beállítása).

- [ ] **Fázis 11: Felhasználói Profil Módosítása**
    - `UserController` és `UserService` metódusok a saját adatok módosítására (`PUT /api/users/me`).
    - Név, telefonszám és jelszó csere támogatása.

*Megjegyzés: A keresés és szűrés funkciók implementálása a Frontend kliensalkalmazás hatáskörébe tartozik (kliensoldali szűrés a /api/products teljes listáján).*

---

## 4. API Végpontok (Referencia)

### Hitelesítés és Felhasználó (Nyilvános / Regisztrált)
* `POST /api/auth/register` - Új regisztráció
* `POST /api/auth/login` - Bejelentkezés, JWT token visszaadása
* `PUT /api/users/me` - **[ÚJ]** Saját profil adatainak módosítása

### Termékek
* `GET /api/products` - Összes termék
* `GET /api/products/{id}` - Termék részletei
* `POST /api/products` - Új termék rögzítése és **[ÚJ]** Képfeltöltés (Admin)
* `PUT /api/products/{id}` - Termék módosítása és **[ÚJ]** Képfeltöltés (Admin)
* `DELETE /api/products/{id}` - Termék törlése (Admin)

### Kosár (Regisztrált felhasználó)
* `GET /api/cart` - Saját kosár
* `POST /api/cart` - Termék kosárba
* `PUT /api/cart/{id}` - Mennyiség frissítése
* `DELETE /api/cart/{id}` - Tétel törlése

### Rendelések (Regisztrált felhasználó)
* `POST /api/orders` - Rendelés leadása fizetési móddal (Checkout)
* `GET /api/orders` - Saját rendelési előzmények

### Admin
* `GET /api/admin/users` - Felhasználók listázása (Admin)
* `DELETE /api/admin/users/{id}` - Felhasználó törlése (Admin)