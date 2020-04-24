package io.testproject.helpers;

import hudson.AbortException;
import io.testproject.constants.Constants;
import javax.annotation.Nonnull;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ApiHelper {

    private String apiKey;

    public ApiHelper(String key) {
        LogHelper.Debug("Initializing API helper...");
        this.apiKey = key;
    }

    public <TData> ApiResponse<TData> Post(String url, Class<TData> clazz) throws IOException {
        return Post(url, null, clazz);
    }

    public <TData> ApiResponse<TData> Post(String url, HashMap<String, Object> headers, Class<TData> clazz) throws IOException {
        return Post(url, headers, null, clazz);
    }

    public <TData> ApiResponse<TData> Post(String url, HashMap<String, Object> headers, Map<String, Object> queryParams, Class<TData> clazz) throws IOException {
        return Post(url, headers, queryParams, null, clazz);
    }

    public <TData> ApiResponse<TData> Post(String url, HashMap<String, Object> headers, Map<String, Object> queryParams, Object body, Class<TData> clazz) throws IOException {
        return execute("POST", url, headers, queryParams, body, clazz);
    }

    public <TData> ApiResponse<TData> Get(String url, Class<TData> clazz) throws IOException {
        return Get(url, null, clazz);
    }

    public <TData> ApiResponse<TData> Get(String url, HashMap<String, Object> headers, Class<TData> clazz) throws IOException {
        return Get(url, headers, null, clazz);
    }

    public <TData> ApiResponse<TData> Get(String url, HashMap<String, Object> headers, Map<String, Object> queryParams, Class<TData> clazz) throws IOException {
        return execute("GET", url, headers, queryParams, null, clazz);
    }

    public <TData> ApiResponse<TData> Put(String url, Class<TData> clazz) throws IOException {
        return Put(url, null, clazz);
    }

    public <TData> ApiResponse<TData> Put(String url, HashMap<String, Object> headers, Class<TData> clazz) throws IOException {
        return Put(url, headers, null, clazz);
    }

    public <TData> ApiResponse<TData> Put(String url, HashMap<String, Object> headers, Map<String, Object> queryParams, Class<TData> clazz) throws IOException {
        return Put(url, headers, queryParams, null, clazz);
    }

    public <TData> ApiResponse<TData> Put(String url, HashMap<String, Object> headers, Map<String, Object> queryParams, Object body, Class<TData> clazz) throws IOException {
        return execute("PUT", url, headers, queryParams, body, clazz);
    }

    private <TData> ApiResponse<TData> execute(@Nonnull String method, @Nonnull String url, HashMap<String, Object> headers, Map<String, Object> queryParams, Object body, Class<TData> clazz) throws IOException {

        HttpsURLConnection con = null;
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new AbortException("No TestProject API key is configured. Please configure a valid API key in global configuration.");
            }

            String query = generateQueryString(queryParams);

            URL uri = query.length() == 0
                    ? new URL(url)
                    : new URL(url + "?" + query);

            con = (HttpsURLConnection) uri.openConnection();

            if (method.equals("POST") || method.equals("PUT")) {
                con.setDoOutput(true);
            }

            con.setRequestMethod(method);
            if (clazz != null)
                con.setRequestProperty(Constants.AUTH_HEADER, apiKey); // Setting the authorization
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate");
            con.addRequestProperty("User-Agent", "TestProject JenkinsPlugin");

            LogHelper.Debug("Using API key: " + apiKey.substring(0,4) + "***************");

            if (headers != null) { // Adding headers if any
                for (HashMap.Entry<String, Object> header : headers.entrySet()) {
                    con.setRequestProperty(header.getKey(), header.getValue().toString());
                }
            }

            con.setConnectTimeout(Constants.DEFAULT_CONNECT_TIMEOUT);
            con.setReadTimeout(Constants.DEFAULT_READ_TIMEOUT);

            if (body != null) {
                LogHelper.Debug("Writing request body");

                if (body instanceof File) {
                    con.setFixedLengthStreamingMode(((File) body).length());

                    OutputStream out = new BufferedOutputStream(con.getOutputStream());

                    out.write(Files.readAllBytes(((File) body).toPath()));
                    out.flush();
                    out.close();
                } else {
                    con.setRequestProperty("Content-Type", "application/json");

                    OutputStreamWriter writer = null;
                    try {
                        writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
                        writer.write(SerializationHelper.ToJson(body));
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        LogHelper.Error(e);
                        throw e;
                    } finally {
                        this.closeQuietly(writer);
                    }
                }


            } else if (method.equals("POST") || method.equals("PUT")) {
                LogHelper.Debug("POST/PUT request with no body...");
                con.setFixedLengthStreamingMode(0);
            }

            LogHelper.Debug("Sending " + method.toUpperCase() + " request to: " + uri.toString());
            int status = con.getResponseCode();

            String requestId = con.getHeaderField("requestId");
            LogHelper.Debug(String.format("Response from TestProject: %d [requestId: %s]", status, requestId));

            return new ApiResponse<>(con, clazz);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                LogHelper.Error(e);
            } else {
                String reqId = con != null
                        ? con.getHeaderField("requestId")
                        : "N/A";
                LogHelper.Info(String.format("An unknown error occurred while sending the API request [requestId: %s]", reqId));
            }

            throw e;
        } finally {
            if (con != null)
                con.disconnect();
        }
    }

    @Nonnull
    private String generateQueryString(Map<String, Object> queryParams) throws UnsupportedEncodingException {
        if (queryParams == null || queryParams.size() == 0)
            return "";

        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, Object> param : queryParams.entrySet()) {
            if (queryString.length() > 0)
                queryString.append("&");

            queryString.append(String.format("%s=%s", param.getKey(), URLEncoder.encode(param.getValue().toString(), "UTF-8")));
        }

        return queryString.toString();
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            LogHelper.Error(e);
        }
    }
}
