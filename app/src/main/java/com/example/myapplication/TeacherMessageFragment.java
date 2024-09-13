package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.myapplication.adapter.MessageAdapter;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.model.ChatAvailable;
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


public class TeacherMessageFragment extends Fragment {
    private static final String ARG_CHAT_INFO = "chat_info";
    private Socket mSocket;
    SharedPreferenceClass sharedPreferenceClass;
    RecyclerView recyclerView;
    LinearLayout btnBack;
    ImageView btnSend;
    TextInputEditText textInputEditText;
    MessageAdapter messageAdapter;
    List<Message> messageList = new ArrayList<>();

    public static TeacherMessageFragment newInstance(ChatAvailable chatAvailable) {
        TeacherMessageFragment fragment = new TeacherMessageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHAT_INFO, chatAvailable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_message, container, false);
        btnSend = view.findViewById(R.id.btn_send);
        btnBack = view.findViewById(R.id.btn_back);
        textInputEditText = view.findViewById(R.id.chat_message);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        recyclerView = view.findViewById(R.id.message_recycle_view);
        String teacherId = sharedPreferenceClass.getValue_string("id");
        String role = sharedPreferenceClass.getValue_string("role");
        ChatAvailable chatAvailable = (ChatAvailable) getArguments().getSerializable(ARG_CHAT_INFO);
        messageAdapter = new MessageAdapter(getContext(), messageList, teacherId, role);
        recyclerView.setAdapter(messageAdapter);
        try {
            mSocket = IO.socket(ApiEndPoint.SOCKET_URL);
            mSocket.connect();
            mSocket.on(io.socket.client.Socket.EVENT_CONNECT, onConnect);
            mSocket.on("newMessage", onNewMessage);
            mSocket.on("messageSent", onMessageSent);
            mSocket.on("allMessageChat", onAllMessageChat);
            mSocket.emit("teacherJoinRoom", teacherId, chatAvailable.getRoomId());
            mSocket.emit("allMessageChat", chatAvailable.getRoomId());
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = String.valueOf(textInputEditText.getText()).trim();
                if (TextUtils.isEmpty(message)) return;
                mSocket.emit("newMessage", message, chatAvailable.getStudentId(), teacherId, chatAvailable.getRoomId(), teacherId);
                textInputEditText.getText().clear();
            }
        });
        return view;
    }

    private Emitter.Listener onConnect = args -> getActivity().runOnUiThread(() -> {

        Log.d("connnect", ": success");
    });
    private Emitter.Listener onMessageSent = args -> getActivity().runOnUiThread(() -> {

    });
    private Emitter.Listener onAllMessageChat = args -> getActivity().runOnUiThread(() -> {
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
                message.setSendId(item.getString("sendId"));
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
                message.setSendId(item.getString("sendId"));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}