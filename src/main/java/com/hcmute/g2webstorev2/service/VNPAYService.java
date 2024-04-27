package com.hcmute.g2webstorev2.service;

import com.hcmute.g2webstorev2.dto.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;

public interface VNPAYService {
    PaymentResponse createPayment(HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException;
}
