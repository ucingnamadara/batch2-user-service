package com.kawahedukasi.user.utils;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@MappedSuperclass
public abstract class CreatedAndUpdatedModel extends PanacheEntityBase {
    @Column(name = "created_datetime", columnDefinition = "timestamp")
    private LocalDateTime createdDateTime;

    @Column(name = "updated_datetime", columnDefinition = "timestamp")
    private LocalDateTime updatedDateTime;

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    @PrePersist
    private void setCreatedDateTime(){
        if(createdDateTime == null){
            this.createdDateTime = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
            this.updatedDateTime = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
        }
    }

    @PreUpdate
    private void setUpdatedDateTime(){
        this.updatedDateTime = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
    }
}
