package ru.lucky_book.features.template;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Arrays;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.SpreadUtils;

/**
 * Created by histler
 * on 08.09.16 14:27.
 */
/*public class TemplatesAdapter extends BaseRecyclerAdapter<PageTemplate,TemplateViewHolder> {
    private int mHeight;
    public TemplatesAdapter(int originalHeight) {
        super(Arrays.asList(PageTemplate.values()));
        mHeight =originalHeight/ SpreadUtils.PREVIEW_SCALE;
    }

    @Override
    public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.template_row,parent,false);
        View sizedPreview=view.findViewById(R.id.selection_holder);
        LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams)sizedPreview.getLayoutParams();
        layoutParams.width= mHeight;
        layoutParams.height= mHeight;
        sizedPreview.setLayoutParams(layoutParams);
        return new TemplateViewHolder(view,mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(TemplateViewHolder holder, int position) {
        holder.init(getItem(position));
    }
}
*/