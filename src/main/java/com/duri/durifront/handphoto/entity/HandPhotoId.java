package com.duri.durifront.handphoto.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HandPhotoId implements Serializable {

    private Long profileId;
    private String userId;
}