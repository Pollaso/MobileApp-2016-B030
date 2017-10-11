package ipn.mobileapp.model.service;

import android.content.Context;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import javax.net.ssl.SSLContext;

import ipn.mobileapp.debug.DebugMode;
import ipn.mobileapp.model.pojo.Document;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.R;

public class OkHttpServletRequest {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String scheme;
    private String host;
    private int port;
    private Context context;

    public OkHttpServletRequest(Context context) {
        this.context = context;
        int resourceId = (DebugMode.ON ? R.array.localhost : R.array.server);
        String[] confValues = this.context.getResources().getStringArray(resourceId);
        scheme = confValues[0];
        host = confValues[1];
        port = Integer.parseInt(confValues[2]);
    }

    public Request buildRequest(Servlets servlet, RequestType requestType, Map<String, String> params) {
        HttpUrl url = null;
        String servletPath = context.getResources().getStringArray(R.array.servlets)[servlet.getValue()];

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder().scheme(scheme).host(host).port(port).addPathSegment(servletPath);
        RequestBody body = null;
        if (requestType.equals(RequestType.GET) || requestType.equals(RequestType.DELETE)) {
            for (Map.Entry map : params.entrySet()) {
                urlBuilder.addQueryParameter(map.getKey().toString(), map.getValue().toString());
            }
        } else {
            FormBody.Builder builder = new FormBody.Builder();

            for (Map.Entry map : params.entrySet()) {
                builder.add(map.getKey().toString(), map.getValue().toString());
            }
            body = builder.build();
        }
        url = urlBuilder.build();

        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (requestType == RequestType.DELETE)
            requestBuilder.delete();

        if (body != null) {
            switch (requestType) {
                case POST:
                    requestBuilder.post(body);
                    break;
                case PUT:
                    requestBuilder.put(body);
                    break;
            }
        }
        return requestBuilder.build();
    }

    public String buildUrl(Servlets servlet) {
        String servletPath = context.getResources().getStringArray(R.array.servlets)[servlet.getValue()];
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder().scheme(scheme).host(host).port(port).addPathSegment(servletPath);
        HttpUrl url = urlBuilder.build();
        return url.toString();
    }

    public Request buildRequest(Servlets servlet, RequestType requestType, Map<String, String> params, File file) {
        String servletPath = context.getResources().getStringArray(R.array.servlets)[servlet.getValue()];

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder().scheme(scheme).host(host).port(port).addPathSegment(servletPath);
        HttpUrl url = urlBuilder.build();

        okhttp3.MultipartBody.Builder multipartBodyBuilder = new okhttp3.MultipartBody.Builder();
        multipartBodyBuilder.setType(okhttp3.MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        MediaType type = MediaType.parse(getMimeType(file.getPath()));
        multipartBodyBuilder.addFormDataPart(
                "file",
                file.getName(),
                okhttp3.RequestBody.create(type, file)
        );
        Request.Builder requestBuilder = new Request.Builder().url(url);
        return requestBuilder.post(multipartBodyBuilder.build()).build();
    }

    public OkHttpClient buildClient() {
        OkHttpClient client = null;

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            client = new OkHttpClient().newBuilder().followRedirects(false).sslSocketFactory(sslContext.getSocketFactory()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return client;
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

}