package ipn.mobileapp.model.services;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import ipn.mobileapp.model.enums.RequestType;
import ipn.mobileapp.model.enums.Servlets;
import ipn.mobileapp.R;

/**
 * Created by GERARDO-LAP on 9/3/2017.
 */

public class ApacheServletRequest {
    private String scheme;
    private String host;
    private int port;
    private String path;
    private Context context;

    public ApacheServletRequest(Context context, boolean localhost) {
        this.context = context;
        int resourceId = (localhost ? R.array.localhost : R.array.server);
        String[] confValues = this.context.getResources().getStringArray(resourceId);
        scheme = confValues[0];
        host = confValues[1];
        port = Integer.parseInt(confValues[2]);
        path = confValues[3];
    }

    public URI create(Servlets servlet, Map<String, JSONObject> params) throws URISyntaxException {
        URI uri = null;
        String servletPath = context.getResources().getStringArray(R.array.servlets)[servlet.ordinal()];

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(scheme)
                .setHost(host)
                .setPort(port)
                .setPath(path + servletPath);

        for (Map.Entry map : params.entrySet()) {
            uriBuilder.addParameter(map.getKey().toString(), map.getValue().toString());
        }

        uri = uriBuilder.build();

        return uri;
    }

    public JSONObject execute(RequestType requestType, URI uri) throws JSONException, IOException {
        JSONObject result = null;
        HttpRequestBase httpRequest;
        switch (requestType) {
            case GET:
                httpRequest = new HttpGet(uri);
                break;
            case POST:
                httpRequest = new HttpPost(uri);
                break;
            case PUT:
                httpRequest = new HttpPut(uri);
                break;
            default:
                httpRequest = null;
                break;
        }

        if (httpRequest != null) {
            HttpClient client = new DefaultHttpClient();
            //HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000);
            HttpResponse httpResponse = client.execute(httpRequest);
            HttpEntity httpEntity = httpResponse.getEntity();
            String jsonResponse = EntityUtils.toString(httpEntity);
            result = new JSONObject(jsonResponse);
        }

        return result;
    }
}
