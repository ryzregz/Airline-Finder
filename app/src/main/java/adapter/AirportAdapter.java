package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Airport;
import search.flight.com.flightsearch.R;

/**
 * Created by mac on 2/26/18.
 */

public class AirportAdapter extends ArrayAdapter<Airport> {
    ArrayList<Airport> airports;

    public AirportAdapter(Context context, ArrayList<Airport> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.airports = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Airport airport = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.airport_item, parent, false);
        }
        TextView txtName = (TextView) convertView.findViewById(R.id.name);
        TextView txtCode = (TextView) convertView.findViewById(R.id.code);
        if (txtName != null)
            txtName.setText(airport.getName() );
        if (txtCode != null )
            txtCode.setText(airport.getCode());


        return convertView;
    }


}