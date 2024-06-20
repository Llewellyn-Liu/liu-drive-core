package com.lrl.liudrivecore.data.dto.schema;

public interface SchemaValidator<T> {

    boolean isValid(T t);

}
