# Kryptografická komunikace klient-server

Tento projekt demonstruje zabezpečenou výměnu šifrovaných zpráv mezi klientem a serverem.
Použité technologie zahrnují:
- **Diffie-Hellmanův algoritmus** pro dohodu na šifrovacím klíči.
- **Caesarovu šifru** pro šifrování a dešifrování zpráv.

---

## Funkcionalita
Projekt obsahuje dvě části:

### Server
- Server čeká na připojení klienta.
- Po připojení proběhne výměna Diffie-Hellmanových parametrů `q`, `g` a veřejných klíčů.
- Na základě těchto dat se pak dopočítá společný klíč
- Server přijímá šifrované zprávy od klienta, dešifruje je a zobrazuje na výstupu.

### Klient
- Klient se připojuje k serveru.
- Probíhá výměna Diffie-Hellmanových parametrů a veřejných klíčů.
- Na základě těchto dat se pak dopočítá společný klíč
- Klient šifruje zprávy pomocí Caesarovy šifry a odesílá je na server.

---

## Požadavky
- **Java Development Kit (JDK) 8 nebo novější**
- **Java Runtime Environment (JRE)** (pro spuštění `.jar` souborů)
- Libovolný IDE (např. IntelliJ IDEA, Eclipse) pro spuštění projektu ze zdrojových kódů.

---

## Spuštění projektu

### V IDE
1. Načtěte projekt do svého IDE.
2. Ujistěte se, že všechny třídy jsou správně zkompilované.
3. Nejprve spusťte třídu `Server`:
   - Server se spustí na výchozím portu `8088` a bude čekat na připojení klienta.
4. Poté spusťte třídu `Client`:
   - Klient se připojí k serveru na `localhost` a portu `8088`.

**Poznámka**: Server musí být spuštěn před klientem.

### Spuštění jako `.jar`
1. Spusťte server:
   ```bash
   java -jar Server.jar
   ```
2. Spusťte klienta:
   ```bash
   java -jar Client.jar
   ```

**Poznámka**: Server musí být spuštěn před klientem. Při spouštění jar souborů je nutné 
být ve shodné složce, jako samotné jar soubory. Dále může být problém při spouštění v příkazovém řádce
na platformě Windows, kdy se vyskytují problémy s šifrováním a dešifrováním díky rozdílnému formátování 
textu oproti IDE. Doporučuje se využívat bash.

---

## Příklad komunikace
1. Po spuštění serveru se zobrazí:
   ```
   Čekám na klienta na portu 8088...
   ```
2. Spuštěním klienta následujte instrukce pro zadání parametrů `q` a `g`.
3. Klient šifruje text pomocí Caesarovy šifry a odešle jej serveru.
4. Server zprávu dešifruje a zobrazí ji na konzoli.

---

## Využité nástroje
V tomto projektu bylo využito ChatGPT pro generování některých metod, zejména metod pro optimalizovaný výpočet modulární exponentace.
