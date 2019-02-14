package com.nh.cartoon.nhcartoon.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.logic.ActivityJumpHelper;
import com.nh.cartoon.nhcartoon.logic.ImgLoaderHelper;
import com.nh.cartoon.nhcartoon.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends BaseAdapter implements View.OnClickListener {

    private List<NHBook> mNHBooks = new ArrayList<>();

    private Activity mContext;

    private LayoutInflater mInflater;

    private ImgLoaderHelper mImgLoaderHelper;

    public BooksAdapter(Activity context, List<NHBook> nhBooks) {
        mContext = context;
        if (nhBooks != null) {
            mNHBooks.addAll(nhBooks);
        }
        mImgLoaderHelper = ImgLoaderHelper.getInstance(context);
        mInflater = LayoutInflater.from(mContext);
    }

    @SuppressLint("NewApi")
    public void addBooks(List<NHBook> nhBooks) {
        mNHBooks.clear();
        mNHBooks.addAll(nhBooks);
        notifyDataSetChanged();
    }

    public void clearBooks() {
        mNHBooks.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mNHBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return mNHBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.books_item, null);
            holder.imageView = convertView.findViewById(R.id.img);
            holder.titleView = convertView.findViewById(R.id.books_title);
            holder.langImage = convertView.findViewById(R.id.lang_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleView.setText(mNHBooks.get(position).getTitle());
        holder.imageView.setOnClickListener(this);
        holder.imageView.setTag(mNHBooks.get(position).getId());

        switch (mNHBooks.get(position).getLanguageType()) {
            case Constant.LANGUAGE_TYPE_CN:
                holder.langImage.setBackgroundResource(R.mipmap.ic_lang_cn);
                break;
            case Constant.LANGUAGE_TYPE_JP:
                holder.langImage.setBackgroundResource(R.mipmap.ic_lang_jp);
                break;
            case Constant.LANGUAGE_TYPE_GB:
            default:
                holder.langImage.setBackgroundResource(R.mipmap.ic_lang_gb);
        }

        mImgLoaderHelper.loadImage(mNHBooks.get(position).getImgUrl(), holder.imageView);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        String id = (String) v.getTag();
        Toast.makeText(mContext, "id -> " + id, Toast.LENGTH_SHORT).show();
        ActivityJumpHelper.goDetailActivity(mContext, id);
    }

    class ViewHolder {
        ImageView imageView;
        TextView titleView;
        ImageView langImage;
    }
}
