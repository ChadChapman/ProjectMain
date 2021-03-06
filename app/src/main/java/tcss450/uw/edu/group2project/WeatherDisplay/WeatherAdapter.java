package tcss450.uw.edu.group2project.WeatherDisplay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import tcss450.uw.edu.group2project.R;

import java.util.ArrayList;

public class WeatherAdapter extends ArrayAdapter<Weather> {

    public WeatherAdapter(@NonNull Context context, ArrayList<Weather> weatherArrayList){
        super(context, 0, weatherArrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Weather weather = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        }
        TextView dateTextView = convertView.findViewById(R.id.tvDate);
        TextView minTextView = convertView.findViewById(R.id.tvLowTemperature);
        TextView maxTextView = convertView.findViewById(R.id.tvHighTemperature);


        //dateTextView.setText(weather.getDate());
        minTextView.setText(weather.getMinTemp());

        maxTextView.setText(weather.getMaxTemp());



        return convertView;


    }
}
