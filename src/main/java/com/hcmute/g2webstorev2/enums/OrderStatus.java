package com.hcmute.g2webstorev2.enums;

public enum OrderStatus {
    UN_PAID,    //This status only use for online payment in case when customer close the payment link
    ORDERED,
    CONFIRMED,
    PACKED,
    DELIVERING,
    DELIVERED,
    RECEIVED,
    CANCELED,
    REFUNDING,
    REFUNDED
}
