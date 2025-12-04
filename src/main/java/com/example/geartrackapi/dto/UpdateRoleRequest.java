package com.example.geartrackapi.dto;

import com.example.geartrackapi.dao.model.Role;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    private Role role;
}