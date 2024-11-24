package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client {
    public static void main(String[] args)
    {
        try {
            //Název serveru a jeho port
            String serverName = "localhost";
            int port = 8088;

            //Privátní klíč
            long xa = 15;

            // Veřejné hodnoty q,g
            // veřejné klíče serveru i klienta (Ya, Yb)
            // společný klíč Kab
            long q,g,Kab,Ya, Yb;
            String pstr, gstr, Yastr;

            // Zadání prvočísla q a čísla g, nebo jejich generování
            Scanner scanner = new Scanner(System.in);
            System.out.println("Zadejte dvě čísla (q a g). Pro automatické generování nechte prázdné:");
            q = getPrimeFromUserOrGenerate(scanner, 10000);
            g = getNumber(scanner, q);
            System.out.println("Veřejné hodnoty:");
            System.out.println("q = " + q);
            System.out.println("g = " + g);

            // Vytvoření připojení k serveru
            System.out.println("Připojuji se na " + serverName
                    + " na portu " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Připojeno k "
                    + client.getRemoteSocketAddress());

            // Vytvoření output streamu
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            // odeslání hodnot q, g,
            pstr = Long.toString(q);
            out.writeUTF(pstr);
            gstr = Long.toString(g);
            out.writeUTF(gstr);

            // Výpočet a odeslání veřejného klíče Ya
            Ya = modExp(g, xa, q);
            Yastr = Long.toString(Ya);
            out.writeUTF(Yastr);

            // Klientův privátní klíč
            System.out.println("Klientův privátní klíč = " + xa);

            // Vytvoření input streamu
            DataInputStream in = new DataInputStream(client.getInputStream());

            // příjmutí veřejného klíče serveru
            Yb = Long.parseLong(in.readUTF());
            System.out.println("Veřejný klíč serveru = " + Yb);

            // Výpočet společného klíče Kab
            Kab = modExp(Yb, xa, q);
            System.out.println("Tajný klíč k šifrování Ka,b = "
                    + Kab);

            // Načtení textu od uživatele
            System.out.println("Zadejte text k zašifrování (podpora české abecedy):");
            String plaintext = scanner.nextLine();

            // Zašiforvání textu a odeslání do serveru
            String encryptedText = encryptText(plaintext, Kab);
            System.out.println("Zašifrovaný text: " + encryptedText);
            out.writeUTF(encryptedText);

            // Uzavření spojení
            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Šifrpvání textu pomocí Caesaraovy sifry
    public static String encryptText(String plaintext, long key) {
        String alphabet = "AaÁáBbCcČčDdĎďEeĚěÉéFfGgHhIiJjKkLlMmNnOoÓóPpQqRrŘřSsŠšTtŤťUuÚúŮůVvWwXxYyZzŽž"; // Czech alphabet
        StringBuilder encrypted = new StringBuilder();
        int mod = alphabet.length();

        for (char c : plaintext.toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                int newIndex = (alphabet.indexOf(c) + (int) key) % mod;
                encrypted.append(alphabet.charAt(newIndex));
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }

    // Načtení prvočísla od uživatele, nebo vygenerování náhodného
    public static long getPrimeFromUserOrGenerate(Scanner scanner, long upperBound) {
        while (true) {
            System.out.print("Zadejte q: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                long generatedPrime = generateRandomPrime(10, upperBound);
                System.out.println("q bylo vygenerováno jako: " + generatedPrime);
                return generatedPrime;
            }

            try {
                long value = Long.parseLong(input);
                if (isPrime(value)) {
                    return value;
                } else {
                    System.out.println("Číslo není prvočíslo. Zadejte prvočíslo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Špatný vstup. Zkuste to znovu");
            }
        }
    }

    // Generování náhodného prvočísla
    public static long generateRandomPrime(long lowerBound, long upperBound) {
        Random random = new Random();

        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Lower bound must be less than or equal to the upper bound.");
        }

        while (true) {
            long candidate = random.nextLong(upperBound - lowerBound + 1) + lowerBound;

            if (isPrime(candidate)) {
                return candidate;
            }
        }
    }

    // Zjišťuje zda je číslo prvočíslo
    public static boolean isPrime(long number) {
        if (number < 2) return false;
        if (number == 2 || number == 3) return true;
        if (number % 2 == 0 || number % 3 == 0) return false;

        long sqrt = (long) Math.sqrt(number);
        for (long i = 5; i <= sqrt; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /* Načte g od uživatele, nebo vygeneruje náhodné.
      g musí být větší než nula a menší než maxLimit (q).
     */
    public static long getNumber(Scanner scanner, long maxLimit) {
        Random random = new Random();
        long result = -1;

        while (result <= 0 || result >= maxLimit) {
            System.out.print("Zadejte g: ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                result = 1 + random.nextLong(maxLimit - 1);
            } else {
                try {
                    result = Long.parseLong(input);
                } catch (NumberFormatException e) {
                    System.out.println("Špatný vstup. Zkuste to znovu");
                    result = -1;
                }
            }

            if (result <= 0 || result >= maxLimit) {
                System.out.println("Číslo musí být větší než nula a menší než" + maxLimit + ".");
            }
        }
        System.out.println("g bylo vygenerováno jako: " + result);
        return result;
    }

    /* Optimalizovaná funkce pro modulární exponentaci pomocí metody “exponentiation by squaring”.
   Používá se v kryptografii, protože je efektivní i pro velmi velká čísla díky
   postupnému snižování velikosti exponentu a práci se zbytky místo kompletních čísel. */
    public static long modExp(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }

            base = (base * base) % mod;
            exp = exp >> 1;
        }

        return result;
    }
}
