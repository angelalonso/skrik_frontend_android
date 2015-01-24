package com.afonseca.skrik;



import java.util.concurrent.ExecutionException;

/**
 * Created by afonseca on 1/24/2015.
 */
public class Control_BackendHandler {
    String URL = "http://192.168.10.229:8000";

    public String getnewID() {
        String output = null;

        String url_getnewid = URL + "/getnewid/";
        try {
            output = new Control_StringAsyncTask().execute(url_getnewid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return output;
    }
}
