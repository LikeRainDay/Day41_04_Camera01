package com.l000phone.camera01;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.l000phone.utils.ImageUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    @ViewInject(R.id.imgId)
    ImageView imgView;//将属性与界面控件建立关联

    private File imgDir = Environment.getExternalStorageDirectory();//获得sd卡的路径
    //	private File imgDir;
    private String imgFileName = null;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    private String uploadAction = "http://10.0.182.64:8080/SinaWebSide/UserRegistServlet";

    private HttpUtils hUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //启用注解方式，达到的效果是：不用再findViewById了，也可以获取界面上的控件实例。
        ViewUtils.inject(this);

        hUtils = new HttpUtils(10 * 1000); //超时时间：30秒
        //imgDir=getCacheDir(); //图片的存储目录：内部存储的缓存目录，如果图片较大，禁止使用
    }

    /**
     * 给界面上的按钮控件btn2Id添加OnClickListener监听器
     * @param v
     */
    @OnClick(R.id.btn2Id)
    public void uploadImg(View v) {
        //将当前拍照的图片上传到服务器
        RequestParams rParams = new RequestParams("utf-8");
        rParams.addBodyParameter("name", "jason");
        rParams.addBodyParameter("pwd", "123");
        rParams.addBodyParameter("photo", new File(imgDir, imgFileName));

        /**
         * 向远程服务器发送post请求，将sd卡上存储的拍摄完的照片上传
         */
        hUtils.send(HttpMethod.POST, uploadAction, rParams, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(MainActivity.this,"图片上传失败！，msg="+msg,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject obj = new JSONObject(responseInfo.result);
                    boolean status = obj.getBoolean("status");

                    if (status) {
                        Toast.makeText(getApplicationContext(), "上传图片成功！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 拍照按钮，会触发执行
     * @param v
     */
    @OnClick(R.id.btnId)
    public void takeImg(View v) {

        //实例化意图，并设置拍照的Action
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //设置保存图片路径
        imgFileName = sdf.format(new Date()) + ".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(imgDir, imgFileName)));//给相机应用传递值，值是sd卡上的路径，作用是：拍摄完照片后，存储到指定的路径下

        startActivityForResult(intent, 200); //启动相机进行拍照，启动拍照应用，且获得返回值

    }

    /**
     * 从目的界面返回源界面会触发执行，
     * 从sd卡上取出照片，采样后，显示到本界面的ImageView控件中
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO 处理返回的数据
        if (requestCode == 200 && resultCode == RESULT_OK) {
            //拍照成功
            Bitmap bitmap = ImageUtils.getBitmap(imgDir + File.separator + imgFileName,
                    imgView.getMeasuredWidth(),
                    imgView.getMeasuredHeight());

            if (bitmap != null)
                imgView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
