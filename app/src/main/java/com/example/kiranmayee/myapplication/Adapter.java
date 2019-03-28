package com.example.kiranmayee.myapplication;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kiranmayee.myapplication.R;
//import com.androidtutorialshub.loginregister.model.User;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.UserViewHolder> {

    private List<Maps> listMaps;

    public Adapter(List<Maps> listMaps) {
        this.listMaps = listMaps;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_recycler, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.textViewEmail.setText(String.valueOf(listMaps.get(position).getLatitude()));
        holder.textViewPassword.setText(String.valueOf(listMaps.get(position).getLongitude()));
    }

    @Override
    public int getItemCount() {
        Log.v(Adapter.class.getSimpleName(),""+listMaps.size());
        return listMaps.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView textViewEmail;
        public AppCompatTextView textViewPassword;

        public UserViewHolder(View view) {
            super(view);
            textViewEmail = (AppCompatTextView) view.findViewById(R.id.textViewEmail);
            textViewPassword = (AppCompatTextView) view.findViewById(R.id.textViewPassword);
        }
    }


}
