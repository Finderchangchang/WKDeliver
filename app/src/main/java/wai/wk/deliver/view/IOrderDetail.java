package wai.wk.deliver.view;

import wai.wk.deliver.model.OrderModel;

/**
 * Created by Administrator on 2016/12/16.
 */

public interface IOrderDetail {
    void showOrder(OrderModel model);

    void changeResult(boolean result);
}
