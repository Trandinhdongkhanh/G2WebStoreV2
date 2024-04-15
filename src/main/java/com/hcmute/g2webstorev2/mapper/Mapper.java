package com.hcmute.g2webstorev2.mapper;

import com.hcmute.g2webstorev2.dto.response.*;
import com.hcmute.g2webstorev2.entity.*;


public class Mapper {
    public static CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .phoneNo(customer.getPhoneNo())
                .fullName(customer.getFullName())
                .avatar(customer.getAvatar())
                .point(customer.getPoint())
                .dob(customer.getDob())
                .isEmailVerified(customer.isEmailVerified())
                .role(customer.getRole())
                .build();
    }

    public static SellerResponse toSellerResponse(Seller seller) {
        return SellerResponse.builder()
                .sellerId(seller.getSellerId())
                .email(seller.getEmail())
                .phoneNo(seller.getPhoneNo())
                .fullName(seller.getFullName())
                .avatar(seller.getAvatar())
                .isEmailVerified(seller.isEmailVerified())
                .role(seller.getRole())
                .shop(seller.getShop())
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .childCategories(category.getChildCategories())
                .build();
    }

    public static AdminResponse toAdminResponse(Admin admin) {
        return AdminResponse.builder()
                .adminId(admin.getAdminId())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .images(product.getImages())
                .description(product.getDescription())
                .price(product.getPrice())
                .specialPrice(product.getSpecialPrice())
                .stockQuantity(product.getStockQuantity())
                .shop(toShopResponse(product.getShop()))
                .category(toCategoryResponse(product.getCategory()))
                .weight(product.getWeight())
                .height(product.getHeight())
                .width(product.getWidth())
                .length(product.getLength())
                .soldQuantity(product.getSoldQuantity())
                .build();
    }

    public static ShopResponse toShopResponse(Shop shop) {
        return ShopResponse.builder()
                .shopId(shop.getShopId())
                .name(shop.getName())
                .image(shop.getImage())
                .street(shop.getStreet())
                .district(shop.getDistrict())
                .ward(shop.getWard())
                .districtId(shop.getDistrictId())
                .province(shop.getProvince())
                .build();
    }
    public static ShopCateResponse toShopCateResponse(ShopCategory shopCategory){
        return ShopCateResponse.builder()
                .shopCateId(shopCategory.getId())
                .shopId(shopCategory.getShop().getShopId())
                .childCategories(shopCategory.getChildCategories())
                .name(shopCategory.getName())
                .build();
    }

    public static VoucherResponse toVoucherResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .name(voucher.getName())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .discountType(voucher.getDiscountType())
                .voucherType(voucher.getVoucherType())
                .minSpend(voucher.getMinSpend())
                .reducePrice(voucher.getReducePrice())
                .reducePercent(voucher.getReducePercent())
                .quantity(voucher.getQuantity())
                .maxUsePerCus(voucher.getMaxUsePerCus())
                .shopId(voucher.getShop().getShopId())
                .build();
    }
    public static AddressResponse toAddressResponse(Address address){
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .orderReceiveAddress(address.getOrderReceiveAddress())
                .ward(address.getWard())
                .districtId(address.getDistrictId())
                .district(address.getDistrict())
                .province(address.getProvince())
                .customerId(address.getCustomer().getCustomerId())
                .receiverPhoneNo(address.getReceiverPhoneNo())
                .receiverName(address.getReceiverName())
                .isDefault(address.isDefault())
                .build();
    }
    public static CartItemResponse toCartItemResponse(CartItem cartItem){
        return CartItemResponse.builder()
                .name(cartItem.getName())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .productId(cartItem.getProduct().getProductId())
                .customerId(cartItem.getCustomer().getCustomerId())
                .subTotal(cartItem.getSubTotal())
                .build();
    }
    public static OrderResponse toOrderResponse(Order order){
        return OrderResponse.builder()
                .orderStatus(order.getOrderStatus())
                .createdDate(order.getCreatedDate())
                .curDate(order.getCurDate())
                .deliveredDate(order.getDeliveredDate())
                .customerId(order.getCustomer().getCustomerId())
                .shopId(order.getShop().getShopId())
                .build();
    }
}
