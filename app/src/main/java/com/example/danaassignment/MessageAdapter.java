package com.example.danaassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private String userID;

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (message.userID.equals(userID)) {
            holder.messageCard.setVisibility(View.GONE);
            holder.messageCard_m.setVisibility(View.VISIBLE);
            holder.message_m.setText(message.message);
            holder.userName_m.setText(message.userName);
            Glide.with(holder.userImage_m.getContext()).load(message.userPhoto).into(holder.userImage_m);
        } else {
            holder.messageCard_m.setVisibility(View.GONE);
            holder.messageCard.setVisibility(View.VISIBLE);
            holder.message.setText(message.message);
            holder.userName.setText(message.userName);
            Glide.with(holder.userImage.getContext()).load(message.userPhoto).into(holder.userImage);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public MessageAdapter(String userID) {
        this.userID = userID;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            messages = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    ChatMessage m = new ChatMessage(
                                            document.get("userPhoto").toString(),
                                            document.get("userName").toString(),
                                            document.get("userId").toString(),
                                            document.get("message").toString()
                                    );
                                    messages.add(m);
                                } catch (Exception e) {
                                }
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
        db.collection("Chat").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                messages = new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    try {
                        ChatMessage m = new ChatMessage(
                                document.get("userPhoto").toString(),
                                document.get("userName").toString(),
                                document.get("userId").toString(),
                                document.get("message").toString()
                        );
                        messages.add(m);
                    } catch (Exception e) {
                    }
                }
                notifyDataSetChanged();
            }
        });
    }
    public void addMessage(ChatMessage m) {
        Map<String, Object> message = new HashMap<>();
        message.put("userPhoto", m.userPhoto);
        message.put("userName", m.userName);
        message.put("userId", m.userID);
        message.put("message",m.message);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chat").document(String.valueOf(System.currentTimeMillis()))
                .set(message);
    }
}
