package com.kawahedukasi.user.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kawahedukasi.user.utils.CreatedAndUpdatedModel;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_otp")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserOtp extends CreatedAndUpdatedModel implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "userOtpSeq", sequenceName = "user_otp_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userOtpSeq")
    @Column(name = "user_otp_id", nullable = false)
    private Long userOtpId;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "bigint default -99")
    @JsonIgnore(value = true)
    @JsonBackReference
    private User userId;

    @Column(name = "user_otp", columnDefinition = "varchar(255) default ''")
    private String userOtp = "";
}
