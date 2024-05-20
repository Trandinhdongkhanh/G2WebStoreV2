package com.hcmute.g2webstorev2.mapper;

import com.hcmute.g2webstorev2.dto.response.*;
import com.hcmute.g2webstorev2.entity.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Mapper {
    public static CustomerResponse toCustomerResponse(Customer customer) {
        String avatar = null;
        if (customer.getAvatar() != null) avatar = customer.getAvatar().getFileUrl();
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .phoneNo(customer.getPhoneNo())
                .fullName(customer.getFullName())
                .avatar(avatar)
                .point(customer.getPoint())
                .dob(customer.getDob())
                .isEmailVerified(customer.isEmailVerified())
                .role(customer.getRole())
                .build();
    }

    public static SellersFromShopResponse toSellersFromShopResponse(Seller seller) {
        return SellersFromShopResponse.builder()
                .sellerId(seller.getSellerId())
                .shopId(seller.getShop().getShopId())
                .email(seller.getEmail())
                .role(seller.getRole().getAppRole())
                .roleId(seller.getRole().getRoleId())
                .isEnabled(seller.isEnabled())
                .isEmailVerified(seller.isEmailVerified())
                .isMainAcc(seller.isMainAcc())
                .build();
    }

    public static SellerResponse toSellerResponse(Seller seller) {
        String avatar = null;
        if (seller.getAvatar() != null) avatar = seller.getAvatar().getFileUrl();
        return SellerResponse.builder()
                .sellerId(seller.getSellerId())
                .email(seller.getEmail())
                .phoneNo(seller.getPhoneNo())
                .fullName(seller.getFullName())
                .avatar(avatar)
                .isEmailVerified(seller.isEmailVerified())
                .role(seller.getRole())
                .shop(Mapper.toShopResponse(seller.getShop()))
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .childCategories(category.getChildCategories())
                .path(category.getPath())
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
        List<GCPFileResponse> images = null;
        if (product.getImages() != null)
            images = product.getImages().stream().map(Mapper::toGCPFileResponse).collect(Collectors.toList());
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .images(images)
                .description(product.getDescription())
                .price(product.getPrice())
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

    public static GCPFileResponse toGCPFileResponse(GCPFile gcpFile) {
        return GCPFileResponse.builder()
                .id(gcpFile.getId())
                .fileName(gcpFile.getFileName())
                .fileType(gcpFile.getFileType())
                .fileUrl(gcpFile.getFileUrl())
                .build();
    }

    public static ShopResponse toShopResponse(Shop shop) {
        String image = null;
        if (shop.getImage() != null) image = shop.getImage().getFileUrl();
        return ShopResponse.builder()
                .shopId(shop.getShopId())
                .name(shop.getName())
                .image(image)
                .street(shop.getStreet())
                .district(shop.getDistrict())
                .ward(shop.getWard())
                .districtId(shop.getDistrictId())
                .province(shop.getProvince())
                .balance(shop.getBalance())
                .build();
    }

    public static ShopCateResponse toShopCateResponse(ShopCategory shopCategory) {
        List<ShopCateResponse> childCategories = null;

        if (shopCategory.getChildCategories() != null)
            childCategories = shopCategory.getChildCategories()
                    .stream().map(Mapper::toShopCateResponse)
                    .collect(Collectors.toList());

        return ShopCateResponse.builder()
                .shopCateId(shopCategory.getId())
                .shopId(shopCategory.getShop().getShopId())
                .childCategories(childCategories)
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

    public static AddressResponse toAddressResponse(Address address) {
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

    public static CartItemResponse toCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .images(cartItem.getProduct().getImages().get(0).getFileUrl())
                .name(cartItem.getProduct().getName())
                .price(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .productId(cartItem.getProduct().getProductId())
                .customerId(cartItem.getCustomer().getCustomerId())
                .shopId(cartItem.getProduct().getShop().getShopId())
                .subTotal(cartItem.getSubTotal())
                .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .createdDate(order.getCreatedDate())
                .payedDate(order.getPayedDate())
                .deliveredDate(order.getDeliveredDate())
                .customerId(order.getCustomer().getCustomerId())
                .shopId(order.getShop().getShopId())
                .items(order.getOrderItems()
                        .stream().map(Mapper::toOrderItemResponse)
                        .collect(Collectors.toList()))
                .shopName(order.getShop().getName())
                .paymentType(order.getPaymentType())
                .feeShip(order.getFeeShip())
                .g2VoucherPriceReduce(order.getG2VoucherPriceReduce())
                .shopVoucherPriceReduce(order.getShopVoucherPriceReduce())
                .pointSpent(order.getPointSpent())
                .total(order.getTotal())
                .address(Mapper.toAddressResponse(order.getAddress()))
                .build();
    }

    public static ReviewResponse toReviewResponse(Review review) {
        List<GCPFileResponse> files = null;
        if (review.getFiles() != null)
            files = review.getFiles().stream().map(Mapper::toGCPFileResponse).collect(Collectors.toList());
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .files(files)
                .rate(review.getRate())
                .customerName(review.getCustomer().getFullName())
                .productId(review.getProduct().getProductId())
                .shopFeedBack(review.getShopFeedBack())
                .build();
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .itemId(orderItem.getItemId())
                .image(orderItem.getImage())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .name(orderItem.getName())
                .orderId(orderItem.getOrder().getOrderId())
                .productId(orderItem.getProductId())
                .subTotal(orderItem.getQuantity() * orderItem.getPrice())
                .isReviewed(orderItem.isReviewed())
                .build();
    }

    public static CartItemV2Res toCartItemv2Res(CartItemV2 cartItemV2) {
        Set<ShopItemRes> shopItemResSet = null;
        Set<VoucherResponse> voucherResponseSet = null;
        if (cartItemV2.getShopItems() != null)
            shopItemResSet = cartItemV2.getShopItems().stream().map(Mapper::toShopItemRes).collect(Collectors.toSet());
        if (cartItemV2.getVouchers() != null)
            voucherResponseSet = cartItemV2.getVouchers().stream().map(Mapper::toVoucherResponse).collect(Collectors.toSet());
        return CartItemV2Res.builder()
                .cartItemId(cartItemV2.getCartItemId())
                .customerId(cartItemV2.getCustomer().getCustomerId())
                .shopName(cartItemV2.getShop().getName())
                .shopId(cartItemV2.getShop().getShopId())
                .shopItems(shopItemResSet)
                .vouchers(voucherResponseSet)
                .shopSubTotal(cartItemV2.getShopSubTotal())
                .build();
    }
    public static ShopItemRes toShopItemRes(ShopItem shopItem){
        return ShopItemRes.builder()
                .shopItemId(shopItem.getShopItemId())
                .cartItemV2Id(shopItem.getCartItemV2().getCartItemId())
                .price(Long.valueOf(shopItem.getProduct().getPrice()))
                .name(shopItem.getProduct().getName())
                .quantity(shopItem.getQuantity())
                .productId(shopItem.getProduct().getProductId())
                .subTotal(shopItem.getSubTotal())
                .build();
    }
}
