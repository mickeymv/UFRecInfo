package ai.api.sample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mickey on 12/8/16.
 */

public class FilterAdapter extends BaseAdapter {
    private Activity context;
    private ArrayList<Filter> filters;

    public FilterAdapter(Context context,
                               ArrayList<Filter> filters) {
        // TODO Auto-generated constructor stub
        this.context = (Activity) context;
        this.filters = filters;
    }

    @Override
    public int getCount() {
        return filters.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return filters.get(position); //returns list item at the specified position
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
                    inflate(R.layout.filter_item_layout, parent, false);
        }

        // get current item to be displayed
        Filter filter = (Filter) getItem(position);

        // get the TextView for filtervalue
        TextView filterValue = (TextView)
                convertView.findViewById(R.id.filterValue);

        //sets the text for filtervalue from the current item object
        filterValue.setText(filter.getValue());
        // returns the view for the current row
        return convertView;
    }
}
