package io.testproject.model;

public class FileNameData {

    /**
     * The name of the apk/ipa file
     */
    private final String fileName;

    public String getFileName() {
        return fileName;
    }

    public FileNameData(String fileName) {
        this.fileName = fileName;
    }
}
