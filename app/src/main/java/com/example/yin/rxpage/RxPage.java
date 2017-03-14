package com.example.yin.rxpage;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;
import io.reactivex.ObservableTransformer;

import java.util.Arrays;

/**
 * Created by yin on 17-3-14.
 */
public class RxPage {

    private int limit;//每页加载数量
    private int threshold;//阀值，离总项数还有多少

    public RxPage(int limit, int threshold) {
        this.limit = limit;
        this.threshold = threshold;
    }

    public ObservableTransformer<? super RecyclerViewScrollEvent, Integer> transformerPage() {
        return upstream -> upstream.filter(event -> loading(event.view().getLayoutManager()))
                .map(this::mapPage)
                .distinctUntilChanged();//更改才改变
    }

    //把总项数转换成页数
    private Integer mapPage(RecyclerViewScrollEvent event) {
        int itemCount = event.view().getLayoutManager().getItemCount();
        return (int)Math.ceil((double)itemCount/limit)+1;
    }

    //过滤掉没达到加载条件的
    private boolean loading(LayoutManager layoutManager){
        if (layoutManager instanceof LinearLayoutManager) {
            int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            if (lastVisibleItemPosition+threshold>=layoutManager.getItemCount()) {
                return true;
            }
        }
        if (layoutManager instanceof GridLayoutManager) {
            int lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            if (lastVisibleItemPosition+threshold>=layoutManager.getItemCount()) {
                return true;
            }
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            Arrays.sort(lastVisibleItemPositions);
            if (lastVisibleItemPositions[lastVisibleItemPositions.length - 1] + threshold >= layoutManager.getItemCount()) {
                return true;
            }
        }
        return false;
    }
}
