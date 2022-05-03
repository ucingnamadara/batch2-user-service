package com.kawahedukasi.user.models;

import com.kawahedukasi.user.utils.CreatedAndUpdatedModel;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_setting")
public class UserSetting extends CreatedAndUpdatedModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "userSettingSeq", sequenceName = "user_setting_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSettingSeq")
    @Column(name = "user_setting_id", nullable = false)
    private Long userSettingId;
}
