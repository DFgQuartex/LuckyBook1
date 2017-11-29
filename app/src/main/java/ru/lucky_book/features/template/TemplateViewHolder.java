package ru.lucky_book.features.template;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.template.TemplatePicture;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.utils.SpreadUtils;

/**
 * Created by histler
 * on 08.09.16 14:17.
 */
/*public class TemplateViewHolder extends BaseViewHolder {

    public TextView templateCount;
    private ViewGroup holder;

    public TemplateViewHolder(View itemView, OnItemClickListener clickListener) {
        super(itemView, clickListener);
    }

    @Override
    protected void initView(View itemView) {
        holder= (ViewGroup) itemView.findViewById(R.id.selection_holder);
        templateCount= (TextView) itemView.findViewById(R.id.template_num);
    }

    public void init(PageTemplate template){
        Context context=itemView.getContext();
        templateCount.setText(context.getResources().getString(R.string.template_photos,template.getImagesCount()));
        Page page=new Page();
        page.setTemplate(template);
        Arrays.fill(page.getPictures(),new TemplatePicture());
        SpreadUtils.bindPage(page,holder,true,null);

    }

    @Override
    public void setSelection(boolean isSelected) {
        holder.setSelected(isSelected);
    }
}
*/