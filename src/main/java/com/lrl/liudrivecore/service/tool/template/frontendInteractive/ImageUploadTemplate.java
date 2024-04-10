package com.lrl.liudrivecore.service.tool.template.frontendInteractive;

import com.lrl.liudrivecore.service.tool.template.ImageFileBase64;

public class ImageUploadTemplate extends MediaMetaTemplateBase64 {
    public ImageFileBase64 getImageFile(){
        ImageFileBase64 imageFile = new ImageFileBase64();
        imageFile.setFilename(filename);
        imageFile.setData(data);
        imageFile.setAccessibility(accessibility);
        imageFile.setType(type);
        imageFile.setTag(tag);
        imageFile.setUserId(userId);
        imageFile.setScale(scale);
        imageFile.setAuthor(author);

        return imageFile;
    }

    @Override
    public String toString() {
        return "ImageUploadTemplate{" +
                "filename='" + filename + '\'' +
                ", userId='" + userId + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                ", accessibility=" + accessibility +
                ", tag=" + tag +
                ", scale=" + scale +
                ", author='" + author + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
