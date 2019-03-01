package com.protobender.artlibary.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.protobender.artlibary.R;
import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.util.Tags;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ArtworkViewHolder>{

    String TAG = "Artwork Adapter";
    private Context mCtx;
    private List<Artwork> artworkList;
    private Map<Artwork, Long> artworkLongMap;
    private int MODE = 0;
    private static OnItemClickListener clickListener;


    public ArtworkAdapter(Context mCtx, List<Artwork> artworkList, int MODE) {
        this.mCtx = mCtx;
        this.MODE = MODE;
        this.artworkList = artworkList;
        artworkLongMap = new HashMap<>();
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ArtworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_artwork, parent, false);
        return new ArtworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtworkViewHolder holder, int position) {
        Artwork artwork = artworkList.get(position);
        Picasso.get()
                .load(artwork.getArtworkUrl())
                .placeholder(R.drawable.ic_photo_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .fit()
                .centerCrop()
                .into(holder.imgArtwork);
        if(MODE == Tags.BROWSE_MODE) {
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, mCtx.getResources().getDisplayMetrics());
            String rssi = "Strength: " + artwork.getRssi() + " dBm";

            ViewGroup.LayoutParams params = holder.imgArtwork.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = height;
            holder.imgArtwork.setLayoutParams(params);
            holder.txtStrength.setVisibility(View.VISIBLE);
            holder.txtStrength.setText(rssi);
        }
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) artworkList.get(position).hashCode();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        ArtworkAdapter.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ArtworkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgArtwork;
        TextView txtStrength;
        private ArtworkViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgArtwork = itemView.findViewById(R.id.imgItemArtwork);
            txtStrength = itemView.findViewById(R.id.txtItemStrength);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
