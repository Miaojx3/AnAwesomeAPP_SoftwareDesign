package com.seu.magiccamera.view.edit.filter;

/**
 * Created by kouseishouganzhoushizenminamixianxianjoushinseigentanleijou on 16/7/14.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.view.edit.filter.FilterInfo;
import com.seu.magicfilter.display.MagicDisplay;
import com.seu.magicfilter.filter.helper.MagicFilterType;

public class FilterLayoutUtils{
    private Context mContext;
    private MagicDisplay mMagicDisplay;
    private FilterAdapter mAdapter;
    private ImageView btn_Favourite;

    private int position;
    private List<FilterInfo> filterInfos;
    private List<FilterInfo> favouriteFilterInfos;

    private MagicFilterType mFilterType = MagicFilterType.NONE;

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    public FilterLayoutUtils(Context context,MagicDisplay magicDisplay) {
        mContext = context;
        mMagicDisplay = magicDisplay;
    }

    public void init(){
        //btn_Favourite = (ImageView) ((Activity) mContext).findViewById(R.id.btn_camera_favourite);
        //btn_Favourite.setOnClickListener(btn_Favourite_listener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mFilterListView = (RecyclerView)((Activity) mContext).findViewById(R.id.filter_listView);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(mContext, types);
        mFilterListView.setAdapter(mAdapter);
        initFilterInfos();
        //mAdapter.setFilterInfos(filterInfos);
        //mAdapter.setOnFilterChangeListener(onFilterChangeListener);
    }

    public void init(View view){


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mFilterListView = (RecyclerView) view.findViewById(R.id.filter_listView);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(mContext, types);
        mFilterListView.setAdapter(mAdapter);
        initFilterInfos();
        //mAdapter.setFilterInfos(filterInfos);
        //mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        view.findViewById(R.id.btn_camera_closefilter).setVisibility(View.GONE);
    }

    /*private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(MagicFilterType filterType, int position) {
            // TODO Auto-generated method stub
            int Type = filterInfos.get(position).getFilterType();//»ñÈ¡ÀàÐÍ
            FilterLayoutUtils.this.position = position;
            mMagicDisplay.setFilter(filterType);
            mFilterType = filterType;
            if(position != 0)
                btn_Favourite.setVisibility(View.VISIBLE);
            else
                btn_Favourite.setVisibility(View.INVISIBLE);
            btn_Favourite.setSelected(filterInfos.get(position).isFavourite());
            if(position <= favouriteFilterInfos.size()){//µã»÷FavouriteÁÐ±í
                for(int i = favouriteFilterInfos.size() + 2; i < filterInfos.size(); i++){
                    if(filterInfos.get(i).getFilterType() == Type){
                        filterInfos.get(i).setSelected(true);
                        //mAdapter.setLastSelected(i);
                        FilterLayoutUtils.this.position = i;
                        mAdapter.notifyItemChanged(i);
                    }else if(filterInfos.get(i).isSelected()){
                        filterInfos.get(i).setSelected(false);
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }
            for(int i = 1; i < favouriteFilterInfos.size() + 1; i++){
                if(filterInfos.get(i).getFilterType() == Type){
                    filterInfos.get(i).setSelected(true);
                    mAdapter.notifyItemChanged(i);
                }else if(filterInfos.get(i).isSelected()){
                    filterInfos.get(i).setSelected(false);
                    mAdapter.notifyItemChanged(i);
                }
            }
        }

    };*/

    private void initFilterInfos(){
        filterInfos = new ArrayList<FilterInfo>();
        //add original
        FilterInfo filterInfo = new FilterInfo();
        //filterInfo.setFilterType(MagicFilterType.NONE);
        filterInfo.setSelected(true);
        filterInfos.add(filterInfo);

        //add Favourite
        //loadFavourite();
        for(int i = 0;i < favouriteFilterInfos.size(); i++){
            filterInfo = new FilterInfo();
            filterInfo.setFilterType(favouriteFilterInfos.get(i).getFilterType());
            filterInfo.setFavourite(true);
            filterInfos.add(filterInfo);
        }
        //add Divider
        filterInfo = new FilterInfo();
        filterInfo.setFilterType(-1);
        filterInfos.add(filterInfo);

        //addAll
        /*for(int i = 1;i <= 42; i++){
            filterInfo = new FilterInfo();
            filterInfo.setFilterType(MagicFilterType.NONE + i);
            for(int j = 0;j < favouriteFilterInfos.size(); j++){
                if(MagicFilterType.NONE + i == favouriteFilterInfos.get(j).getFilterType()){
                    filterInfo.setFavourite(true);
                    break;
                }
            }
            filterInfos.add(filterInfo);
        }*/
    }



    public MagicFilterType getFilterType(){
        return mFilterType;
    }
}
