package org.telegram.irooms.network;

public interface VolleyCallback {

    void onSuccess(String response);

    void onError(String error);
}
