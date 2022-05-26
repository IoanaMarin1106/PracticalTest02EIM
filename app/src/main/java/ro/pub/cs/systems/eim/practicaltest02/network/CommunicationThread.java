package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.webkit.HttpAuthHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.WeatherForecastInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    private String unixTimePut;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            String clientKey = bufferedReader.readLine();
            String clientValue = bufferedReader.readLine();
            String method = bufferedReader.readLine();

            HashMap<String, String> data = serverThread.getData();

            if (method.equals("GET")) {
                String pageSourceCode = "";
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                JSONObject content = null;
                try {
                    content = new JSONObject(pageSourceCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    // Daca timpul e mai mare de un minut, se sterge cheia
                    String unixTime = content.getString("unixtime");
                    if (Long.parseLong(unixTime) - Long.parseLong(this.unixTimePut) >= 60) {
                        serverThread.getData().remove(clientKey);
                    }

                    // Timpul la care s-a facut PUT
                    System.out.println(unixTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(Constants.TAG, "[COMMUNICATION THREAD] GET value for key " + clientKey);
                if (data.containsKey(clientKey)) {
                    printWriter.println("Value is: " + data.get(clientKey));
                    printWriter.flush();
                } else {
                    printWriter.println("Key does not exists.");
                    printWriter.flush();
                }

            } else if (method.equals("POST")) {
                String pageSourceCode = "";
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                JSONObject content = null;
                try {
                    content = new JSONObject(pageSourceCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String unixTime = content.getString("unixtime");
                    this.unixTimePut = unixTime;

                    // Timpul la care s-a facut PUT
                    System.out.println(unixTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(Constants.TAG, "[COMMUNICATION THREAD] PUT value in local cache: key " + clientKey + " value " + clientValue);
                serverThread.setData(clientKey, clientValue);
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
