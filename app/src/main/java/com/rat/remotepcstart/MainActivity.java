package com.rat.remotepcstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rat.remotepcstart.R;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    public static final int PORT = 9;
    public static String ip = "192.168.1.80";
    //    public static String ip = "192.168.1.255";
    public static String macAddress = "A85E4557CE2D";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.buttonWol);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeup(ip, macAddress);
            }
        });
    }

    private static byte[] getMacBytes(String mac) throws IllegalArgumentException {
        Log.d("GetMacBytes", "method started");

        byte[] bytes = new byte[6];
        try {
            String hex;
            for (int i = 0; i < 6; i++) {
                hex = mac.substring(i * 2, i * 2 + 2);
                bytes[i] = (byte) Integer.parseInt(hex, 16);
                Log.d("GetMacbytes", "calculated");
                Log.d("GetMacBytes (bytes)", new String(bytes));
            }
        } catch (NumberFormatException e) {
            Log.e("GetMacBytes", "error");
        }
        return bytes;
    }

    public static void wakeup(String broadcastIP, String mac) {
        Log.d("wakeup", "method started");
        if (mac == null) {
            Log.d("Mac error at wakeup", "mac = null");
            return;
        }

        try {
            byte[] macBytes = getMacBytes(mac);
            Log.d("wakeup (bytes)", new String(macBytes));
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            Log.d("wakeup", "calculating completed, sending...");
            InetAddress address = InetAddress.getByName(broadcastIP);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            Log.d("wakeup", "Magic Packet sent");


        } catch (Exception e) {
            Log.e("wakeup", "error");
        }

    }

}