package io.testproject.helpers;

import io.testproject.model.ApiErrorResponseData;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * API Response wrapper
 *
 * @param <TData> The type of the response data
 */
public class ApiResponse<TData> {

    private int statusCode;
    private String message;
    private String requestId;
    private TData data;
    private ApiErrorResponseData error;

    private final Class<TData> myType;

    ApiResponse(HttpURLConnection con, Class<TData> clazz) {
        this.myType = clazz;

        parseResponse(con);
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode <= 299;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getError() {
        return error != null ? error.getError() : "";
    }

    public TData getData() {
        return data;
    }

    public boolean hasData() {
        return data != null;
    }

    public String generateErrorMessage(String prefix) {
        return prefix + (statusCode > 0 ? " - " + statusCode : "") + (message != null ? " - " + message : "") + (requestId != null ? " [" + requestId  + "]": "");
    }

    private void parseResponse(@Nonnull HttpURLConnection con) {
        try {
            statusCode = con.getResponseCode();
            requestId = con.getHeaderField("RequestId");

            String content = getContent(con);

            if (statusCode >= 200 && statusCode <= 299) {
                if (content != null) {
                    Class<TData> clazz = myType != null ? myType : (Class<TData>) void.class;
                    data = SerializationHelper.FromJson(content, clazz);
                }
            } else if (statusCode == 401) {
                error = new ApiErrorResponseData("Unauthorized");

            } else {
                String messageHeader = con.getHeaderField("Message");

                if (messageHeader != null) {
                    message = messageHeader;
                }

                if (content != null) {
                    error = SerializationHelper.FromJson(content, ApiErrorResponseData.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String getContent(@Nonnull HttpURLConnection con) throws IOException {

        // Getting response stream
        int responseCode = con.getResponseCode();
        InputStream inputStream = null;
        try {
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }

            String encoding = con.getContentEncoding();
            if (encoding != null)
                LogHelper.Debug("Content encoding: " + encoding);

            if (encoding != null && "gzip".equals(encoding)) {
                inputStream = new GZIPInputStream(inputStream);
            }

            if (inputStream == null)
                return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            in.close();

            return response.toString();

        } catch (IOException | NullPointerException e) {
            LogHelper.Error(e);
        }

        return inputStream != null ? IOUtils.toString(inputStream, StandardCharsets.UTF_8) : null;
    }
}
