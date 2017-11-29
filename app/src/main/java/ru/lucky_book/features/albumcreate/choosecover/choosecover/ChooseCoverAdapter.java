package ru.lucky_book.features.albumcreate.choosecover.choosecover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.albumcreate.choosecover.CoverViewHolder;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.CoverUtils;

/**
 * Created by demafayz on 25.08.16.
 */
public class ChooseCoverAdapter extends BaseRecyclerAdapter<ChooseCoverItem, CoverViewHolder> {
    public static final int PROMO = 0;
    public static final int DEFAULT = 1;

    public ChooseCoverAdapter(List<ChooseCoverItem> list) {
        super(list);
    }

    @Override
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        switch (viewType) {
            case PROMO:
                itemView = inflater.inflate(R.layout.cover_promo_item, parent, false);
                break;
            default:
            case DEFAULT:
                itemView = inflater.inflate(R.layout.cover_item, parent, false);
                break;
        }

        return new CoverViewHolder(itemView, mOnItemClickListener);
    }


    @Override
    public void onBindViewHolder(CoverViewHolder holder, int position) {
        ChooseCoverItem item = getItem(position);
        holder.title.setText(item.getTitle());
        Context context = holder.itemView.getContext();
        String path;
        if (getItemViewType(position) == PROMO) {
            path = CoverUtils.toAssetsUrl(item.getIcon());
        } else {
            path = CoverUtils.toUrl(item.getIcon());
        }
        RequestCreator requestCreator =
                Picasso
                        .with(context)
                        .load(path);
                //.placeholder(R.color.color_placeholder_photo);
        if (getItemViewType(position) == DEFAULT) {
            requestCreator.fit();
        } else {
            requestCreator
                    .resize(1024, 1024);
        }
        requestCreator
                .centerInside()
                .into(holder.icon);
    }

    @Override
    public int getItemViewType(int position) {
        ChooseCoverItem item = getItem(position);

        if (item.getIcon().contains("promo")) {
            return PROMO;
        }
        return DEFAULT;
    }
}
