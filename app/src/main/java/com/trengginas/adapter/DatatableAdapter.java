package com.trengginas.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trengginas.R;
import com.trengginas.activity.data.DataGeografisActivity;
import com.trengginas.activity.data.DataPemerintahanActivity;
import com.trengginas.activity.data.DataPertanianActivity;
import com.trengginas.activity.data.DataKependudukanActivity;
import com.trengginas.activity.data.DataKetenagakerjaanActivity;
import com.trengginas.activity.data.DataKemiskinanActivity;
import com.trengginas.activity.data.DataIpmActivity;
import com.trengginas.activity.data.DataEkonomiActivity;
import com.trengginas.activity.data.DataPariwisataActivity;
import com.trengginas.model.Datatable;

import java.util.List;

public class DatatableAdapter extends RecyclerView.Adapter<DatatableAdapter.DatatableViewHolder> {
    private List<Datatable> datatableList;
    private Context context;

    public DatatableAdapter(Context context, List<Datatable> datatableList) {
        this.context = context;
        this.datatableList = datatableList;
    }

    @Override
    public DatatableAdapter.DatatableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new DatatableAdapter.DatatableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DatatableAdapter.DatatableViewHolder holder, int position) {
        Datatable datatable = datatableList.get(position);
        holder.titleTextView.setText(datatable.getJudul());
        holder.subtitleTextView.setText(datatable.getSubjudul());

        // Load the image from the URL and update the placeholder URL
        String imageUrl = datatable.getIcon().replace("jokosanbps.com", "bpstrenggalek.com");
        Glide.with(context).load(imageUrl).into(holder.iconImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nmscreen = datatable.getNmscreen();
                if (nmscreen == null) {
                    Toast.makeText(context, "Screen name is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent;

                switch (nmscreen) {
                    case "Geografis":
                        intent = new Intent(context, DataGeografisActivity.class);
                        break;
                    case "Pemerintahan":
                        intent = new Intent(context, DataPemerintahanActivity.class);
                        break;
                    case "pertanian":
                        intent = new Intent(context, DataPertanianActivity.class);
                        break;
                    case "Penduduk":
                        intent = new Intent(context, DataKependudukanActivity.class);
                        break;
                    case "tk":
                        intent = new Intent(context, DataKetenagakerjaanActivity.class);
                        break;
                    case "kemiskinan":
                        intent = new Intent(context, DataKemiskinanActivity.class);
                        break;
                    case "ipm":
                        intent = new Intent(context, DataIpmActivity.class);
                        break;
                    case "pdrb":
                        intent = new Intent(context, DataEkonomiActivity.class);
                        break;
                    case "wisata":
                        intent = new Intent(context, DataPariwisataActivity.class);
                        break;
                    // Tambahkan kasus lainnya jika diperlukan
                    default:
                        Toast.makeText(context, "Screen not found", Toast.LENGTH_SHORT).show();
                        return;
                }

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datatableList.size();
    }

    public static class DatatableViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;
        ImageView iconImageView;

        public DatatableViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            subtitleTextView = itemView.findViewById(R.id.item_subtitle);
            iconImageView = itemView.findViewById(R.id.image_view);
        }
    }

    public void updateData(List<Datatable> datatableList) {
        this.datatableList = datatableList;
        notifyDataSetChanged();
    }
}
