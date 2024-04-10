package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

public class MediaMetaTemplateBase64 extends MediaMetaTemplate{

    protected String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MediaMetaTemplateBase64{" +
                "data='" + data + '\'' +
                ", filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", tag=" + tag +
                ", scale=" + scale +
                ", author='" + author + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
