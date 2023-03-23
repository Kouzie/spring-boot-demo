package com.example.vending_batch.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class PaycoPaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNo;
    private Long deviceId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "system_user_id")
    private SystemUserPaymentInfo systemUserPaymentInfo;

    private String paycoOrderNo;
    private String paycoOrderCertifyKey;
    private String paycoOrderProductNo;
    private Long paycoSellerOrderProductReferencekey;
    private String productId;
    private Float productPaymentAmt;
    private String status;

    @CreationTimestamp
    private Date createTime;
    @UpdateTimestamp
    private Date updateTime;

    @Override
    public String toString() {
        return "PaycoPaymentInfo{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", deviceId=" + deviceId +
                ", paycoOrderNo='" + paycoOrderNo + '\'' +
                ", paycoOrderCertifyKey='" + paycoOrderCertifyKey + '\'' +
                ", sellerKey=" + systemUserPaymentInfo.getPaycoSellerKey() + '\'' +
                ", paycoOrderProductNo='" + paycoOrderProductNo + '\'' +
                ", paycoSellerOrderProductReferencekey=" + paycoSellerOrderProductReferencekey +
                ", productId='" + productId + '\'' +
                ", productPaymentAmt=" + productPaymentAmt +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
