package com.mabeijianxi.smallvideo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @auther 赵广 on 2018/6/22 0022.
 * com.chinamobile.sdgzt.adapter
 */
public class YinxiaoSnWgAdapter extends BaseQuickAdapter<MediaBean, BaseViewHolder> {

    public YinxiaoSnWgAdapter() {
        super(R.layout.item_layout);

    }

    @Override
    protected void convert(BaseViewHolder helper, MediaBean item) {
        Bitmap bitmap = BitmapFactory.decodeFile(item.thumbPath);
        helper.setImageBitmap(R.id.ima_show,bitmap);
        helper.setText(R.id.show_name,item.getName());
        helper.setText(R.id.show_time,FileUtil.formatDateTime(item.getDuration()));
        helper.setText(R.id.show_big,item.getSize());
        helper.addOnClickListener(R.id.ll_show);
        helper.addOnClickListener(R.id.ima_show);
    }
    }





