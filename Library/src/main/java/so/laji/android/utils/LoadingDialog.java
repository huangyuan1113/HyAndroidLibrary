package so.laji.android.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import so.laji.android.R;


/**
 * 加载框
 * Created by folie on 16/3/24.
 */
public class LoadingDialog {

    /**
     * 创建加载对话框
     *
     * @param context
     * @param msg     加载中文字
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_loading_dialog, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.rl_dialog_view);// 加载布局
        TextView tipTextView = (TextView) view.findViewById(R.id.tv_loading_text);
        if (!TextUtils.isEmpty(msg)) {
            // 设置加载信息
            tipTextView.setText(msg);
        } else {
            // 没文字时不显示
            tipTextView.setVisibility(View.GONE);
        }
        Dialog dialog = new Dialog(context, R.style.LoadingDialogStyle);// 创建自定义样式dialog
        dialog.setCancelable(true);// 不可以用“返回键”取消
        // 设置布局
        dialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return dialog;

    }
}
