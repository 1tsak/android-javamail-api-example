package com.a2v10.javamailapiexample;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Message;
import javax.mail.MessagingException;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<MailModel> messages;

    public RecyclerAdapter(Context context, ArrayList<MailModel> messages) {
        this.context = context;
        this.messages = messages;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String subject = messages.get(position).getSubject().toString(), body = messages.get(position).getContent().toString();
        holder.title.setText(subject);
        holder.body.setText(body);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgv;
        TextView title, body;
        ConstraintLayout mail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgv = itemView.findViewById(R.id.imgIdentifier);
            title = itemView.findViewById(R.id.titleTxt);
            body = itemView.findViewById(R.id.bodyTxt);
            mail = itemView.findViewById(R.id.mail_cont);


        }
    }
}
