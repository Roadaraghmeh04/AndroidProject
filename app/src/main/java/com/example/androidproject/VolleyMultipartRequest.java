package com.example.androidproject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;

    private Map<String, String> mParams = new HashMap<>();
    private Map<String, DataPart> mByteData = new HashMap<>();

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    public void setParams(Map<String, String> params) {
        this.mParams = params;
    }

    public void setByteData(Map<String, DataPart> byteData) {
        this.mByteData = byteData;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return new HashMap<>();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // إضافة البارامترات النصية
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                buildTextPart(bos, entry.getKey(), entry.getValue());
            }

            // إضافة البيانات الثنائية (الملف)
            for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                buildDataPart(bos, entry.getValue(), entry.getKey());
            }

            // نهاية الملف
            bos.write(footer.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";
    private final String footer = twoHyphens + boundary + twoHyphens + lineEnd;

    private void buildTextPart(ByteArrayOutputStream bos, String paramName, String value) throws IOException {
        String part = twoHyphens + boundary + lineEnd +
                "Content-Disposition: form-data; name=\"" + paramName + "\"" + lineEnd +
                "Content-Type: text/plain; charset=UTF-8" + lineEnd +
                lineEnd +
                value + lineEnd;
        bos.write(part.getBytes("UTF-8"));
    }

    private void buildDataPart(ByteArrayOutputStream bos, DataPart dataFile, String inputName) throws IOException {
        String partHeader = twoHyphens + boundary + lineEnd +
                "Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd +
                "Content-Type: " + dataFile.getType() + lineEnd +
                lineEnd;
        bos.write(partHeader.getBytes("UTF-8"));

        bos.write(dataFile.getContent());
        bos.write(lineEnd.getBytes());
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String name, byte[] data) {
            this(name, data, "application/octet-stream");
        }

        public DataPart(String name, byte[] data, String type) {
            this.fileName = name;
            this.content = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
