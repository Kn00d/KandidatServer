/**
 * Created by KarlFredrik on 2016-04-13.
 */
import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.JsonArray;

public class ClientSender extends Thread
{
    private LinkedList<String> messageQueue = new LinkedList<String>();
    private ServerDispatcher mServerDispatcher;
    private ClientInfo mClientInfo;
    private PrintWriter mOut;
    private String message;
    int receiverPort;
    String receiverIP;
    Socket socket;
    //
    // private Database db;
    //

    public ClientSender(ClientInfo aClientInfo, String message, String mreceiverIP, int mreceiverPort)
            throws IOException
    {
        receiverIP = mreceiverIP;
        receiverPort = mreceiverPort;
        mClientInfo = aClientInfo;
        this.message=message;
        socket = new Socket (receiverIP, receiverPort);
        //mServerDispatcher = aServerDispatcher;

        mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Adds given message to the message queue and notifies this thread
     * (actually getNextMessageFromQueue method) that a message is arrived.
     * sendMessage is called by other threads (ServeDispatcher).
     */
    public synchronized void sendMessage(String aMessage)
    {
        //messageQueue.add(aMessage);
        //notify();
    }

    /**
     * @return and deletes the next message from the message queue. If the queue
     * is empty, falls in sleep until notified for message arrival by sendMessage
     * method.
     */

    private synchronized String getNextMessageFromQueue() throws InterruptedException
    {
        while(messageQueue.size()==0){
            wait();
        }
        String message = messageQueue.get(0);
        messageQueue.remove(message);
        return message;
    }

    /**
     * Sends given message to the client's socket.
     */
    private void sendMessageToClient(String aMessage)
    {
        mOut.println(aMessage);
        mOut.flush();
        message = "";
    }

    /**
     * Until interrupted, reads messages from the message queue
     * and sends them to the client's socket.
     */
    public void run()
    {
        //db = new Database();
        //JsonArray jsonArray;
        try {
            //while (!isInterrupted()) {
            //String message = getNextMessageFromQueue();
            sendMessageToClient(message);
        } catch (Exception e) {
            // Communication problem
        }
        // Communication is broken. Interrupt both listener and sender threads
        //mClientInfo.mClientListener.interrupt();
        // mServerDispatcher.deleteClient(mClientInfo);
    }

    void sendJSON(JsonArray jsonArray) {
        String message = jsonArray.toString();
        sendMessageToClient(message);

    }

}