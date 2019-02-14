package com.nh.cartoon.nhcartoon.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.logic.ImgLoaderHelper;
import com.nh.cartoon.nhcartoon.ui.view.CircleProgressView;
import com.nh.cartoon.nhcartoon.utils.StringUtils;

import static com.nh.cartoon.nhcartoon.utils.Constant.DETAIL_LIST_IMG_URL;


public class ImagePagerAdapter extends PagerAdapter implements View.OnClickListener {

    private static final String TAG = "ImagePagerAdapter";

    //图片资源合集:ViewPager滚动的页面种类
    private Context mContext;

    private NHBook mNHBook;

    private LayoutInflater mInflater;

    private ViewPager mViewPager;

    //构造函数
    public ImagePagerAdapter(Context context, NHBook nhBook, ViewPager viewPager) {
        super();
        this.mContext = context;
        this.mNHBook = nhBook;
        this.mViewPager = viewPager;
        mInflater = LayoutInflater.from(mContext);
    }

    //返回填充ViewPager页面的数量
    @Override
    public int getCount() {
        return mNHBook.getPageNumber();
    }

    //销毁ViewPager内某个页面时调用
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//固定是view == object
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position + 1;

        ViewHolder holder = new ViewHolder();
        holder.rootView = mInflater.inflate(R.layout.image_show_view, null);

        holder.imgPanel = holder.rootView.findViewById(R.id.img_panel);
        holder.leftView = holder.rootView.findViewById(R.id.img_go_left);
        holder.rightView = holder.rootView.findViewById(R.id.img_go_right);
        holder.pageNumView = holder.rootView.findViewById(R.id.image_page_num);
        holder.progressView = holder.rootView.findViewById(R.id.progress_view);
        holder.photoView = new PhotoView(mContext);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        holder.photoView.setLayoutParams(params);

        PhotoViewAttacher attacher = new PhotoViewAttacher(holder.photoView);

        holder.leftView.setOnClickListener(this);
        holder.rightView.setOnClickListener(this);

        holder.imgPanel.addView(holder.photoView);


        holder.pageNumView.setText(index + "/" + mNHBook.getPageNumber());


        String url = StringUtils.replaceParam(DETAIL_LIST_IMG_URL, mNHBook.getImgId(), index + "" , mNHBook.getImgType());

        //ImgLoaderHelper.getInstance(mContext).loadImage(url, holder.photoView);
        ImgLoaderHelper.getInstance(mContext).loadImageWithProgress(url, holder.photoView, holder.progressView);


        container.addView(holder.rootView);

        return holder.rootView;
    }

    @Override
    public void onClick(View v) {
        int index = mViewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.img_go_left:
                if (index == 0) {
                    return;
                }
                index--;
                mViewPager.setCurrentItem(index);
                break;
            case R.id.img_go_right:
                if (index == mNHBook.getPageNumber() - 1) {
                    return;
                }
                index++;
                mViewPager.setCurrentItem(index);
                break;
            default:
        }
    }

    class ViewHolder {
        View rootView;
        RelativeLayout imgPanel;
        View leftView;
        View rightView;
        TextView pageNumView;
        PhotoView photoView;
        CircleProgressView progressView;
    }
}
