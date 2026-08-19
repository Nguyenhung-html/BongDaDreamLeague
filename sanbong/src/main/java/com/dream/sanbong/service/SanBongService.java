package com.dream.sanbong.service;

import com.dream.sanbong.dto.SanBongPhanHoi;
import java.util.List;
import java.util.UUID;

public interface SanBongService {
    List<SanBongPhanHoi> layTatCaSan();
    SanBongPhanHoi layChiTietSan(UUID sanId);
}
