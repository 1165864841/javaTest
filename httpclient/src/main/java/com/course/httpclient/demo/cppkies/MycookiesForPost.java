package com.course.httpclient.demo.cppkies;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MycookiesForPost {
    private String url;
    private ResourceBundle bundle;
    private CookieStore store;

    @BeforeTest
    public void beforeTest(){
        bundle = ResourceBundle.getBundle("application", Locale.CANADA);
        url=bundle.getString("test.url");
    }

    @Test
    public void testGetCookies() throws IOException {
        String result;
        String uri=bundle.getString("getCookies.uri");
        String testurl=this.url+uri;
        HttpGet get=new HttpGet(testurl);
        DefaultHttpClient client=new DefaultHttpClient();
        HttpResponse respone=client.execute(get);
        result = EntityUtils.toString(respone.getEntity(),"utf-8");
        System.out.println(result);

        //获取Cookies信息
        this.store = client.getCookieStore();
        List<Cookie> cookieList = store.getCookies();

        for (Cookie cookie : cookieList){
            String name = cookie.getName();
            String value = cookie.getValue();
            System.out.println("cookie name = " + name
                    + ";  cookie value = " + value);
        }
    }

    @Test(dependsOnMethods = {"testGetCookies"})
    public void PostWithCookies() throws IOException {
        String uri=bundle.getString("post.uri");
        //拼接测试地址
        String TestUrl=this.url+uri;

        //申明clien对象，用来方法的执行
        DefaultHttpClient client = new DefaultHttpClient();
        //申明一个方法，该方法为Post请求
        HttpPost post = new HttpPost(TestUrl);
        //添加参数
        JSONObject param = new JSONObject();
        param.put("name","zhangsan");
        param.put("age","18");

        //设置请求头信息 设置header
        post.setHeader("content-type","application/json");

        //将参数信息添加到方法中
        StringEntity entity = new StringEntity(param.toString(),"utf-8");
        post.setEntity(entity);


        //申明对象进行响应结果的储存
        String result;

        //添加Cookies信息
        client.setCookieStore(this.store);

        //执行Post方法
        HttpResponse response = client.execute(post);


        //获取响应结果
        result=EntityUtils.toString(response.getEntity(),"utf-8");
        System.out.println(result);

        //处理结果，就是判断返回结果是否符合预期
        //将返回的响应结果字符串转化成为json对象
        JSONObject Resultjson=new JSONObject(result);

        //获取到结果值
        String success= (String)Resultjson.get("zhangsan");
        String  status= (String)Resultjson.get("status");

        //具体判断返回结果的值
        Assert.assertEquals("success",success);
        Assert.assertEquals("1",status);

    }


}
