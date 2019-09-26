package com.example.mymeetings;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MeetingListViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
   public TextView name_cv;
   public TextView sapid_cv;
    public MeetingListViewHolder(@NonNull View itemView) {
        super(itemView);
        name_cv=(TextView)itemView.findViewById(R.id.name_cv);
        sapid_cv=(TextView)itemView.findViewById(R.id.sapid_cv);
        cardView=(CardView)itemView.findViewById(R.id.info_card);
    }
}
