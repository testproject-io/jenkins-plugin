package io.testproject.model;

public class UploadLinkData {
    /**
     * The URL to upload the apk/ipa file to
     */
    private String url;
    /**
     * Request method: GET, POST, PUT, etc.
     */
    private MethodData method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MethodData getMethod() {
        return method;
    }

    public void setMethod(MethodData method) {
        this.method = method;
    }
}
