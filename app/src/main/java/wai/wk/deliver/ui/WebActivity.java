package wai.wk.deliver.ui;

import android.webkit.WebView;

import net.tsz.afinal.view.TitleBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import wai.wk.deliver.R;
import wai.wk.deliver.base.BaseActivity;
import wai.wk.deliver.method.HttpUtil;

/**
 * Created by Administrator on 2016/12/3.
 */

public class WebActivity extends BaseActivity {
    @Bind(R.id.title_bar)
    TitleBar titleBar;
    @Bind(R.id.web_view)
    WebView webView;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
    }

    @Override
    public void initEvents() {
        titleBar.setLeftClick(() -> finish());
        String title = getIntent().getStringExtra("web");
        titleBar.setCenter_str(title);
        String url = "";
        switch (title) {
            case "规则":
                url = HttpUtil.BASE_URL + "App/Android/Deliver/AboutUs.aspx?dataid=19";
                break;
            case "关于":
                url = HttpUtil.BASE_URL + "App/Android/Deliver/AboutUs.aspx?dataid=18";
                break;
        }
        webView.loadUrl(url);
    }
}
