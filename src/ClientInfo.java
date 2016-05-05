/**
 * Created by KarlFredrik on 2016-04-13.
 */
import java.net.Socket;
import java.util.UUID;

public class ClientInfo {
    public Socket mSocket = null;
    public ClientListener mClientListener = null;
    public ClientSender mClientSender = null;
    public UUID id;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}