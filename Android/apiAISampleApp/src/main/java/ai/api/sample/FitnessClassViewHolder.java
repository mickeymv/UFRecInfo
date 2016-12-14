package ai.api.sample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by rahulmaliakkal on 12/13/16.
 */
public class FitnessClassViewHolder  extends RecyclerView.ViewHolder{
    public TextView className;
    public TextView classDay;
    public TextView type;
    public TextView location;
    public TextView type2;
    public TextView beginTime;
    public TextView endTime;
    public TextView duration;
    public TextView instructor;
    public FitnessClassViewHolder(View itemView) {
        super(itemView);
        className = (TextView)itemView.findViewById(R.id.className);
        classDay = (TextView)itemView.findViewById(R.id.classDay);
        type = (TextView)itemView.findViewById(R.id.type);
        location = (TextView)itemView.findViewById(R.id.location);
        type2 = (TextView)itemView.findViewById(R.id.type2);
        beginTime =(TextView)itemView.findViewById(R.id.beginTime);
        endTime = (TextView)itemView.findViewById(R.id.endTime);
        duration = (TextView)itemView.findViewById(R.id.duration);
        instructor = (TextView)itemView.findViewById(R.id.instructor);
    }
}
