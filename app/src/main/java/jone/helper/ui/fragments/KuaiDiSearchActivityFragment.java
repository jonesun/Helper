package jone.helper.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jone.helper.R;
import jone.helper.databinding.FragmentKuaiDiSearchBinding;
import jone.helper.ui.activities.KuaiDiSearchActivity;
import jone.helper.ui.fragments.base.DataBindingBaseFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class KuaiDiSearchActivityFragment extends DataBindingBaseFragment<KuaiDiSearchActivity, FragmentKuaiDiSearchBinding> {
    private String TAG = KuaiDiSearchActivityFragment.class.getSimpleName();

    private ArrayAdapter<String> adapter;
    private List<String> kuaiDiNameList = new ArrayList<>();

    private String kuaiDiId;

    @Override
    protected int getContentView() {
        return R.layout.fragment_kuai_di_search;
    }

    @Override
    public void initViews(final FragmentKuaiDiSearchBinding viewDataBinding) {
        viewDataBinding.webViewResult.getSettings().setJavaScriptEnabled(true);
        Iterator<String> iterator = kuaiDiNameMap.keySet().iterator();
        while (iterator.hasNext()){
            kuaiDiNameList.add(iterator.next());
        }
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, kuaiDiNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewDataBinding.spinnerKuaidiName.setAdapter(adapter);
        viewDataBinding.spinnerKuaidiName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.e(TAG, "您选择的是：" + adapter.getItem(arg2));
//                textInputLayout.getEditText().setText("1600836108601");
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                Log.e(TAG, "NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
        viewDataBinding.textInputLayout.setHint("快递单号");
        viewDataBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kuaiDiId = viewDataBinding.textInputLayout.getEditText().getText().toString();
                if (TextUtils.isEmpty(kuaiDiId)) {
                    viewDataBinding.textInputLayout.setError("快递单号不能为空");
                } else {
                    StringBuffer url = new StringBuffer("http://m.kuaidi100.com/index_all.html");
                    url.append("?")
                            .append("type=").append(kuaiDiNameMap.get(viewDataBinding.spinnerKuaidiName.getSelectedItem().toString()))
                            .append("&").append("postid=").append(kuaiDiId)
                            .append("#result");
                    Log.e(TAG, "快递查询: " + url.toString());
                    if (getActivity().getWindow().peekDecorView() != null) {
                        InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    viewDataBinding.webViewResult.loadUrl(url.toString());
                }

            }
        });
        //webView_result.loadUrl("http://m.kuaidi100.com/index_all.html?type=yunda&postid=1600836108601#result");
//        webView_result.loadUrl("http://wap.kuaidi100.com/wap_result.jsp?rand=20120517&id=yunda&fromWeb=null&&postid=1600836108601");
    }

    public void search(String value){
        getViewDataBinding().textInputLayout.getEditText().setText(value);
        getViewDataBinding().textInputLayout.getEditText().setSelection(value.length());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            getViewDataBinding().btnSearch.callOnClick();
        }
    }

    private Map<String, String> kuaiDiNameMap = new HashMap<String, String>(){
        {
//            put("aae全球专递", "aae");
//            put("安捷快递", "anjie");
//            put("安信达快递", "anxindakuaixi");
//            put("彪记快递", "biaojikuaidi");

            put("德邦物流", "debangwuliu");
            put("ems快递", "ems");
            put("汇通快运", "huitongkuaidi");
            put("嘉里大通", "jialidatong");
            put("申通", "shentong");
            put("顺丰", "shunfeng");
            put("天天快递", "tiantian");
            put("邮政包裹挂号信", "youzhengguonei");
            put("圆通速递", "yuantong");
            put("韵达快运", "yunda");
            put("宅急送", "zhaijisong");
            put("中通速递", "zhongtong");
        }
    };
}
