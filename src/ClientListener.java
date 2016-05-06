/**
 * Created by KarlFredrik on 2016-04-13.
 */

import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import sun.rmi.runtime.Log;


public class ClientListener extends Thread {
    private ClientInfo mClientInfo;
    private BufferedReader mIn;
    File encrypted;
    File decrypted;
    File encryptedAesKeyMix;
    File rsaPrivateKeyMix;
    FileEncryption encryption;
    Socket socket;
    String receiverIP;
    int receiverPort;
    VoteReceiver votereceiver;
    FileUtils fileUtils;

    public ClientListener(ClientInfo aClientInfo, VoteReceiver voteReceiver)
            throws IOException {
        mClientInfo = aClientInfo;
        //mServerDispatcher = aServerDispatcher;
        socket = aClientInfo.mSocket;
        mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.votereceiver = voteReceiver;
    }

    /**
     * Until interrupted, reads messages from the client socket, forwards them
     * to the server dispatcher's queue and notifies the server dispatcher.
     */
    public void run() {
        try {
            while (!isInterrupted()) {

                String next;
                StringBuilder jsonString = new StringBuilder();
                while ((next = mIn.readLine()) != null) {
                    jsonString.append(next);
                    if (jsonString.toString().endsWith("\"}")){
                        break;
                    }

                }
                if (jsonString == null) {
                    break;
                }
                processMessage(jsonString.toString());
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

    private void processMessage(String jsonMessage){
        fileUtils = new FileUtils();
        System.out.println("JSONMESSAGE: "+jsonMessage);
        String [] parts = jsonMessage.split("\"?,?\"[a-z]*\":\"");
        //JsonParser parser = new JsonParser();
        //JsonObject jo = (JsonObject) parser.parse(jsonMessage);
        String activity = parts [1];
        //String activity = jo.get("activity").getAsString().toLowerCase();
        System.out.println(activity);

        if (activity.equalsIgnoreCase("vote")) {
            encrypted = new File("/srvakf/KandidatServer/encrypted1.txt");
            decrypted = new File("/srvakf/KandidatServer/decrypted1.txt");
            encryptedAesKeyMix = new File("/srvakf/KandidatServer/encryptedAesKeyMix.txt");
            rsaPrivateKeyMix = new File("/srvakf/KandidatServer/privateSender.der");
            //String encryptedAesKey = jo.get("aeskey").toString();
            String encryptedAesKey = parts [3].replace("\"}","");
            System.out.println("AESKEY: "+encryptedAesKey);

            try{
                encryption = new FileEncryption();
            BufferedWriter writer = new BufferedWriter(new FileWriter(encryptedAesKeyMix, false /*append*/));
            writer.write(encryptedAesKey);
            writer.close();
                System.out.println("AESKEY: "+fileUtils.readFileToString(encryptedAesKeyMix));
            encryption.loadKey(encryptedAesKeyMix, rsaPrivateKeyMix);
                //String encryptedMessage = jo.get("message").toString();
                String encryptedMessage = parts [2];
                System.out.println(encryptedMessage);
                writer = new BufferedWriter(new FileWriter(encrypted, false /*append*/));
                writer.write(encryptedMessage);
                writer.close();
            encryption.decrypt(encrypted, decrypted);
                jsonMessage = encryption.getJsonString(decrypted).replace("RANDOM_SALT", "");
                receiverIP = votereceiver.getReceiverIP();
                receiverPort = votereceiver.getReceiverPort();
                System.out.println(jsonMessage);
                System.out.println(receiverIP);
                System.out.println(receiverPort);
                ClientSender clientSender =
                        new ClientSender(mClientInfo, jsonMessage, receiverIP, receiverPort);
                clientSender.start();



        } catch (IOException i){
            i.printStackTrace();
        } catch (GeneralSecurityException e){
                e.printStackTrace();
            }
        }

        if (activity.equalsIgnoreCase("receive")){
            receiverIP = socket.getInetAddress().getHostAddress();
            receiverPort = socket.getPort();
            System.out.println(receiverIP);
            System.out.println(receiverPort);
            votereceiver.setReceiverIP(receiverIP);
            votereceiver.setReceiverPort(receiverPort);
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

