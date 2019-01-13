package io.testproject.helpers;

import io.testproject.model.ApiErrorResponse;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * API Response wrapper
 *
 * @param <TData> The type of the response data
 */
public class ApiResponse<TData> {

    private int statusCode;
    private String message;
    private TData data;
    private ApiErrorResponse error;

    private Class<TData> myType;

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

    public String getError() {
        return error != null ? error.getError() : "";
    }

    public TData getData() {
        return data;
    }

    public boolean hasData() {
        return data != null;
    }

    private void parseResponse(@Nonnull HttpURLConnection con) {
        try {
            statusCode = con.getResponseCode();
            String content = getContent(con);

            if (statusCode >= 200 && statusCode <= 299) {

                if (content != null) {

                    data = SerializationHelper.FromJson(content, myType);
                }
            } else if (statusCode == 401) {
                error = new ApiErrorResponse("Unauthorized");

            } else {
                String messageHeader = con.getHeaderField("Message");

                if (messageHeader != null) {
                    message = messageHeader;
                }

                if (content != null) {
                    error = SerializationHelper.FromJson(content, ApiErrorResponse.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String getContent(@Nonnull HttpURLConnection con) throws IOException {

        // Getting response stream
        InputStream inputStream = null;
        try {
            inputStream = con.getInputStream();
        } catch (IOException ioe) {
            LogHelper.Error(ioe);
        }

        return inputStream != null ? IOUtils.toString(inputStream) : null;
    }
}
