package wai.wk.deliver.view;

import wai.wk.deliver.model.OrderModel;

/**
 * 订单详情数据显示
 * Created by Administrator on 2016/10/15.
 */

public interface IOrderDetailView {
    /**
     * 显示订单详情
     * @param model
     */
    void showOrder(OrderModel model);
    void changeResult(String result);
}
