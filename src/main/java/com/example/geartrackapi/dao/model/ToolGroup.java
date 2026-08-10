package com.example.geartrackapi.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "tool_groups")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ToolGroup extends OrganizationalEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "toolGroup", fetch = FetchType.LAZY)
    private List<Tool> tools;
}
