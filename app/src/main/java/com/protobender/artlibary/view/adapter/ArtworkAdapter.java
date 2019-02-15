package com.protobender.artlibary.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.protobender.artlibary.R;
import com.protobender.artlibary.model.entity.Artwork;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ArtworkViewHolder>{

    String TAG = "Artwork Adapter";
    private Context mCtx;
    private List<Artwork> artworkList;
    private static OnItemClickListener clickListener;

    public ArtworkAdapter(Context mCtx, List<Artwork> artworkList) {
        this.mCtx = mCtx;
        this.artworkList = artworkList;
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
        Log.d(TAG, "onBindViewHolder: " + artwork.getArtworkName());
        Picasso.get()
                .load(artwork.getArtworkUrl())
                .placeholder(R.drawable.ic_photo_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .into(holder.imgArtwork);
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        ArtworkAdapter.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ArtworkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgArtwork;
        private ArtworkViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgArtwork = itemView.findViewById(R.id.imgItemArtwork);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
