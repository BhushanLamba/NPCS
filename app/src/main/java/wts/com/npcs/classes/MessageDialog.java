package wts.com.npcs.classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import wts.com.npcs.R;
import wts.com.npcs.activities.RechargeActivity;

public class MessageDialog
{
    public static void showMessageDialog(String message, Context context, Activity activity)
    {
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.message_dialog, null);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setView(convertView);
        messageDialog.setCancelable(false);
        messageDialog.show();

        ImageView imgClose=convertView.findViewById(R.id.img_close);
        TextView tvMessage=convertView.findViewById(R.id.tv_message);

        imgClose.setOnClickListener(v->
        {
            messageDialog.dismiss();
        });

        tvMessage.setText(message);
    }
}
