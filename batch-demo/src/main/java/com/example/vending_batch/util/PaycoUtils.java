package com.example.vending_batch.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaycoUtils {
    public static String sellerKey = "S0FSJE";            // 가맹점 코드 - 파트너센터에서 알려주는 값으로, 초기 연동 시 PAYCO에서 쇼핑몰에 값을 전달한다.
    public static String cpId = "PARTNERTEST_AUTO";    // 상점ID
    public static String productId = "PROD_AUTO";            // 상품ID
    public static String logYn = "Y";                    // 로그Y/N
    public static String PREFIX = "PAYCO";

    @Value("${server.type}")
    public static String serverType;                // 서버유형. DEV:개발, REAL:운영

    @Value("${server.type}")
    public void setServerType(String serverType) {
        PaycoUtils.serverType = serverType;
    }

    //도메인명 or 서버IP
    @Value("https://" + "${server.domain}" + "/vending_web/payment")
    public static String domainName;

    @Value("https://" + "${server.domain}" + "/vending_web/payment")
    public void setDomainName(String domainName) {
        PaycoUtils.domainName = domainName;
    }

    // 자동 결제 예약 페이지 URL주소
    public static String PAYCO_RESERVE_URL = "/outseller/autoPayment/reserve";

    // 자동 결제 페이지 URL주소
    public static String PAYCO_AUTOPAYMENT_UTL = "/outseller/autoPayment/payment";

    public static String autoPayment_cancel(
            String sellerKey,
            String autoPaymentCertifyKey,
            String sellerAutoPaymentReferenceKey) {

        ObjectMapper mapper = new ObjectMapper();                    //jackson json object
        AutoPayment_Util util = new AutoPayment_Util(serverType);


        /* 설정한 취소요청 정보로 Json String 을 작성합니다. */
        Map<String, Object> cancelInfo = new HashMap<String, Object>();
        cancelInfo.put("sellerKey", sellerKey);                                            //[필수]판매자Key
        cancelInfo.put("sellerAutoPaymentReferenceKey", sellerAutoPaymentReferenceKey); //[필수]외부가맹점의 자동결제 관리번호
        cancelInfo.put("autoPaymentCertifyKey", autoPaymentCertifyKey);                //[필수]자동결제 인증 키


        //자동결제 삭제 API 호출 함수
        String result = util.autoPayment_cancel(cancelInfo, logYn);
        return result;
    }
}

