package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GreetingServer {
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
            server.close();
        }

        catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        }
        catch (IOException e) {
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
