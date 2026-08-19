package com.dream.sanbong.service;

import com.dream.sanbong.dto.PostRequest;
import com.dream.sanbong.dto.PostResponse;

import java.util.List;

public interface PostService {

    List<PostResponse> layDanhSachBaiViet();

    PostResponse layChiTietBaiViet(Long id);

    void taoBaiViet(String userEmail, PostRequest request);

    void capNhatBaiViet(Long id, PostRequest request);

    void xoaBaiViet(Long id);
}