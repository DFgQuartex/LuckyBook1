package ru.lucky_book.features.albumcreate.albumlist;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.transformation.HalfedTransformation;

/**
 * Created by demafayz on 29.08.16.
 */
public class AlbumListAdapter extends BaseRecyclerAdapter<RealmAlbum, AlbumViewHolder> {

    public AlbumListAdapter(List<RealmAlbum> albums) {
        super(albums);
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.album_list_item, parent, false);
        AlbumViewHolder vh = new AlbumViewHolder(itemView, mOnItemClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        RealmAlbum item = getItem(position);
        if (item.getCover() != null)
            Picasso.with(holder.itemView.getContext())
                    .load(new File(item.getCover()))
                    .fit()
                    .centerInside()
                    .transform(
                            new HalfedTransformation()
                    )
                    .into(holder.cover);
        else {
            Picasso.with(holder.itemView.getContext())
                    .load(R.drawable.luckybook_default_cover)
                    .fit()
                    .centerInside()
                    .transform(
                            new HalfedTransformation()
                    )
                    .into(holder.cover);
        }
        holder.title.setText(item.getTitle());
    }
}
