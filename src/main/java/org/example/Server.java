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
            int port = 8088;

            // Server Key
            long Xb = 11;

            // Client p, g, and key
            long clientQ, clientG, clientYa, Yb, Kba;
            String Ybstr;

            // Established the Connection
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Čekám na klienta na portu " + serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Připojeno k " + server.getRemoteSocketAddress());

            // Server's Private Key
            System.out.println("Serverův privátní klíč =  " + Xb);

            // Accepts the data from client
            DataInputStream in = new DataInputStream(server.getInputStream());

            clientQ = Long.parseLong(in.readUTF()); // to accept p
            System.out.println("Klientova hodnota q = " + clientQ);

            clientG = Long.parseLong(in.readUTF()); // to accept g
            System.out.println("Klientova hodnota g = " + clientG);

            clientYa = Long.parseLong(in.readUTF()); // to accept A
            System.out.println("Veřejný klíč klienta = " + clientYa);

            Yb = modExp(clientG, Xb, clientQ);
            Ybstr = Long.toString(Yb);

            // Sends data to client
            // Value of B
            OutputStream outToclient = server.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToclient);

            out.writeUTF(Ybstr); // Sending B

            Kba = modExp(clientYa, Xb, clientQ);
         //   Kba = ((Math.pow(clientYa, Xb)) % clientQ); // calculation of Bdash

            System.out.println("Tajný klíč k šifrování Kb,a = "
                    + Kba);

            String encryptedText = in.readUTF();

            System.out.println("Přijatý zašifrovaný text: \n" + encryptedText);

            // Decrypt the text
            String decryptedText = decryptText(encryptedText, Kba);
            System.out.println("Dešifrovaný text: " + decryptedText);

            server.close();
        }

        catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        }
        catch (IOException e) {
        }
    }

    public static String decryptText(String encryptedText, long key) {
        String alphabet = "AaÁáBbCcČčDdĎďEeĚěÉéFfGgHhIiJjKkLlMmNnOoÓóPpQqRrŘřSsŠšTtŤťUuÚúŮůVvWwXxYyZzŽž"; // Czech alphabet
        StringBuilder decrypted = new StringBuilder();
        int mod = alphabet.length();

        for (char c : encryptedText.toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                // Handle case preservation while applying decryption
                int newIndex = ((alphabet.indexOf(c) - (int) key) % mod + mod) % mod; // Fix for negative index
                decrypted.append(alphabet.charAt(newIndex));
            } else {
                // Keep non-alphabet characters as is
                decrypted.append(c);
            }
        }
        return decrypted.toString();
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
