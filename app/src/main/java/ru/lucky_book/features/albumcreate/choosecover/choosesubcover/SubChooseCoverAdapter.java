package ru.lucky_book.features.albumcreate.choosecover.choosesubcover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.albumcreate.choosecover.CoverViewHolder;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.CoverUtils;
import ru.lucky_book.utils.transformation.HalfedTransformation;

/**
 * Created by demafayz on 25.08.16.
 */
public class SubChooseCoverAdapter extends BaseRecyclerAdapter<ChooseCoverItem,CoverViewHolder> {

    public SubChooseCoverAdapter(List<ChooseCoverItem> list) {
        super(list);
    }

    @Override
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.sub_cover_item,parent,false);
        return new CoverViewHolder(view,mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(CoverViewHolder holder, int position) {
        ChooseCoverItem item=getItem(position);
        Context context=holder.itemView.getContext();
        Picasso
                .with(context)
                .load(CoverUtils.toUrl(item.getIcon()))
               // .placeholder(R.color.color_placeholder_photo)

                .fit()
                .centerInside()
                .transform(Arrays.asList(
                        new HalfedTransformation()
                ))
                .into(holder.icon);
    }
}
