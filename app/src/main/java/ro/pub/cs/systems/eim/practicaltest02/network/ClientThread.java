package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String clientKey;
    private String clientValue;
    private TextView resultTextView;
    private String method;

    private Socket socket;

    public ClientThread(String address, int port, String clientKey, String clientValue, String method, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.resultTextView = resultTextView;
        this.clientValue = clientValue;
        this.clientKey = clientKey;
        this.method = method;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println(clientKey);
            printWriter.flush();
            printWriter.println(clientValue);
            printWriter.flush();
            printWriter.println(method);
            printWriter.flush();

            String information;
            while ((information = bufferedReader.readLine()) != null) {
                System.out.println(information);
                final String finalizedInformation = information;
                resultTextView.post(new Runnable() {
                   @Override
                    public void run() {
                       resultTextView.setText(finalizedInformation);
                   }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
