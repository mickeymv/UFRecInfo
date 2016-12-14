package ai.api.sample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by rahulmaliakkal on 12/13/16.
 */
public class FilterBubblesViewHolder extends RecyclerView.ViewHolder{
    public TextView filterKey;
    public FilterBubblesViewHolder(View itemView) {
        super(itemView);
        filterKey = (TextView)itemView.findViewById(R.id.filterKey);
    }
}
