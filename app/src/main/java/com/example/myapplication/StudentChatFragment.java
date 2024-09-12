package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.adapter.MessageAdapter;
import com.example.myapplication.model.Message;
import com.example.myapplication.utils.SharedPreferenceClass;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class StudentChatFragment extends Fragment {
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.1.7:8000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    RecyclerView recyclerView;
    ImageView btnSend;
    TextInputEditText textInputEditText;
    SharedPreferenceClass sharedPreferenceClass;
    MessageAdapter messageAdapter;
    List<Message> messageList = new ArrayList<>();
    boolean firstMessage = true;
    private String roomId;
    private String teacherId = null;
    public static StudentChatFragment newInstance() {
        StudentChatFragment fragment = new StudentChatFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_chat, container, false);
        btnSend = view.findViewById(R.id.btn_send);
        textInputEditText = view.findViewById(R.id.chat_message);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        recyclerView = view.findViewById(R.id.message_recycle_view);
        messageAdapter = new MessageAdapter(getContext(), messageList, sharedPreferenceClass.getValue_string("id"));
        recyclerView.setAdapter(messageAdapter);
        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("newMessage", onNewMessage);
        mSocket.on("messageSent", onMessageSent);
        mSocket.on("chatCreated", chatCreated);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = String.valueOf(textInputEditText.getText());
                if (TextUtils.isEmpty(message)) return;
                String studentId = sharedPreferenceClass.getValue_string("id");
                if (firstMessage) {
                    firstMessage = false;
                    mSocket.emit("newChat", studentId, message);
                } else {
                    mSocket.emit("newMessage", message, roomId, studentId, teacherId);
                }
                textInputEditText.getText().clear();
            }
        });
        return view;
    }

    private Emitter.Listener onConnect = args -> getActivity().runOnUiThread(() -> {
        Log.d("connnect", ": success");
    });
    private Emitter.Listener chatCreated = args -> getActivity().runOnUiThread(() -> {
        if (args.length > 0 && args[0] instanceof JSONObject) {
            try {
                JSONObject data = (JSONObject) args[0];
                this.roomId = data.getString("roomId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });
    private Emitter.Listener onNewMessage = args -> getActivity().runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        try {
            JSONArray jsonArray = data.getJSONArray("messages");
            List<Message> dataList = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Message message = new Message();
                message.set_id(item.getString("_id"));
                message.setStudentId(item.getString("studentId"));
                message.setRoomId(item.getString("roomId"));
                message.setMessage(item.getString("message"));
                message.setTeacherId(item.getString("teacherId"));
                Date timestamp = formatter.parse(item.getString("timestamp"));
                message.setTimestamp(timestamp);
                dataList.add(message);
            }
            messageList.clear();
            messageList.addAll(dataList);
            messageAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    });
    private Emitter.Listener onMessageSent = args -> getActivity().runOnUiThread(() -> {

    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}