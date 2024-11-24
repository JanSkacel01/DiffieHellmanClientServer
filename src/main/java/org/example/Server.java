package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {
    public static void main(String[] args) throws IOException
    {
        try {
            int port = 8088; // Port pro komunikaci na localhostu

            long Xb = 11; // Privátní klíč serveru

            // Klientovo Q,G, a veřejný klíč Ya
            // Serverův veřejný klíč a společný klíč Kba
            long clientQ, clientG, clientYa, Yb, Kba;
            String Ybstr;

            // Otevření spojení pro klienta
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Čekám na klienta na portu " + serverSocket.getLocalPort() + "...");

            // Přijetí klienta
            Socket server = serverSocket.accept();
            System.out.println("Připojeno k " + server.getRemoteSocketAddress());

            // Serverův privátní klíč
            System.out.println("Serverův privátní klíč =  " + Xb);

            // Přijetí dat od klienta (q, g, Ya)
            DataInputStream in = new DataInputStream(server.getInputStream());

            clientQ = Long.parseLong(in.readUTF());
            System.out.println("Klientova hodnota q = " + clientQ);

            clientG = Long.parseLong(in.readUTF());
            System.out.println("Klientova hodnota g = " + clientG);

            clientYa = Long.parseLong(in.readUTF());
            System.out.println("Veřejný klíč klienta = " + clientYa);

            // Výpočet veřejného klíče Yb
            Yb = modExp(clientG, Xb, clientQ);
            Ybstr = Long.toString(Yb);

            // Vytvoření output streamu pro klienta
            OutputStream outToclient = server.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToclient);

            out.writeUTF(Ybstr); // Odesílám veřejný klíč Yb

            // Výpočet společného klíče
            Kba = modExp(clientYa, Xb, clientQ);
            System.out.println("Tajný klíč k šifrování Kb,a = "
                    + Kba);

            // Přijetí zašiforovaného textu od klienta
            String encryptedText = in.readUTF();
            System.out.println("Přijatý zašifrovaný text: \n" + encryptedText);

            // Dešiforvání zašiforovaného textu
            String decryptedText = decryptText(encryptedText, Kba);
            System.out.println("Dešifrovaný text: " + decryptedText);

            // Uzavření spojení
            server.close();
        }

        catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Dešiforvání zašiforovaného textu - Caesarova šifra
    public static String decryptText(String encryptedText, long key) {
        String alphabet = "AaÁáBbCcČčDdĎďEeĚěÉéFfGgHhIiJjKkLlMmNnOoÓóPpQqRrŘřSsŠšTtŤťUuÚúŮůVvWwXxYyZzŽž";
        StringBuilder decrypted = new StringBuilder();
        int mod = alphabet.length();

        for (char c : encryptedText.toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                int newIndex = ((alphabet.indexOf(c) - (int) key) % mod + mod) % mod;
                decrypted.append(alphabet.charAt(newIndex));
            } else {
                decrypted.append(c);
            }
        }
        return decrypted.toString();
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
