package cc.emw.mobile.asr;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

public class UploadFile {
    public static Runnable uploadFile(final String url, final String isNew,
                                      final String save, final String exp, final File uploadfile, final Handler handler) {
        Runnable run = new Runnable() {
            /* String result = null;
             String rs = "";
             String reCode = "";*/
            String info = "";

            @Override
            public void run() {
                // TODO Auto-generated method stub
                SSLSocketFactory.getSocketFactory().setHostnameVerifier(
                        new AllowAllHostnameVerifier());
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                FileBody file = new FileBody(uploadfile);
                MultipartEntity emEntity = new MultipartEntity();
                try {// 封装所需数据，包括文件和所需的Strig 数据
                    emEntity.addPart("File", file);
                    emEntity.addPart("isNew",
                            new StringBody(isNew, Charset.forName("utf-8")));
                    emEntity.addPart("save",
                            new StringBody(save, Charset.forName("utf-8")));
                    emEntity.addPart("exp",
                            new StringBody(exp, Charset.forName("utf-8")));
                    httpPost.setEntity(emEntity);
                    Log.i("TAG", "post总字节数:" + emEntity.getContentLength());
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    int code = httpResponse.getStatusLine().getStatusCode();
                    if (code == 200) {
                        /*result = EntityUtils.toString(httpResponse.getEntity());
                        JSONObject jsonObject = new JSONObject(result);
                        rs = jsonObject.getString("info");
                        reCode = jsonObject.getString("code");*/
                        info = "上传成功";
                    } else {
                        //rs = "暂时无法认证，请稍后再试";
                        info = "上传失败";
                    }
                    /*AuthenticationBean authen = new AuthenticationBean();
                    authen.info = rs;
                    authen.code = reCode;*/
                    Message message = handler.obtainMessage();
                    //message.obj = authen;
                    message.obj = info;
                    handler.sendMessage(message);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } /*catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            }
        };
        return run;
    }

    public static Runnable httpUpload(final String url, final String isNew,
                                      final String save, final String exp, final File uploadfile, final Handler handler) {
        Runnable run = new Runnable() {
            String info = "";

            @Override
            public void run() {
                String uuid = UUID.randomUUID().toString();
                String BOUNDARY = uuid;
                String NewLine = "\r\n";
                String spec = url;
                try {
                    File file = uploadfile;
                    FileInputStream fis = null;
                    DataOutputStream bos = null;
                    DataInputStream bis = null;
                    URL url = new URL(spec);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //打开输出
                    connection.setDoOutput(true);
                    //打开输入
                    connection.setDoInput(true);
                    //关闭缓存
                    connection.setUseCaches(false);
                    //读取超时
                    connection.setReadTimeout(50 * 1000);
                    //连接超时
                    connection.setConnectTimeout(5 * 1000);
                    //请求方式POST
                    connection.setRequestMethod("POST");
                    //设置请求头
                    // 必须设置，数据类型，编码方式，分界线
                    connection.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + BOUNDARY);
                    connection.setChunkedStreamingMode(1024 * 50);
                    bos = new DataOutputStream(connection.getOutputStream());
                    if (file.exists()) {
                        fis = new FileInputStream(file);
                        byte[] buff = new byte[1024];
                        bis = new DataInputStream(fis);
                        int cnt = 0;
                        //数据以--BOUNDARY开始
                        bos.write(("--" + BOUNDARY).getBytes());
                        //换行
                        bos.write(NewLine.getBytes());
                        //内容描述信息
                        String content = "Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"";
                        bos.write(content.getBytes());
                        bos.write(NewLine.getBytes());//
                        bos.write(NewLine.getBytes());
                        //空一行后，开始通过流传输文件数据
                        while ((cnt = bis.read(buff)) != -1) {
                            bos.write(buff, 0, cnt);
                        }
                        bos.write(NewLine.getBytes());


                        //结束标志--BOUNDARY--
                        bos.write(("--" + BOUNDARY + "--").getBytes());
                        bos.write(NewLine.getBytes());
                        bos.flush();
                    }
                    //开始发送请求，获取请求码和请求结果
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        info = "上传成功";
                    } else {
                        info = "上传失败";
                    }
                    Message message = handler.obtainMessage();
                    //message.obj = authen;
                    message.obj = info;
                    handler.sendMessage(message);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        };
        return run;
    }


    public static Runnable httpClientUpload(final String url, final String isNew,
                                            final String save, final String exp, final File uploadfile, final Handler handler) {
        Runnable run = new Runnable() {
            String info = "";

            @Override
            public void run() {
                String uuid = UUID.randomUUID().toString();
                String BOUNDARY = uuid;
                String NewLine = "\r\n";
                String spec = url;
                try {
                    HttpEntity entity = null;
                    HttpPost httpPostUpLoadFile = new HttpPost(spec);
                    //这里拼接了一个Http请求的请求头 就是我们上边分析的 Accept-Charset: GBK,utf-8 Content-Type:multipart/form-data; boundary=
                    httpPostUpLoadFile.addHeader("Accept", "application/json");
                    // boundary可以是任意字符,但是必须和MultipartEntity的boundary相同,否则就会报错
                    httpPostUpLoadFile.addHeader("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                    // 与header的boundary一致,否则报错
                    MultipartEntity multiEntity = new MultipartEntity(HttpMultipartMode.STRICT, BOUNDARY, Charset.forName(HTTP.UTF_8));

                    //拼接params参数 在Client包中称作StringBody请求体 其实就是Content—Type为text/plan的字段 new Stringbody对应的请求头内容
                    //Content-Disposition: form-data;name="desc"
                    //Content-Type: text/plain; charset=UTF-8

                    multiEntity.addPart("isNew", new StringBody(isNew, Charset.forName(HTTP.UTF_8)));
                    multiEntity.addPart("save", new StringBody(save, Charset.forName(HTTP.UTF_8)));
                    multiEntity.addPart("exp", new StringBody(exp, Charset.forName(HTTP.UTF_8)));
                    //这里是上传文件
                    File file = uploadfile;
                    //拼接一个文件的请求体 在client中被称为FileBody 也就是Content-Type为application/octet-stream的请求体

                    //Content-Disposition: form-data;name="pic"; filename="photo.jpg"
                    //Content-Type: application/octet-stream
                    //Content-Transfer-Encoding: binary
                    ContentBody cbFile = new FileBody(file);

                    //addpart的两个参数一个是filename 一个是file 请求体
                    multiEntity.addPart("outfile", cbFile);
                    //接下来就是发送请求 这里不再赘述了

                    httpPostUpLoadFile.setEntity(multiEntity);
                    // 创建客户端
                    HttpClient httpClient = new DefaultHttpClient();
                    // 执行请求获得响应
                    HttpResponse response = httpClient.execute(httpPostUpLoadFile);
                    //开始发送请求，获取请求码和请求结果
                    if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                        info = "上传成功";
                    } else {
                        info = "上传失败";
                    }
                    Message message = handler.obtainMessage();
                    //message.obj = authen;
                    message.obj = info;
                    handler.sendMessage(message);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        };
        return run;

    }
}