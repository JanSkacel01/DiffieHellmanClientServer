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
            String pstr, gstr, Yastr;
            String serverName = "localhost";
            int port = 8088;

            long xa = 15;

            long Kab, Yb;

            Scanner scanner = new Scanner(System.in);

            System.out.println("Zadejte dvě čísla (g a q). Pro automatické generování nechte prázdné:");

            long q = getPrimeFromUserOrGenerate(scanner, 10000);
            long g = getNumber(scanner, q);

            System.out.println("Veřejné hodnoty:");
            System.out.println("q = " + q);
            System.out.println("g = " + g);

            // Established the connection
            System.out.println("Připojuji se na " + serverName
                    + " na portu " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Připojeno k "
                    + client.getRemoteSocketAddress());

            // Sends the data to client
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            pstr = Long.toString(q);
            out.writeUTF(pstr); // Sending p

            gstr = Long.toString(g);
            out.writeUTF(gstr); // Sending g

            long Ya = modExp(g, xa, q); // calculation of Ya
            Yastr = Long.toString(Ya);
            out.writeUTF(Yastr); // Sending Ya

            // Client's Private Key
            System.out.println("Klientův privátní klíč = " + xa);

            // Accepts the data
            DataInputStream in = new DataInputStream(client.getInputStream());

            Yb = Long.parseLong(in.readUTF());
            System.out.println("Veřejný klíč serveru = " + Yb);

            Kab = modExp(Yb, xa, q);
            //Kab = ((Math.pow(Yb, xa)) % q); // calculation of Adash

            System.out.println("Tajný klíč k šifrování Ka,b = "
                    + Kab);

            // Prompt user for text to encrypt
            System.out.println("Zadejte text k zašifrování (podpora české abecedy):");
            String plaintext = scanner.nextLine();

            // Encrypt the text
            String encryptedText = encryptText(plaintext, Kab);
            System.out.println("Zašifrovaný text: " + encryptedText);

            out.writeUTF(encryptedText); // Sending Ya

            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Encryption function
    public static String encryptText(String plaintext, long key) {
        String alphabet = "AaÁáBbCcČčDdĎďEeĚěÉéFfGgHhIiJjKkLlMmNnOoÓóPpQqRrŘřSsŠšTtŤťUuÚúŮůVvWwXxYyZzŽž"; // Czech alphabet
        StringBuilder encrypted = new StringBuilder();
        int mod = alphabet.length();

        for (char c : plaintext.toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                // Handle case preservation while applying encryption
                int newIndex = (alphabet.indexOf(c) + (int) key) % mod;
                encrypted.append(alphabet.charAt(newIndex));
            } else {
                // Keep non-alphabet characters as is
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }


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

    // Helper function to check if a number is prime
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

    public static long getNumber(Scanner scanner, long maxLimit) {
        Random random = new Random();
        long result = -1;

        while (result <= 0 || result >= maxLimit) {
            System.out.print("Zadejte g: ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                result = 1 + random.nextLong(maxLimit - 1); // Generate random number between 1 and maxLimit-1
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

    public static long modExp(long base, long exp, long mod) {
        long result = 1;
        base = base % mod; // Ensure base is within mod initially

        while (exp > 0) {
            // If exp is odd, multiply base with result
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }

            // Square the base and reduce exp by half
            base = (base * base) % mod;
            exp = exp >> 1; // Equivalent to exp /= 2
        }

        return result;
    }
}
