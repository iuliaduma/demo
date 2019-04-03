package com.example.demo.mapping;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenericModelMapper<E> extends ModelMapper {

    public List map(List<E> list) {

        if (!CollectionUtils.isEmpty(list)){
            Type type = new TypeToken<List<E>>(){
            }.getType();
            return super.map(list, type);
        }
        return new ArrayList<>();
    }
}
