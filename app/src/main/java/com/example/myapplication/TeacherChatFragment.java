package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.adapter.ChatAvailableAdapter;
import com.example.myapplication.apis.ApiEndPoint;
import com.example.myapplication.model.ChatAvailable;
import com.example.myapplication.model.Message;
import com.example.myapplication.utils.SharedPreferenceClass;

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


public class TeacherChatFragment extends Fragment {
    private Socket mSocket;
    SharedPreferenceClass sharedPreferenceClass;
    RecyclerView recyclerView;
    List<ChatAvailable> chatAvailableList = new ArrayList<>();
    ChatAvailableAdapter chatAvailableAdapter;
    String teacherId;

    public static TeacherChatFragment newInstance() {
        TeacherChatFragment fragment = new TeacherChatFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_chat, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        chatAvailableAdapter = new ChatAvailableAdapter(getContext(), chatAvailableList);
        recyclerView = view.findViewById(R.id.chat_available);
        recyclerView.setAdapter(chatAvailableAdapter);
        teacherId = sharedPreferenceClass.getValue_string("id");
        chatAvailableAdapter.setOnItemClickListener(new ChatAvailableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatAvailable chatAvailable, int position) {
                Fragment fragment = TeacherMessageFragment.newInstance(chatAvailable);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        try {
            mSocket = IO.socket(ApiEndPoint.SOCKET_URL);
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.emit("roomAvailable", teacherId);
            mSocket.on("roomAvailable", roomAvailable);
            mSocket.on("reloadRoomAvailable", reloadRoomAvilable);
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        return view;
    }

    private Emitter.Listener onConnect = args -> getActivity().runOnUiThread(() -> {

        Log.d("connnect", ": success");
    });
    private Emitter.Listener reloadRoomAvilable = args -> getActivity().runOnUiThread(() -> {

        mSocket.emit("roomAvailable", teacherId);

    });
    private Emitter.Listener roomAvailable = args -> getActivity().runOnUiThread(() -> {
        JSONObject data = (JSONObject) args[0];
        try {
            JSONArray jsonArray = data.getJSONArray("rooms");
            List<ChatAvailable> dataList = new ArrayList<>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                ChatAvailable chatAvailable = new ChatAvailable();
                chatAvailable.setRoomId(item.getString("_id"));
                chatAvailable.setMessage(item.getString("lastMessage"));
                chatAvailable.setStudentName(item.getString("studentName"));
                chatAvailable.setStudentId(item.getString("studentId"));
                Date timestamp = formatter.parse(item.getString("timestamp"));
                chatAvailable.setCreatedAt(timestamp);
                dataList.add(chatAvailable);
            }
            chatAvailableList.clear();
            chatAvailableList.addAll(dataList);
            chatAvailableAdapter.notifyDataSetChanged();
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