package com.aws.photosharing.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_photos")
@JsonDeserialize
@Setter
@Getter
@NoArgsConstructor
public class UserPhoto implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "tags")
    private String tags;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public UserPhoto(String userName) {
        this.id = UUID.randomUUID().toString();
        this.userName = userName;
    }

}
