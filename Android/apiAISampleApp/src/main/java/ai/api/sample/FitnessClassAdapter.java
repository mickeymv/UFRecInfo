package ai.api.sample;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mickey on 11/29/16.
 */

public class FitnessClassAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<FitnessClass> fitnessClasses;

    public FitnessClassAdapter(Context context,
                               ArrayList<FitnessClass> fitnessClasses) {
        // TODO Auto-generated constructor stub
        this.context = (Activity) context;
        this.fitnessClasses = fitnessClasses;
    }

    @Override
    public int getCount() {
        return fitnessClasses.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return fitnessClasses.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.list_item_layout, parent, false);
        }

        // get current item to be displayed
        FitnessClass currentClass = (FitnessClass) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.className);
        TextView textViewItemDay = (TextView)
                convertView.findViewById(R.id.classDay);

        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentClass.getName());
        textViewItemDay.setText(currentClass.getDay());

        // returns the view for the current row
        return convertView;
    }
}
