package com.example.demo.service;

import com.example.demo.data.user.Role;
import com.example.demo.dto.user.RoleDTO;
import com.example.demo.mapping.GenericModelMapper;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final GenericModelMapper roleMapper;

    public RoleDTO findByDescriptionAndName(RoleDTO roleDTO) {
        return roleMapper.map(roleRepository.findByDescriptionAndAndName(roleDTO.getDescription(), roleDTO.getName()), RoleDTO.class);
    }

    public RoleDTO saveOrUpdate(RoleDTO roleDTO){

        Role role = roleRepository.findByDescriptionAndAndName(roleDTO.getDescription(), roleDTO.getName());

        if(role != null){
            roleDTO.setId(role.getId());
        }

        return roleMapper.map(roleRepository.save(roleMapper.map(roleDTO, Role.class)), RoleDTO.class);
    }
}
