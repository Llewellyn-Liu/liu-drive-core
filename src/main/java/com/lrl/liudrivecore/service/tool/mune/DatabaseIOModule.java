package com.lrl.liudrivecore.service.tool.mune;

public enum DatabaseIOModule {

    IMAGE("image"),
    OBJECT_FILE("object"),
    MEMO("memo"),
    DEFAULT("default");

    String moduleName;

    DatabaseIOModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

}
