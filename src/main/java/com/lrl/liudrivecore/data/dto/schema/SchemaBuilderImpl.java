package com.lrl.liudrivecore.data.dto.schema;

import com.lrl.liudrivecore.data.dto.AbstractObjectDTO;
import com.lrl.liudrivecore.data.dto.ObjectDTO;
import com.lrl.liudrivecore.service.dir.uploadConfig.SaveConfiguration;

public abstract class SchemaBuilderImpl<T extends AbstractObjectDTO> implements SchemaBuilder<T>{
    @Override
    public String buildUrl(T abstractObjectDTO, String pathPreference) {
        String res;
        if(abstractObjectDTO.getMeta().getFilename() != null){
            res = abstractObjectDTO.getMeta().getUserId() + "/" + abstractObjectDTO.getMeta().getFilename();
        }
        else {
            res = abstractObjectDTO.getMeta().getUserId() + "/" + System.currentTimeMillis();
        }
        abstractObjectDTO.setUrl(res);
        return res;
    }


    @Override
    public void buildLocation(AbstractObjectDTO abstractObjectDTO){
        SaveConfiguration config = abstractObjectDTO.getConfig();
        String builtLocation;
        if (config == null ||config.getDrive()== null || config.getDrive().equalsIgnoreCase("default")) {
            builtLocation = "default" + ";" + abstractObjectDTO.getUrl();
        } else if (config.getDrive().equalsIgnoreCase("local")) {
            builtLocation = "local;" + abstractObjectDTO.getUrl();
        } else if (config.getDrive().equalsIgnoreCase("cloud")) {
            builtLocation = "cloud;" + config.getToken() + ";" + abstractObjectDTO.getUrl();
        } else throw new RuntimeException("URLCheck: Not a valid method");

        abstractObjectDTO.getMeta().setLocation(builtLocation);
    }
}
