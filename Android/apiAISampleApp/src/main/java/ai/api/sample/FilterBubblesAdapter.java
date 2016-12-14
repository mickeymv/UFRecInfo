package ai.api.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by rahulmaliakkal on 12/13/16.
 */

public class FilterBubblesAdapter extends RecyclerView.Adapter<FilterBubblesViewHolder> {
    private ArrayList<Filter> list;

    public FilterBubblesAdapter(ArrayList<Filter> Data) {
        list = Data;
    }
    @Override
    public FilterBubblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_bubbles_recycle_items, parent, false);
        FilterBubblesViewHolder holder = new FilterBubblesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FilterBubblesViewHolder holder, int position) {
        holder.filterKey.setText(list.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
