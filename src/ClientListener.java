/**
 * Created by KarlFredrik on 2016-04-13.
 */

import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;

import com.google.gson.*;
import sun.rmi.runtime.Log;


public class ClientListener extends Thread {
    private ServerDispatcher mServerDispatcher;
    private ClientInfo mClientInfo;
    private BufferedReader mIn;
    private String query;
    private String action;
    File encrypted;
    File decrypted;
    File encryptedAesKeyMix;
    File rsaPrivateKeyMix;
    FileEncryption encryption;
    Socket socket;
    Boolean receiver;
    String receiverIP;
    String receiverPort;

    public ClientListener(ClientInfo aClientInfo, String mreceiverIP, String mreceiverPort)
            throws IOException {
        mClientInfo = aClientInfo;
        receiverIP = mreceiverIP;
        receiverPort = mreceiverPort;
        //mServerDispatcher = aServerDispatcher;
        socket = aClientInfo.mSocket;
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
                //mServerDispatcher.dispatchMessage(mClientInfo, query, action);
            }
        } catch (IOException ioex) {
            // Problem reading from socket (communication is broken)
        }

        // Communication is broken. Interrupt both listener and sender threads
        //mClientInfo.mClientListener.interrupt();
        //mClientInfo.mClientSender.interrupt();
        //mServerDispatcher.deleteClient(mClientInfo);

    }

    private void processMessage(String jsonMessage) {
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(jsonMessage);
        String activity = jo.get("activity").getAsString().toLowerCase();
//        System.out.println(jsonMessage);
        if (activity.equalsIgnoreCase("vote")) {
            encrypted = new File("/srvakf/KandidatServer/", "encrypted1.txt");
            decrypted = new File("/srvakf/KandidatServer/", "decrypted1.txt");
            encryptedAesKeyMix = new File("/srvakf/KandidatServer/", "encryptedAesKeyMix.txt");
            rsaPrivateKeyMix = new File("/srvakf/KandidatServer/", "privateSender.der");
            receiver = false;
            String message;

            String encryptedAesKey = jo.get("aeskey").toString();
            try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(encryptedAesKeyMix, false /*append*/));
            writer.write(encryptedAesKey);
            writer.close();
            encryption.loadKey(encryptedAesKeyMix, rsaPrivateKeyMix);
                String encryptedMessage = jo.get("message").toString();
                writer = new BufferedWriter(new FileWriter(encrypted, false /*append*/));
                writer.write(encryptedMessage);
                writer.close();
            encryption.decrypt(encrypted, decrypted);
                jsonMessage = encryption.getJsonString(decrypted).replace("RANDOM_SALT", "");
                ClientSender clientSender =
                        new ClientSender(mClientInfo, jsonMessage, receiverIP, receiverPort);
                clientSender.start();



        } catch (IOException i){
            i.printStackTrace();
        } catch (GeneralSecurityException e){
                e.printStackTrace();
            }

            /*JsonArray data = (JsonArray) jo.get("data");
            message = data.get(0).getAsString();
            address = data.get(1).getAsString();
            System.out.println(message);
            System.out.println(address);*/
        }

        if (activity.equalsIgnoreCase("receive")){
            receiver = true;
            receiverIP = socket.getInetAddress().getHostAddress();
            receiverPort = "" + socket.getPort();
        }
    }

    public void decrypt (File in, File out){
        try {

            encryption.decrypt(in, out);
        } catch (IOException i){
            i.printStackTrace();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        }
    }
    }

