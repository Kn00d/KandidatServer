/**
 * Created by KarlFredrik on 2016-04-13.
 */

import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;

import com.google.gson.*;


public class ClientListener extends Thread {
    private ServerDispatcher mServerDispatcher;
    private ClientInfo mClientInfo;
    private BufferedReader mIn;
    private String query;
    private String action;

    public ClientListener(ClientInfo aClientInfo)
            throws IOException {
        mClientInfo = aClientInfo;
        // mServerDispatcher = aServerDispatcher;
        Socket socket = aClientInfo.mSocket;
        mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Until interrupted, reads messages from the client socket, forwards them
     * to the server dispatcher's queue and notifies the server dispatcher.
     */
    public void run() {
        try {
            while (!isInterrupted()) {

                String jsonString = mIn.readLine();
                if (jsonString == null) {
                    break;
                }
                processMessage(jsonString);
                mServerDispatcher.dispatchMessage(mClientInfo, query, action);
            }
        } catch (IOException ioex) {
            // Problem reading from socket (communication is broken)
        }

        // Communication is broken. Interrupt both listener and sender threads
        mClientInfo.mClientListener.interrupt();
        mClientInfo.mClientSender.interrupt();
        mServerDispatcher.deleteClient(mClientInfo);

    }

    private void processMessage(String jsonMessage) {
        decrypt (encrypted1, decrypted);
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(jsonMessage);
        String activity = jo.get("activity").getAsString().toLowerCase();
//        System.out.println(jsonMessage);
        if (activity.equalsIgnoreCase("vote")) {
            String message;
            String address;
            String encryptedMessage = jo.get("message").toString();

            JsonArray data = (JsonArray) jo.get("data");
            message = data.get(0).getAsString();
            address = data.get(1).getAsString();
            System.out.println(message);
            System.out.println(address);

        }
    }



    File encrypted1 = new File("encrypted1.txt");
    File decrypted = new File ("Decrypted.txt");
    File encryptedAesKey = new File("encryptedAesKey.txt");
    File rsaPrivateKey = new File("private.der");
    FileEncryption encryption;


    public void decrypt (File in, File out){
        try {
            encryption.loadKey(encryptedAesKey, rsaPrivateKey);

            encryption.decrypt(in, out);
        } catch (IOException i){
            i.printStackTrace();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        }
    }
    }

