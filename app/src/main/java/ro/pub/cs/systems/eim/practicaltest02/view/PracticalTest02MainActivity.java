package ro.pub.cs.systems.eim.practicaltest02.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private TextView resultTextView = null;
    private EditText clientKeyEditText = null;
    private EditText clientValueEditText = null;
    private Button putButton = null;
    private Button getButton = null;

    // TODO: add information for the server

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private GetInformationButtonClickListener getInformationButtonClickListener = new GetInformationButtonClickListener();
    private class GetInformationButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientKey = clientKeyEditText.getText().toString();
            if (clientKey == null || clientKey.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client information parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), clientKey, "", "GET", resultTextView
            );
            clientThread.start();
        }
    }

    private PutInformationButtonClickListener putInformationButtonClickListener = new PutInformationButtonClickListener();
    private class PutInformationButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String clientKey = clientKeyEditText.getText().toString();
            String clientValue = clientValueEditText.getText().toString();
            if (clientKey == null || clientKey.isEmpty() || clientValue == null || clientValue.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client information parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), clientKey, clientValue, "POST", resultTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        resultTextView = (TextView)findViewById(R.id.result_text_view);
        clientKeyEditText = (EditText) findViewById(R.id.client_key_edit_text);
        clientValueEditText = (EditText) findViewById(R.id.client_value_edit_text);

        getButton = (Button) findViewById(R.id.get_button);
        getButton.setOnClickListener(getInformationButtonClickListener);

        putButton = (Button) findViewById(R.id.put_button);
        putButton.setOnClickListener(putInformationButtonClickListener);

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

}
