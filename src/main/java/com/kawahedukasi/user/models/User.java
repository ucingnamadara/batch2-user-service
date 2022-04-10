package com.kawahedukasi.user.models;

import com.kawahedukasi.user.utils.CreatedAndUpdatedModel;
import lombok.*;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class User extends CreatedAndUpdatedModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "userSequence", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="userSequence")
    @Column(name = "user_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @Column(name = "user_code", nullable = false, columnDefinition = "varchar(255) default ''")
    private String userCode = "";

    @Column(name = "login_name", nullable = false, columnDefinition = "varchar(255) default ''")
    private String loginName = "";

    @Column(name = "full_name", nullable = false, columnDefinition = "varchar(255) default ''")
    private String fullName = "";

    @Column(name = "password", nullable = false, columnDefinition = "varchar(255) default ''")
    private String password = "";

    @Column(name = "email", nullable = false, columnDefinition = "varchar(100) default ''")
    private String email = "";

    @Column(name = "phone_number", nullable = false, columnDefinition = "varchar(50) default ''")
    private String phoneNumber = "";

    @Column(name = "address", nullable = false, columnDefinition = "text default ''")
    private String address = "";

    @Column(name = "area", nullable = false, columnDefinition = "varchar(50) default ''")
    private String area = "";

    @Column(name = "area_id", nullable = false, columnDefinition = "bigint default -99")
    private Long areaId = -99L;

}
