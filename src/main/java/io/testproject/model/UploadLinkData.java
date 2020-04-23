package io.testproject.model;

public class UploadLinkData {
    /**
     * The URL to upload the apk/ipa file to
     */
    private String url;
    /**
     * Request method: GET, POST, PUT, etc.
     */
    private Method method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private class Method {
        /**
         * Request method: GET, POST, PUT, etc.
         */
        private String method;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}
