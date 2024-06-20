package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.ObjectDTO;

public interface SchemaBuilder<T> {

    String buildUrl(T t, String pathPreference);

    void buildLocation(T t);

}
