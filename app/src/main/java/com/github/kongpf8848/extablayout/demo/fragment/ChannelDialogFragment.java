package com.github.kongpf8848.extablayout.demo.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kongpf8848.extablayout.demo.CommonPreferenceManager;
import com.github.kongpf8848.extablayout.demo.base.BaseRecyclerViewAdapter;
import com.github.kongpf8848.extablayout.demo.channel.IChannelManage;
import com.github.kongpf8848.extablayout.demo.R;
import com.github.kongpf8848.extablayout.demo.adapter.ChannelAdapter;
import com.github.kongpf8848.extablayout.demo.bean.Channel;
import com.github.kongpf8848.extablayout.demo.touchhelper.OnItemTouchHelperListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.multitype.MultiTypeAdapter;

public class ChannelDialogFragment extends DialogFragment implements BaseRecyclerViewAdapter.OnItemClickListener<Channel>,OnItemTouchHelperListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.tv_edit)
    TextView tv_edit;

    private List<Channel> selectedChannelList = new ArrayList<>();
    private List<Channel> unselectedChannelList = new ArrayList<>();
    private List<Channel>allData = new ArrayList<>();
    private MultiTypeAdapter multiTypeAdapter;
    private ChannelAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        }
        return inflater.inflate(R.layout.dialog_channel, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        selectedChannelList=( List<Channel>)getArguments().getSerializable(CommonPreferenceManager.SELECTED_CHANNEL_DATA);
        unselectedChannelList=( List<Channel>)getArguments().getSerializable(CommonPreferenceManager.UNSELECTED_CHANNEL_DATA);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType=adapter.getItemViewType(position);
                if(viewType== ChannelAdapter.TYPE_RECOMMEND_TITLE ){
                    return 4;
                }
                return 1;
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        allData.clear();
        if(selectedChannelList!=null && selectedChannelList.size()>0){
           allData.addAll(selectedChannelList);
        }

        if(unselectedChannelList!=null && unselectedChannelList.size()>0){
           allData.addAll(unselectedChannelList);
        }
        multiTypeAdapter=new MultiTypeAdapter();
        //multiTypeAdapter.register(Channel.class).to(new MyChannelViewBinder(),)
        multiTypeAdapter.setItems(allData);


        adapter = new ChannelAdapter(getActivity(), allData);
        adapter.setOnItemTouchHelperListener(this);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 编辑
     */
    @OnClick(R.id.tv_edit)
    public void onClickEdit()
    {
        adapter.toogleEditMode();
        setEditStatus();

    }

    private void setEditStatus(){
        if(adapter.isEditMode()){
            tv_tip.setText("拖拽可以排序");
            tv_edit.setBackgroundResource(R.drawable.bg_shape_finish);
            tv_edit.setText(R.string.finish);
            tv_edit.setTextColor(Color.WHITE);
        }
        else{
            tv_tip.setText("点击进入频道");
            tv_edit.setBackgroundResource(R.drawable.bg_shape_edit);
            tv_edit.setText(R.string.edit);
            tv_edit.setTextColor(Color.parseColor("#0000EE"));
        }
    }

    @OnClick(R.id.iv_close)
    public void onClose() {
        List<Channel>selectedList=new ArrayList<>();
        for(Channel channel:allData){
            Log.d("JACK8","channel:"+channel);
        }
        if(getActivity() instanceof IChannelManage){
           // ((IChannelManage)getActivity()).onDragChannelFinish(List<Channel>selectedChannelList,);
        }
        dismiss();
    }


    @Override
    public void onItemDragStart(int position) {
        Log.d("JACK8", "onItemDragStart() called with: position = [" + position + "]");
        setEditStatus();
    }

    @Override
    public void onItemMove(int starPosition, int endPosition) {
        Log.d("JACK8", "onItemMove() called with: starPosition = [" + starPosition + "], endPosition = [" + endPosition + "]");
        Channel channel = allData.get(starPosition);
        allData.remove(starPosition);
        allData.add(endPosition, channel);
        adapter.notifyItemMoved(starPosition, endPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Log.d("JACK8", "onItemDismiss() called with: position = [" + position + "]");
    }

    @Override
    public void onRecyclerViewItemClick(int position, Channel channel) {
        Log.d("JACK8","onFinish:"+channel.getChannelName());
        if(getActivity() instanceof IChannelManage){
            ((IChannelManage)getActivity()).onSelectedChannel(channel);
        }
        dismiss();
    }

    @Override
    public void onRecyclerViewItemLongClick(int position, Channel channel) {

    }
}



