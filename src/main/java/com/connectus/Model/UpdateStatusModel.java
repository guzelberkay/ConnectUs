package com.connectus.Model;


import com.connectus.entity.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateStatusModel {
    private Long authId;
    private EStatus status;
}
