package com.example.vending_batch.job;

import com.alibaba.fastjson.JSONObject;
import com.example.vending_batch.model.PaycoPaymentInfo;
import com.example.vending_batch.repository.PaycoPaymentInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class ItemReaderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory; // 생성자 DI 받음
    private final StepBuilderFactory stepBuilderFactory; // 생성자 DI 받음
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    @Autowired
    PaycoPaymentInfoRepository paycoPaymentInfoRepository;

    @Bean
    public Job repositoryItemReaderJob() {
        return jobBuilderFactory.get("repositoryItemReaderJob")
                .start(repositoryItemReaderStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step repositoryItemReaderStep(@Value("#{jobParameters[now]}") String dateNow) {
        log.info(dateNow);
        return stepBuilderFactory.get("repositoryItemReaderStep")
                .<PaycoPaymentInfo, PaycoPaymentInfo>chunk(chunkSize)
                .reader(repositoryItemReader())
                .processor(processor())
                .writer(jpaPagingItemWriter()) //writer에서는 리스트로 처리된 값을 받아 한번에 처리한다. chunksize만큼
                .build();
    }

    @Bean
    @StepScope

    public ItemReader<PaycoPaymentInfo> repositoryItemReader() {
        RepositoryItemReader reader = new RepositoryItemReader<PaycoPaymentInfo>();
        Date date = new Date();
        List<Object> arguments = new ArrayList<>();
        arguments.add("삭제완료");
        arguments.add(date);
//        arguments.add(0L);
        log.info("cal:" + date.toString());
        reader.setRepository(paycoPaymentInfoRepository);
//        reader.setMethodName("findAllByIdGreaterThan");
        reader.setMethodName("findAllByStatusIsNotLikeAndCreateTimeBefore");
        reader.setArguments(arguments);
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        reader.setPageSize(chunkSize);
        return reader;
    }

    @Bean
    @StepScope
    public ItemWriter<PaycoPaymentInfo> jpaPagingItemWriter() {
        ItemWriter<PaycoPaymentInfo> jpaPagingItemWriter = new ItemWriter<PaycoPaymentInfo>() {
            @Override
            public void write(List<? extends PaycoPaymentInfo> list) throws Exception {
                for (PaycoPaymentInfo paycoPaymentInfo : list) {
                    System.out.println("삭제완료 ");
                }
            }
        };
        return jpaPagingItemWriter;
    }

    @Bean("processor")
    @StepScope
    public ItemProcessor<PaycoPaymentInfo, PaycoPaymentInfo> processor() {
        return paycoPaymentInfo -> {
            JSONObject paycoPaymentInfoJsonStr = new JSONObject();

            String sellerKey = paycoPaymentInfo.getSystemUserPaymentInfo().getPaycoSellerKey();
            String autoPaymentCertifyKey = paycoPaymentInfo.getPaycoOrderCertifyKey();
            String sellerAutoPaymentReferenceKey = paycoPaymentInfo.getSystemUserPaymentInfo().getPaycoSellerAutoPaymentReferenceKey();
            paycoPaymentInfoJsonStr.put("sellerKey", sellerKey);
            paycoPaymentInfoJsonStr.put("autoPaymentCertifyKey", autoPaymentCertifyKey);
            paycoPaymentInfoJsonStr.put("sellerAutoPaymentReferenceKey", sellerAutoPaymentReferenceKey);
            //{
            //   "createTime": 1573578579000,
            //   "deviceId": 200002,
            //   "id": 31,
            //   "orderNo": "643844862046642176",
            //   "paycoOrderCertifyKey": "G2jMlutHCqgxbvsPkRSVSBBWDbx9YAAja7UvWmXS2rL4wCC",
            //   "paycoOrderNo": "201911122002493073",
            //   "paycoOrderProductNo": "201911122002670968",
            //   "paycoSellerOrderProductReferencekey": 21,
            //   "productId": "PROD_AUTO",
            //   "productPaymentAmt": 3000.0,
            //   "status": "결제완료"
            //}
            log.info(paycoPaymentInfoJsonStr.toJSONString());
            // String paymentdeleteResult = HttpUtils.executePost(PaycoUtils.domainName+"/autoPayment_cancel", paycoPaymentInfoJsonStr.toJSONString());
            // JSONObject jsonFromPaymentCancle = JSONObject.parseObject(paymentdeleteResult);


            // if (jsonFromPaymentCancle.getInteger("code") == 9999) {
            //     log.info("자동결제 삭제 실패, strFromPaymentdelete: " + paymentdeleteResult + " paycoPaymentInfo: " + paycoPaymentInfoJsonStr);
            //     //wsMsgService.sendPayFailMsg(Constants.APP_TYPE_PURCHASE, userId, purchaseId, "페이코 자동결제 삭제 실패, strFromPaymentdelete: " + strFromPaymentdelete + " paymentReserveInfo: " + paymentReserveInfo);
            // }
            return paycoPaymentInfo;
        };
    }
}
