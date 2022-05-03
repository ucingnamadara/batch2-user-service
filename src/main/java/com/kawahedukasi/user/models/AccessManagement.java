package com.kawahedukasi.user.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kawahedukasi.user.types.StringArrayType;
import com.kawahedukasi.user.utils.CreatedAndUpdatedModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "access_management")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@TypeDef(name = "StringArrayType", typeClass = StringArrayType.class)
public class AccessManagement extends CreatedAndUpdatedModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "accessManagementSeq", sequenceName = "access_management_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accessManagementSeq")
    @Column(name = "access_management_id", nullable = false)
    private Long accessManagementId;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "user_id", columnDefinition = "bigint default -99")
    @JsonIgnore(value = true)
    private User userId;

    @Column(name = "role", nullable = false, columnDefinition = "varchar(50) default ''")
    private String role = "";

    @Column(name = "role_id", nullable = false, columnDefinition = "bigint default -99")
    private Long roleId = -99L;

    @Column(name = "access_role", nullable = false, columnDefinition = "text[]")
    @Type(type = "StringArrayType")
    private String[] accessRole = new String[0];

    @Column(name = "access", nullable = false, columnDefinition = "text[]")
    @Type(type = "StringArrayType")
    private String[] access = new String[0];
}
