package ui.FindFriendsScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shinydisco.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import data.Disco;
import data.User;
import ui.FindDiscos.DiscoSearchAdapter;
import ui.FindDiscos.ViewDiscoOnMapFragment;
import utils.Firebase;
import utils.MultiSpinner;

public class FindFriendsFragment extends Fragment {
//    public static final int REQUEST_ENABLE_BT = 1;
//    ListView lv_paired_devices;
//    Set<BluetoothDevice> set_pairedDevices;
//    ArrayAdapter adapter_paired_devices;
//    BluetoothAdapter bluetoothAdapter;
//    public static final UUID MY_UUID = UUID.randomUUID();
//    public static final int MESSAGE_READ = 0;
//    public static final int MESSAGE_WRITE = 1;
//    public static final int CONNECTING = 2;
//    public static final int CONNECTED = 3;
//    public static final int NO_SOCKET_FOUND = 4;
    private ListView lv_friend_search_result;

    String uid = Firebase.getFirebaseAuth().getCurrentUser().getUid();

    public FindFriendsFragment() {

    }

    public static FindFriendsFragment newInstance() {
        FindFriendsFragment fragment = new FindFriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        initialize_layout(rootView);
//        initialize_bluetooth();
//        start_accepting_connection();
//        initialize_clicks();
        View rootView = inflater.inflate(R.layout.fragment_find_friends, container, false);

        Button btn = rootView.findViewById(R.id.friend_search_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                EditText searchFriendTextBox = (EditText) rootView.findViewById(R.id.friend_search_box);

//                Firebase.getDbRef().child(Firebase.DB_USERS).child(uid).get().addOnSuccessListener(snapshot -> {
//                    User currentUser = snapshot.getValue(User.class);
//                });

                Firebase.getDbRef().child(Firebase.DB_USERS).get().addOnSuccessListener(snapshot -> {
                    ArrayList<User> users = new ArrayList<>();
                    User currentUser = new User();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (!user.getUid().equals(uid))
                            users.add(user);
                        else
                            currentUser = user;
                    }

                    if (!(searchFriendTextBox.getText().toString().length() == 0)) {
                        users.removeIf(d -> !d.getName().toLowerCase().contains(searchFriendTextBox.getText().toString().toLowerCase()));
                    }

                    User finalCurrentUser = currentUser;
                    users.removeIf(u -> finalCurrentUser.getFriends().containsKey(u.getUid()));

                    lv_friend_search_result = (ListView) rootView.findViewById(R.id.lv_friend_search_result);
                    lv_friend_search_result.setAdapter(new FriendSearchAdapter(getActivity().getApplicationContext(), users));

                    lv_friend_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            User addedUser = (User)parent.getAdapter().getItem(position);

                            if (finalCurrentUser.getFriends() == null) {
                                finalCurrentUser.setFriends(new HashMap<>());
                                finalCurrentUser.getFriends().put(addedUser.getUid(), true);
                                Firebase.getDbRef().child(Firebase.DB_USERS).child(finalCurrentUser.getUid()).setValue(finalCurrentUser);
                            }
                            else if (!finalCurrentUser.getFriends().containsKey(addedUser.getUid())) {
                                finalCurrentUser.getFriends().put(addedUser.getUid(), true);
                                Firebase.getDbRef().child(Firebase.DB_USERS).child(finalCurrentUser.getUid()).setValue(finalCurrentUser);
                            }

                            if (addedUser.getFriends() == null) {
                                addedUser.setFriends(new HashMap<>());
                                addedUser.getFriends().put(finalCurrentUser.getUid(), true);
                                Firebase.getDbRef().child(Firebase.DB_USERS).child(addedUser.getUid()).setValue(addedUser);
                            }
                            else if (!addedUser.getFriends().containsKey(finalCurrentUser.getUid())) {
                                addedUser.getFriends().put(finalCurrentUser.getUid(), true);
                                Firebase.getDbRef().child(Firebase.DB_USERS).child(addedUser.getUid()).setValue(addedUser);
                            }

                            Toast.makeText(getActivity(), "Friend added successfully", Toast.LENGTH_SHORT).show();
                            users.remove(position);
                            lv_friend_search_result.setAdapter(new FriendSearchAdapter(getActivity().getApplicationContext(), users));
                        }
                    });

                });
            }
        });
        return rootView;
    }

//    @SuppressLint("HandlerLeak")
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg_type) {
//            super.handleMessage(msg_type);
//
//            switch (msg_type.what) {
//                case MESSAGE_READ:
//
//                    byte[] readbuf = (byte[]) msg_type.obj;
//                    String string_received = new String(readbuf);
//                    String currentUserUid = Firebase.getFirebaseAuth().getCurrentUser().getUid();
//
//                    Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserUid).get().addOnSuccessListener(snapshot -> {
//                       User currentUser = snapshot.getValue(User.class);
//                       if (!currentUser.getFriends().containsKey(string_received)) {
//                           currentUser.getFriends().put(string_received, true);
//                           Firebase.getDbRef().child(Firebase.DB_USERS).child(currentUserUid).setValue(currentUser);
//                       }
//                    });
//
//                    Firebase.getDbRef().child(Firebase.DB_USERS).child(string_received).get().addOnSuccessListener(snapshot -> {
//                       User receivedUser = snapshot.getValue(User.class);
//                        if (!receivedUser.getFriends().containsKey(currentUserUid)) {
//                            receivedUser.getFriends().put(currentUserUid, true);
//                            Firebase.getDbRef().child(Firebase.DB_USERS).child(string_received).setValue(receivedUser);
//                        }
//
//                    });
//
//                    Toast.makeText(getActivity().getApplicationContext(), string_received, Toast.LENGTH_LONG).show();
//
//                    break;
//                case MESSAGE_WRITE:
//
//                    Toast.makeText(getActivity().getApplicationContext(), "message write", Toast.LENGTH_SHORT).show();
//                    if (msg_type.obj != null) {
//                        ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket) msg_type.obj);
//                        connectedThread.write(bluetooth_message.getBytes());
//                    }
//                    break;
//
//                case CONNECTED:
//                    Toast.makeText(getActivity().getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
//                    break;
//
//                case CONNECTING:
//                    Toast.makeText(getActivity().getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();
//                    break;
//
//                case NO_SOCKET_FOUND:
//                    Toast.makeText(getActivity().getApplicationContext(), "No socket found", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
//
//    public void start_accepting_connection() {
//        AcceptThread acceptThread = new AcceptThread();
//        acceptThread.start();
//    }
//
//    public void initialize_clicks() {
//        lv_paired_devices.setOnItemClickListener((parent, view, position, id) -> {
//            Object[] objects = set_pairedDevices.toArray();
//            BluetoothDevice device = (BluetoothDevice) objects[position];
//
//            ConnectThread connectThread = new ConnectThread(device);
//            connectThread.start();
//
//            Toast.makeText(getActivity().getApplicationContext(), "Chosen Device" + device.getName(), Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    public void initialize_layout(View rootView) {
//        lv_paired_devices = (ListView) rootView.findViewById(R.id.lv_paired_devices);
//        adapter_paired_devices = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.bluetooth_connection_layout);
//        lv_paired_devices.setAdapter(adapter_paired_devices);
//    }
//
//    public void initialize_bluetooth() {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(getActivity().getApplicationContext(), "Your Device doesn't support bluetooth. Upgrade your phone :D", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } else {
//            set_pairedDevices = bluetoothAdapter.getBondedDevices();
//
//            if (set_pairedDevices.size() > 0) {
//
//                for (BluetoothDevice device : set_pairedDevices) {
//                    String deviceName = device.getName();
//                    String deviceHardwareAddress = device.getAddress();
//
//                    adapter_paired_devices.add(device.getName() + "\n" + device.getAddress());
//                }
//            }
//        }
//    }
//
//    public class AcceptThread extends Thread {
//        private final BluetoothServerSocket serverSocket;
//
//        public AcceptThread() {
//            BluetoothServerSocket tmp = null;
//            try {
//                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NAME", MY_UUID);
//            } catch (IOException e) {
//            }
//            serverSocket = tmp;
//        }
//
//        public void run() {
//            BluetoothSocket socket = null;
//            while (true) {
//                try {
//                    socket = serverSocket.accept();
//                } catch (IOException e) {
//                    break;
//                }
//
//                if (socket != null) {
//                    // Do work to manage the connection (in a separate thread)
//                    mHandler.obtainMessage(CONNECTED).sendToTarget();
//                }
//            }
//        }
//    }
//
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//
//        public ConnectThread(BluetoothDevice device) {
//            // Use a temporary object that is later assigned to mmSocket,
//            // because mmSocket is final
//            BluetoothSocket tmp = null;
//            mmDevice = device;
//
//            // Get a BluetoothSocket to connect with the given BluetoothDevice
//            try {
//                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) {
//            }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            // Cancel discovery because it will slow down the connection
//            bluetoothAdapter.cancelDiscovery();
//
//            try {
//                // Connect the device through the socket. This will block
//                // until it succeeds or throws an exception
//                mHandler.obtainMessage(CONNECTING).sendToTarget();
//
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                // Unable to connect; close the socket and get out
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) {
//                }
//                return;
//            }
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//            }
//        }
//    }
//
//    private class ConnectedThread extends Thread {
//
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) {
//            }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//
//                } catch (IOException e) {
//                    break;
//                }
//            }
//        }
//
//        /* Call this from the main activity to send data to the remote device */
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) {
//            }
//        }
//
//        /* Call this from the main activity to shutdown the connection */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//            }
//        }
//    }
}