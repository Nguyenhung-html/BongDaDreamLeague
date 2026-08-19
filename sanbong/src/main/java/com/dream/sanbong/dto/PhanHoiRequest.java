package com.dream.sanbong.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class PhanHoiRequest {
    private String noiDungPhanHoi;
    private UUID nguoiPhanHoiId; // ID của Staff/Admin thực hiện phản hồi
}