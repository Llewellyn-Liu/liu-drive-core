package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

public class UserDigest {

    private Integer fileCount;

    private Integer imageCount;

    private Integer filePages;

    private Integer imagePages;

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public Integer getFilePages() {
        return filePages;
    }

    public void setFilePages(Integer filePages) {
        this.filePages = filePages;
    }

    public Integer getImagePages() {
        return imagePages;
    }

    public void setImagePages(Integer imagePages) {
        this.imagePages = imagePages;
    }

    @Override
    public String toString() {
        return "UserDigest{" +
                "fileCount=" + fileCount +
                ", imageCount=" + imageCount +
                ", filePages=" + filePages +
                ", imagePages=" + imagePages +
                '}';
    }
}
