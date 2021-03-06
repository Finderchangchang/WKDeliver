package wai.wk.deliver.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wai.wk.deliver.R;
import wai.wk.deliver.listener.MainFragListener;
import wai.wk.deliver.method.CommonAdapter;
import wai.wk.deliver.method.CommonViewHolder;
import wai.wk.deliver.method.Utils;
import wai.wk.deliver.model.ListModel;
import wai.wk.deliver.model.OrderModel;
import wai.wk.deliver.view.IMainFragView;

/**
 * Created by Administrator on 2016/10/14.
 */

public class OrderFragment extends Fragment implements IMainFragView {
    ListView listView;
    CommonAdapter<OrderModel> mAdapter;
    View view;
    MainFragListener listener;
    int tab_index = 0;
    private List<OrderModel> mOrders;
    LinearLayout no_data_ll;
    SwipeRefreshLayout item_refresh_sw;
    int pageNum = 1;
    int totalPage = 1;
    boolean isLoad = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new MainFragListener(this);
        mOrders = new ArrayList<>();
        mAdapter = new CommonAdapter<OrderModel>(MyOrderActivity.mInstance,
                mOrders, R.layout.item_order) {
            @Override
            public void convert(CommonViewHolder holder, OrderModel model, int position) {
                String btn_state = "";
                holder.setText(R.id.remark_tv, model.getOrderAttach());
                //配送状态：0：未配送(0页面：抢单，1页面：到商家)，1.已接单。5到商家（配送中），2：配送中（配送完成），3：配送完成， 4：配送失败
                switch (model.getSendstate()) {
                    case "0":
                        btn_state = "抢单";
                        break;
                    case "1":
                        btn_state = "到商家";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.no_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;
                    case "5":
                        btn_state = "拍照并送货";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;
                    case "2":
                        btn_state = "完成配送";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.no_ren);
                        break;
                    case "3":
                        btn_state = "已完成";
                        holder.setImageResource(R.id.che_iv, R.mipmap.sy_che);
                        holder.setImageResource(R.id.dian_iv, R.mipmap.sy_dian);
                        holder.setImageResource(R.id.ren_iv, R.mipmap.sy_ren);
                        break;
                    case "4":
                        btn_state = "已取消";
                        break;
                }
                //1--1  25--2 3--3
                holder.setOnClickListener(R.id.tou_ll, v -> {
                    if (!("").equals(model.getUlng())) {//导航到用户
                        String lat_lng = model.getUlat() + "-" + model.getUlng();
                        Utils.IntentPost(TestActivity.class, intent -> {
                            intent.putExtra("key", lat_lng);
                            intent.putExtra("address", model.getAddress());
                        });
                    } else {
                        MyOrderActivity.mInstance.ToastShort("坐标有问题无法导航 -_-!");
                    }
                });
                holder.setText(R.id.change_state_tv, btn_state);
                holder.setOnClickListener(R.id.tos_ll, v -> {
                    if (!("").equals(model.getSlat())) {//到商家
                        String lat_lng = Utils.check(model.getSlat(), model.getSlng());
                        Utils.IntentPost(TestActivity.class, intent -> {
                            intent.putExtra("key", lat_lng);
                            intent.putExtra("address", model.getShopaddress());
                        });
                    } else {
                        MyOrderActivity.mInstance.ToastShort("坐标有问题无法导航 -_-!");
                    }
                });

                //改变当前的订单状态
                holder.setOnClickListener(R.id.order_state_ll, v -> {
                    if (!("5").equals(model.getSendstate())) {
                        String now_state = "0";
                        switch (model.getSendstate()) {
                            case "0":
                                now_state = "1";
                                break;
                            case "1":
                                now_state = "5";
                                break;
                            case "5":
                                now_state = "2";
                                break;
                            case "2":
                                now_state = "3";
                                break;
                        }
                        if (("1").equals(now_state)) {
                            listener.qiangDan("1", model.getOrderid());
                        } else if (("3").equals(now_state)) {
                            listener.finishOrder(now_state, "1", model.getOrderid(), Utils.getCache("address")
                                    , Utils.getCache("lat"), Utils.getCache("lon"), Utils.getCache("cityName"));
                        } else if (("5").equals(now_state)) {
                            listener.finishOrder(now_state, "1", model.getOrderid(), "", "", "", "");
                        }
                    } else {
                        Intent intent = new Intent(MyOrderActivity.mInstance, CameraActivity.class);
                        intent.putExtra("orderid", model.getOrderid());
                        startActivityForResult(intent, 99);
                        listener.finishOrder("2", "1", model.getOrderid(), "", "", "", "");
                    }
                });
                holder.setText(R.id.jl1_tv, model.getJuLi());
                holder.setText(R.id.jl2_tv, model.getSJDaoYH());
                holder.setText(R.id.qh_tv, model.getShopname());
                holder.setText(R.id.xx_address_tv, model.getTogoAddress());
                holder.setText(R.id.sh_tv, model.getAddress());
                holder.setText(R.id.fb_time_tv, model.getAddtime());
                holder.setText(R.id.sr_tv, model.getSendFee());
                holder.setOnClickListener(R.id.tel1_iv, v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getPotioncomm()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
                holder.setOnClickListener(R.id.tel2_iv, v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getOrderComm()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) view.findViewById(R.id.list_frag);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(MyOrderActivity.mInstance, OrderDetailActivitys.class);
            if (mOrders.size() > 0) {
                intent.putExtra("orderid", mOrders.get(position).getOrderid());
                intent.putExtra("foodno", mOrders.get(position).getFoodNo());
                intent.putExtra("btn_state", mOrders.get(position).getSendstate());
            }
            startActivityForResult(intent, 44);
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (!isLoading && totalPage > pageNum) {
                            isLoading = true;
                            Toast.makeText(MyOrderActivity.mInstance, pageNum + "/" + totalPage, Toast.LENGTH_SHORT).show();
                            pageNum = pageNum + 1;
                            if (tab_index == 2) {
                                listener.loadOrder(pageNum, "2,5");
                            } else {
                                listener.loadOrder(pageNum, tab_index + "");
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        item_refresh_sw = (SwipeRefreshLayout) view.findViewById(R.id.item_refresh_sw);
        no_data_ll = (LinearLayout) view.findViewById(R.id.no_data_ll);
        item_refresh_sw.setOnRefreshListener(() -> {
            pageNum = 1;
            refreshList(tab_index);
        });
        return view;
    }

    boolean isLoading = false;

    /**
     * 刷新当前列表
     *
     * @param position 当前选中的item位置
     */
    public void refreshList(int position) {
        isLoad = true;
        if (item_refresh_sw != null) {
            item_refresh_sw.setRefreshing(true);
        }
        tab_index = position;
        if (listener == null) {
            listener = new MainFragListener(this);
        }
        mOrders = new ArrayList<>();
        if (position == 2) {
            listener.loadOrder(1, "2,5");
        } else {
            listener.loadOrder(1, position + "");
        }
    }


    @Override
    public void refreshOrder(List<OrderModel> list) {
        item_refresh_sw.setRefreshing(false);
        mOrders = list;
        if (list != null) {
            mAdapter.refresh(list);
            if (list.size() > 0) {
                no_data_ll.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                listView.setVisibility(View.GONE);
                no_data_ll.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.refresh(new ArrayList<>());
            listView.setVisibility(View.GONE);
            no_data_ll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refresh(ListModel list) {
        isLoading = false;
        item_refresh_sw.setRefreshing(false);
        pageNum = Integer.parseInt(list.getPage());
        totalPage = Integer.parseInt(list.getTotal());
        if (pageNum > 1) {
            List<OrderModel> order = list.getOrderlist();
            for (OrderModel model : order) {
                mOrders.add(model);
            }
        } else {
            mOrders = list.getOrderlist();
        }
        if (list != null) {
            mAdapter.refresh(mOrders);
            if (mOrders.size() > 0) {
                no_data_ll.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                listView.setVisibility(View.GONE);
                no_data_ll.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.refresh(new ArrayList<>());
            listView.setVisibility(View.GONE);
            no_data_ll.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void loadMoreOrder(List<OrderModel> list) {

    }

    @Override
    public void changeStateResult(boolean result) {
        if (result) {
//            refreshList(tab_index);
            MyOrderActivity.mInstance.ToastShort("更新成功~~");
        } else {
            MyOrderActivity.mInstance.ToastShort("更新失败~~");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 99:
                refreshList(tab_index);
                break;
            case 44:
                refreshList(tab_index);
                break;
        }
    }
}
