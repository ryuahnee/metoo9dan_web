package com.idukbaduk.metoo9dan.payments.kakaopay;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;



@SpringBootTest
@Transactional
class KakaoPayServiceTest {


        static String cid = "TC0ONETIME"; // 가맹점 테스트 코드
        static String admin_Key = "952a7b3082d015dcec08556a07b94b78"; // 공개 조심! 본인 애플리케이션의 어드민 키를 넣어주세요

        @Autowired
        private KakaoReadyResponse kakaoReady;

        @Test
        public void kakaoPayReady() {

            // 카카오페이 요청 양식
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("partner_order_id", "가맹점 주문 번호");
            parameters.add("partner_user_id", "가맹점 회원 ID");
            parameters.add("item_name", "상품명");
            parameters.add("quantity", "1" );
            parameters.add("total_amount", "200");
            parameters.add("vat_amount", "300");
            parameters.add("tax_free_amount", "0");
            parameters.add("approval_url", "http://localhost/payments/success"); // 성공 시 redirect url
            parameters.add("cancel_url", "http://localhost/payments/payments/fail"); // 취소 시 redirect url
            parameters.add("fail_url", "http://localhost/payments/cancel"); // 실패 시 redirect url

            // 파라미터, 헤더
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            // 외부에 보낼 url
            RestTemplate restTemplate = new RestTemplate();

            kakaoReady = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/ready",
                    requestEntity,
                    KakaoReadyResponse.class);
            System.out.println("requestEntity?:"+requestEntity);

            System.out.println("kakaoReady" + kakaoReady);
        }
        /**
         * 결제 완료 승인
         */
        public void approveResponse(String pgToken) {

            // 카카오 요청
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("cid", cid);
            parameters.add("tid", kakaoReady.getTid());
            parameters.add("partner_order_id", "가맹점 주문 번호");
            parameters.add("partner_user_id", "가맹점 회원 ID");
            parameters.add("pg_token", pgToken);

            // 파라미터, 헤더
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

            // 외부에 보낼 url
            RestTemplate restTemplate = new RestTemplate();

            KakaoApproveResponse approveResponse = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/approve",
                    requestEntity,
                    KakaoApproveResponse.class);
            System.out.println("approveResponse? :"+approveResponse);

        }

        /**
         * 카카오 요구 헤더값
         */
        private void getHeaders() {
            HttpHeaders httpHeaders = new HttpHeaders();

            String auth = "KakaoAK " + admin_Key;

            httpHeaders.set("Authorization", auth);
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");


            System.out.println("httpHeaders:" +httpHeaders);
        }
    }


