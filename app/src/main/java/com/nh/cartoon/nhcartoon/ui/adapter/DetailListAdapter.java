package com.nh.cartoon.nhcartoon.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nh.cartoon.nhcartoon.R;
import com.nh.cartoon.nhcartoon.bean.NHBook;
import com.nh.cartoon.nhcartoon.bean.NHBookTag;
import com.nh.cartoon.nhcartoon.database.BookDBHelper;
import com.nh.cartoon.nhcartoon.logic.ActivityJumpHelper;
import com.nh.cartoon.nhcartoon.logic.DownLoadHelper;
import com.nh.cartoon.nhcartoon.logic.ImgLoaderHelper;
import com.nh.cartoon.nhcartoon.ui.DetailActivity;
import com.nh.cartoon.nhcartoon.ui.view.CircleProgressView;
import com.nh.cartoon.nhcartoon.utils.Constant;
import com.nh.cartoon.nhcartoon.utils.Logger;
import com.nh.cartoon.nhcartoon.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nh.cartoon.nhcartoon.utils.Constant.BOOK_IMG_URL;
import static com.nh.cartoon.nhcartoon.utils.Constant.DETAIL_LIST_SIMPLE_IMG_URL;

public class DetailListAdapter extends BaseAdapter {

    private static final String TAG = "DetailListAdapter";

    private static final int VIEW_TYPE_HEAD = 0;
    private static final int VIEW_TYPE_PARODIES_TAG = 1;
    private static final int VIEW_TYPE_CHARACTERS_TAG = 2;
    private static final int VIEW_TYPE_TAGS_TAG = 3;
    private static final int VIEW_TYPE_ARTISTS_TAG = 4;
    private static final int VIEW_TYPE_GROUPS_TAG = 5;
    private static final int VIEW_TYPE_LANGUAGE_TAG = 6;
    private static final int VIEW_TYPE_CATEGORIES_TAG = 7;
    private static final int VIEW_TYPE_IMG = 8;


    private NHBook mNhBook;

    private DetailActivity mContext;

    private LayoutInflater mInflater;

    private ImgLoaderHelper mImgLoaderHelper;

    private View mHeadView;


    public DetailListAdapter(DetailActivity context, NHBook nhBook) {
        this.mContext = context;
        this.mNhBook = nhBook;
        mInflater = LayoutInflater.from(mContext);
        mImgLoaderHelper = ImgLoaderHelper.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return (mNhBook.getPageNumber() / 2) + (mNhBook.getPageNumber() % 2) + 8;
    }

    @Override
    public Object getItem(int position) {
        return mNhBook;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (position) {
            case VIEW_TYPE_HEAD:
                convertView = buildHeadView();
                break;
            case VIEW_TYPE_PARODIES_TAG:
                //convertView = buildTagViwe("parody", mNhBook.getParodies());
                convertView = buildTagView("parody");
                break;
            case VIEW_TYPE_CHARACTERS_TAG:
                //convertView = buildTagViwe("character", mNhBook.getCharacters());
                convertView = buildTagView("character");
                break;
            case VIEW_TYPE_TAGS_TAG:
                //convertView = buildTagViwe("tag", mNhBook.getTags());
                convertView = buildTagView("tag");
                break;
            case VIEW_TYPE_ARTISTS_TAG:
                //convertView = buildTagViwe("artist", mNhBook.getArtists());
                convertView = buildTagView("artist");
                break;
            case VIEW_TYPE_GROUPS_TAG:
                //convertView = buildTagViwe("group", mNhBook.getGroups());
                convertView = buildTagView("group");
                break;
            case VIEW_TYPE_LANGUAGE_TAG:
                //convertView = buildTagViwe("language", mNhBook.getLanguage());
                convertView = buildTagView("language");
                break;
            case VIEW_TYPE_CATEGORIES_TAG:
                //convertView = buildTagViwe("category", mNhBook.getCategories());
                convertView = buildTagView("category");
                break;
            default:
                convertView = buildImg(position);
        }

        return convertView;
    }

    private View buildImg(int position) {
        final int index = position - 7;
        View view = mInflater.inflate(R.layout.detail_list_img, null);
        ImageView img1 = view.findViewById(R.id.detail_img_1);
        ImageView img2 = view.findViewById(R.id.detail_img_2);
        final int leftIndex = index * 2 - 1;
        final int rightIndex = index * 2;

        if (leftIndex <= mNhBook.getPageNumber()) {
            mImgLoaderHelper.loadImage(StringUtils.replaceParam(
                    DETAIL_LIST_SIMPLE_IMG_URL,
                    mNhBook.getImgId() + "",
                    leftIndex + "",
                    mNhBook.getImgType()), img1);
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, leftIndex + "", Toast.LENGTH_SHORT).show();
                    ActivityJumpHelper.goImageShowActivity(mContext, mNhBook.getId(), leftIndex);
                }
            });
        } else {
            img1.setVisibility(View.GONE);
        }
        if (rightIndex <= mNhBook.getPageNumber()) {
            mImgLoaderHelper.loadImage(StringUtils.replaceParam(
                    DETAIL_LIST_SIMPLE_IMG_URL,
                    mNhBook.getImgId() + "",
                    rightIndex + "",
                    mNhBook.getImgType()), img2);
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, rightIndex + "", Toast.LENGTH_SHORT).show();
                    ActivityJumpHelper.goImageShowActivity(mContext, mNhBook.getId(), rightIndex);
                }
            });
        } else {
            img2.setVisibility(View.GONE);
        }

        return view;
    }

    private Map<String, View> mMap = new HashMap<>();

    private View buildTagView(String tagTitle) {
        List<NHBookTag> tags = mNhBook.getTagMap().get(tagTitle);

        if (tags == null || tags.size() == 0) {
            return new TextView(mContext);
        }
        View view = mMap.get(tagTitle);
        if (view != null) {
            return view;
        }
        view = mInflater.inflate(R.layout.tag_panel_item, null);
        mMap.put(tagTitle, view);
        TextView titleView = view.findViewById(R.id.tag_title);
        titleView.setText(tagTitle);

        ViewGroup tagList = view.findViewById(R.id.tag_list);
        for (NHBookTag tmpTag : tags) {
            TextView tagView = (TextView) mInflater.inflate(R.layout.tag_item, null);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(20, 20, 20, 20);

            tagView.setLayoutParams(layoutParams);
            String showName = tmpTag.getName();
            if (!TextUtils.isEmpty(tmpTag.getCnName())) {
                showName = tmpTag.getCnName();
            }

            tagView.setText(showName + "(" + tmpTag.getCount() + ")");
            tagView.setTag(tmpTag);
            tagList.addView(tagView);
            final String url = tmpTag.getUrl();
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
                    mContext.onBackMainActivity(url);
                }
            });
            tagView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logger.d(TAG, "setOnLongClickListener");
                    TextView tmpView = (TextView) v;
                    NHBookTag tag = (NHBookTag) tmpView.getTag();
                    if (TextUtils.isEmpty(tag.getCnName())) {
                        tmpView.setText(tag.getName() + "(" + tag.getCount() + ")");
                        return true;
                    }
                    String viewText = tmpView.getText().toString();
                    if (viewText.contains(tag.getCnName())) {
                        tmpView.setText(tag.getName() + "(" + tag.getCount() + ")");
                    } else {
                        tmpView.setText(tag.getCnName() + "(" + tag.getCount() + ")");
                    }
                    return true;
                }
            });
        }

        return view;
    }

    private View buildHeadView() {
        if (mHeadView == null) {
            mHeadView = mInflater.inflate(R.layout.detail_head, null);
            ImageView headimage = mHeadView.findViewById(R.id.title_img);
            ImageView langImage = mHeadView.findViewById(R.id.lang_img);
            View downloadBtn = mHeadView.findViewById(R.id.download_btn);

            CircleProgressView progressView = mHeadView.findViewById(R.id.progress_view);

            switch (mNhBook.getLanguageType()) {
                case Constant.LANGUAGE_TYPE_CN:
                    langImage.setBackgroundResource(R.mipmap.ic_lang_cn);
                    break;
                case Constant.LANGUAGE_TYPE_JP:
                    langImage.setBackgroundResource(R.mipmap.ic_lang_jp);
                    break;
                case Constant.LANGUAGE_TYPE_GB:
                default:
                    langImage.setBackgroundResource(R.mipmap.ic_lang_gb);
            }


            //advanceLoaderImage();
            TextView titleView = mHeadView.findViewById(R.id.title_str);

            mImgLoaderHelper.loadImageWithProgress(
                    StringUtils.replaceParam(BOOK_IMG_URL, mNhBook.getImgId(), mNhBook.getImgType()),
                    headimage,
                    progressView);
            titleView.setText(mNhBook.getTitle());

            View loveBtn = mHeadView.findViewById(R.id.love_btn);
            ImageView loveImage = mHeadView.findViewById(R.id.love_img);
            loveImage.setBackgroundResource(mNhBook.getFavorite() == Constant.IS_FAVORITE ? R.mipmap.love_1 : R.mipmap.love_2);
            loveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNhBook.getFavorite() == Constant.IS_FAVORITE) {
                        mNhBook.setFavorite(Constant.NOT_FAVORITE);

                    } else {
                        mNhBook.setFavorite(Constant.IS_FAVORITE);
                    }
                    BookDBHelper.getInstance(mContext).updateBookFavorite(mNhBook.getId(), mNhBook.getFavorite());
                    ImageView loveImage = mHeadView.findViewById(R.id.love_img);
                    loveImage.setBackgroundResource(mNhBook.getFavorite() == Constant.IS_FAVORITE ? R.mipmap.love_1 : R.mipmap.love_2);
                }
            });

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                    DownLoadHelper.doDownLoad(mContext, mNhBook);
                }
            });

            headimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.d(TAG, "headimage setOnClickListener");
                    ActivityJumpHelper.goImageShowActivity(mContext, mNhBook.getId(), 1);
                }
            });
        }

        return mHeadView;
    }

    public void onDestory() {
        if (mHeadView == null) {
            return;
        }
        ImageView headimage = mHeadView.findViewById(R.id.title_img);
        mImgLoaderHelper.onDestroy(headimage);
    }

}
