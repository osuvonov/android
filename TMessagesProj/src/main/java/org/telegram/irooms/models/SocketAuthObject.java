package org.telegram.irooms.models;

import java.net.Socket;

public class SocketAuthObject {
    private String token = "";
    private String version = "";
    private String platform = "";

    public SocketAuthObject(String token, String pl, String ver){
        this.token=token;
        this.version=ver;
        this.platform=pl;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}