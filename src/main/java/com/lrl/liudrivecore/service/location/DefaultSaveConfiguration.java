package com.lrl.liudrivecore.service.location;

public class DefaultSaveConfiguration extends SaveConfiguration {

    private static DefaultSaveConfiguration instance = new DefaultSaveConfiguration();;

    public DefaultSaveConfiguration() {

        this.method = "local";
        this.compressed = 0;
    }

    public static DefaultSaveConfiguration getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "DefaultSaveConfiguration{" +
                "method='" + method + '\'' +
                ", compressed=" + compressed +
                ", token='" + token + '\'' +
                '}';
    }
}