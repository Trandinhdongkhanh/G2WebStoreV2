package com.hcmute.g2webstorev2.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UnixTimestampConverter {
    public static LocalDateTime covert(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
}
