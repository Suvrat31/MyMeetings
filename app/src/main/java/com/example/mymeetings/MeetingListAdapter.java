package com.example.mymeetings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListViewHolder> {
    ArrayList<MeetingListModel> meetingListModelList;
    Context context;

    public MeetingListAdapter(ArrayList<MeetingListModel> list,Context context){
        this.context=context;
        this.meetingListModelList=list;
    }
    @NonNull
    @Override
    public MeetingListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card,viewGroup,false);
        return new MeetingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingListViewHolder holder, int position) {
            holder.name_cv.setText(meetingListModelList.get(position).getName());
            holder.sapid_cv.setText(meetingListModelList.get(position).getSapid());

    }

    @Override
    public int getItemCount() {
        int arr = 0;

        try{
            if(meetingListModelList.size()==0){

                arr = 0;

            }
            else{

                arr=meetingListModelList.size();
            }



        }catch (Exception e){



        }

        return arr;

    }
}
