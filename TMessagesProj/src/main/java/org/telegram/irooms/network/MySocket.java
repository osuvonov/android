package org.telegram.irooms.network;

import org.telegram.irooms.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;

public class MySocket {
    private Socket mSocket;
    private static MySocket instance;

    private MySocket() {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            SocketSSL.set(options);
            mSocket = IO.socket(Constants.SOCKET_ENDPOINT, options);
        } catch (URISyntaxException e) {
        }
    }

    public Socket getSocket(){
        return mSocket;
    }

    public static MySocket getInstance(){
        if (instance==null){
            instance=new MySocket();
        }
        return instance;
    }

    private boolean isAuthorized(){
        return false;
    }
}
