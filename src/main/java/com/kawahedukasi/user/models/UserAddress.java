package com.kawahedukasi.user.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserAddress extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "userAddressSeq", sequenceName = "user_address_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userAddressSeq")
    @Column(name = "user_address_id", nullable = false)
    private Long userAddressId;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false ,columnDefinition = "bigint default -99")
    @JsonIgnore(value = true)
    @JsonBackReference
    private User userId;

    @Column(name = "main_address", columnDefinition = "text default ''")
    private String mainAddress = "";

    @Column(name = "country", columnDefinition = "varchar(100) default ''")
    private String country = "";

    @Column(name = "country_id", columnDefinition = "bigint default -99")
    private Long countryId = -99L;

    @Column(name = "province", columnDefinition = "varchar(100) default ''")
    private String province = "";

    @Column(name = "province_id", columnDefinition = "bigint default -99")
    private Long provinceId = -99L;

    @Column(name = "city", columnDefinition = "varchar(100) default ''")
    private String city = "";

    @Column(name = "city_id", columnDefinition = "bigint default -99")
    private Long cityId = -99L;

    @Column(name = "kecamatan", columnDefinition = "varchar(100) default ''")
    private String kecamatan = "";

    @Column(name = "kecamatan_id", columnDefinition = "bigint default -99")
    private Long kecamatanId = -99L;

    @Column(name = "kelurahan", columnDefinition = "varchar(100) default ''")
    private String kelurahan = "";

    @Column(name = "kelurahan_id", columnDefinition = "bigint default -99")
    private Long kelurahanId = -99L;

    @Column(name = "rw", columnDefinition = "varchar(50) default ''")
    private String rw = "";

    @Column(name = "rt", columnDefinition = "varchar(50) default ''")
    private String rt = "";

    @Column(name = "desc_address", columnDefinition = "text default ''")
    private String descAddress = "";

    @Column(name = "longitude", columnDefinition = "varchar(50) default ''")
    private String longitude = "";

    @Column(name = "latitude", columnDefinition = "varchar(50) default ''")
    private String latitude = "";
}
