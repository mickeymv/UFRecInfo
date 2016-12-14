package ai.api.sample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by rahulmaliakkal on 12/13/16.
 */
public class FitnessClassViewHolder  extends RecyclerView.ViewHolder{
    public TextView className;
    public TextView classDay;
    public TextView type;
    public TextView location;
    public FitnessClassViewHolder(View itemView) {
        super(itemView);
        className = (TextView)itemView.findViewById(R.id.className);
        classDay = (TextView)itemView.findViewById(R.id.classDay);
        type = (TextView)itemView.findViewById(R.id.type);
        location = (TextView)itemView.findViewById(R.id.location);
    }
}
