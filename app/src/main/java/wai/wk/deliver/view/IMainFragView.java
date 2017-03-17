package wai.wk.deliver.view;

import java.util.List;

import wai.wk.deliver.model.ListModel;
import wai.wk.deliver.model.OrderModel;

/**
 * Created by Administrator on 2016/10/14.
 */

public interface IMainFragView {
    /**
     * 刷新后返回数据
     *
     * @param list
     */
    void refreshOrder(List<OrderModel> list);

    /**
     * 刷新处理结果
     *
     * @param list   列表
     */
    void refresh(ListModel list);

    /**
     * 加载更多显示数据
     *
     * @param list
     */
    void loadMoreOrder(List<OrderModel> list);

    /**
     * 改变订单状态处理结果
     *
     * @param result
     */
    void changeStateResult(boolean result);

}
