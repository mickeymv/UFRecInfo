package ai.api.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mickey on 11/29/16.
 */

public class FitnessClassAdapter extends RecyclerView.Adapter<FitnessClassViewHolder> {
    private ArrayList<FitnessClass> list;

    public FitnessClassAdapter(ArrayList<FitnessClass> Data) {
        list = Data;
    }
    @Override
    public FitnessClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fitness_class_list_recycle_items, parent, false);
        FitnessClassViewHolder holder = new FitnessClassViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FitnessClassViewHolder holder, int position) {
        holder.className.setText(list.get(position).getName());
        holder.classDay.setText(list.get(position).getDay());
        holder.type.setText(list.get(position).getType());
        holder.location.setText(list.get(position).getVenue());
        holder.type2.setText(list.get(position).getType_2());
        holder.beginTime.setText(list.get(position).getBegin_Time());
        holder.endTime.setText(list.get(position).getEnd_Time());
        holder.duration.setText(list.get(position).getDuration());
        holder.instructor.setText(list.get(position).getInstructor());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
