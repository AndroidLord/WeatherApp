package com.example.weatherapp;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class weatherRVAdaptor extends RecyclerView.Adapter<weatherRVAdaptor.viewHolder> {

    private final ArrayList<weatherRVModel> list;
    private final Context context;

    public weatherRVAdaptor(ArrayList<weatherRVModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public weatherRVAdaptor.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull weatherRVAdaptor.viewHolder holder, int position) {

        weatherRVModel rvModel = list.get(position);

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        Date date = null;
        try {
            date = input.parse(rvModel.getTime());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert date != null;
        holder.timeTV.setText(output.format(date));

        holder.temperatureTV.setText(rvModel.getTemperature()+"°c");
        holder.windSpeedTV.setText(rvModel.getWindSpeed()+"Km/h");

        Picasso.get().load("https:".concat(rvModel.getIcon())).into(holder.weatherConditionIV);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView temperatureTV,windSpeedTV,timeTV;
        ImageView weatherConditionIV;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.Time_TV_Item);
            temperatureTV = itemView.findViewById(R.id.Temperature_TV_Item);
            windSpeedTV = itemView.findViewById(R.id.windSpeed_TV_Item);
            weatherConditionIV = itemView.findViewById(R.id.weatherCondition_IV_Item);

         }

    }
}
