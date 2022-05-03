package com.kawahedukasi.user.models;

import com.kawahedukasi.user.utils.CreatedAndUpdatedModel;
import lombok.*;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class User extends CreatedAndUpdatedModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "userSequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="userSequence")
    @Column(name = "user_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @Column(name = "user_code", nullable = false, columnDefinition = "varchar(255) default ''")
    private String userCode = "";

    @Column(name = "login_name", nullable = false, columnDefinition = "varchar(255) default ''")
    private String loginName = "";

    @Column(name = "password", nullable = false, columnDefinition = "varchar(255) default ''")
    private String password = "";

    @Column(name = "full_name", nullable = false, columnDefinition = "varchar(255) default ''")
    private String fullName = "";

    @Column(name = "nick_name", nullable = false, columnDefinition = "varchar(100) default ''")
    private String nickName = "";

    @Column(name = "date_of_birth", nullable = false, columnDefinition = "timestamp")
    private LocalDate dateOfBirth = LocalDate.now();

    @Column(name = "place_of_birth", nullable = false, columnDefinition = "varchar(50) default ''")
    private String placeOfBirth = "";

    @Column(name = "email", nullable = false, columnDefinition = "varchar(100) default ''")
    private String email = "";

    @Column(name = "phone_number", nullable = false, columnDefinition = "varchar(50) default ''")
    private String phoneNumber = "";

    @Column(name = "id_card_number", nullable = false, columnDefinition = "varchar(50) default ''")
    private String idCardNumber = "";

    @Column(name = "id_card_type", nullable = false, columnDefinition = "varchar(50) default ''")
    private String idCardType = "";

    @Column(name = "id_card_type_id", nullable = false, columnDefinition = "bigint default -99")
    private Long idCardTypeId = -99L;

    @Column(name = "nationality", nullable = false, columnDefinition = "varchar(50) default ''")
    private String nationality = "";

    @Column(name = "nationality_id", nullable = false, columnDefinition = "bigint default -99")
    private Long nationalityId = -99L;

    @Column(name = "verified_status", nullable = false, columnDefinition = "varchar(50) default ''")
    private String verifiedStatus = "";

    @Column(name = "verified_status_id", nullable = false, columnDefinition = "bigint default -99")
    private Long verifiedStatusId = -99L;


}
