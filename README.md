# 바둑을 게임으로! '나도 9단'  

<div align="center">
<img width="80%" alt="스크린샷 2023-10-27 오전 9 50 06" src="https://github.com/ryuahnee/metoo9dan_web/assets/135402890/84c4adb7-b55f-4662-b6c9-063e70a88479">
</div>

<br>

## 🖥️ 프로젝트 소개

**‘나도 9단’** 은 포스트코로나 이후 비대면 교육 서비스가 활발해짐에 따라 온라인으로도 편리하게 학원 및 학교 방과 후 학습을 진행을 돕기 위해 개발한 서비스 입니다.
-  교육자와 일반회원인 경우 게임콘텐츠를 구매할 수 있으며, 게임콘텐츠에는 바둑교육자료가 포함되어 있습니다.
-  교육자는 결제를 진행하여 학습그룹을 생성할 수 있습니다.
-  학습자들은 교육자가 생성한 학습그룹에 가입하여 교육자가 배포하는 숙제를 제출할 수 있습니다.
-  교육자는 학습자가 제출한 숙제를 평가합니다.

<br>

## 🗓️ 프로젝트 상세

* 기간 : 2023.09.19 ~ 10.24
* 인원 : 5명 (기술팀장)
* 분류 : Spring Boot 기반 팀 프로젝트
* 언어 : Java(JDK 17), Spring Boot, Javascrip, JPA, Ajax, HTML5/CSS3
* 서버 : Apache Tomcat 9.0
* DB : MariaDB(10.11.2)
* API & library : Spring Security, KaKao Pay API, Scheduler API, Mail API, Thymeleaf

<br>

## 💡 주요 역할 
- Chart.js - 일,월 별 매출조회 
- Kakao pay API 연동 
- Session Storage 사용 장바구니 구현
- Docker MariaDB서버 구축 
- DB 설계  

---

## ❕신경쓴 부분
* ‘여러건 동시 결제’ DB저장 방법
*  ### 실제 쇼핑몰에서 다중선택 후 일괄결제시 저장 방법에 대해 자문을 구해 결제번호는 PK로, 주문번호는 해당 결제건의 상품들의 주문번호를 통일하여 구분하도록 하였습니다. 

  <img width="384" alt="image" src="https://github.com/ryuahnee/metoo9dan_web/assets/135402890/e5421abd-4022-417c-9c48-e29056303e65">
  <img width="613" alt="image" src="https://github.com/ryuahnee/metoo9dan_web/assets/135402890/55de5d4c-4652-4168-9897-89e8344ff873">

<pre><code>
  @Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    // 가장 큰 OrderNumber값 가져옴
    @Query("SELECT MAX(p.orderNumber) FROM Payments p")
    Integer findMaxOrderNumber();
</code></pre>

<pre><code>
  // OrderNumber의 가장 큰 값을 가져온다.
  @Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final GameService gameService;
    private EntityManager entityManager;
  
    public int generateOrderNumber() {
        Integer maxOrderNumber = paymentsRepository.findMaxOrderNumber();
        if (maxOrderNumber == null) {
            // 주문 번호를 생성할 데이터가 없다면 1로 시작
            return 1;
        } else {
            // 가장 큰 주문 번호를 찾아서 1을 더함
            int newOrderNumber = maxOrderNumber + 1;
            return newOrderNumber;
        }
    }
</code></pre>

* 장바구니 기능 구현 

<pre><code>
    // 체크 박스가 선택된 경우
      if (checkbox.checked) {
          var gameContentNo = checkbox.value;
          totalItemsElement.textContent = selectedGameContentNos.length;
          total += salePrice;

            if (totalItemsElement) {
              totalItemsElement.textContent = selectedGameContentNos.length;
           }

          // 선택된 게임 콘텐츠 번호와 salePrice를 배열에 추가하고 세션 스토리지에 저장
          if (!selectedGameContentNos.includes(gameContentNo)) {
              selectedGameContentNos.push(gameContentNo);
              selectedSalePrices.push(salePrice);
              sessionStorage.setItem("selectedGameContentNos", JSON.stringify(selectedGameContentNos));
              sessionStorage.setItem("selectedSalePrices", JSON.stringify(selectedSalePrices));
          }
      } else {
          var gameContentNo = checkbox.value;
          total -= salePrice;

          // 선택 해제된 게임 콘텐츠 번호와 salePrice를 배열에서 제거하고 세션 스토리지를 업데이트
          selectedGameContentNos = selectedGameContentNos.filter(function (value) {
              return value !== gameContentNo;
          });

          var salePriceIndex = selectedGameContentNos.indexOf(gameContentNo);
          if (salePriceIndex !== -1) {
              selectedSalePrices.splice(salePriceIndex, 1);
          }
  
          sessionStorage.setItem("selectedGameContentNos", JSON.stringify(selectedGameContentNos));
          sessionStorage.setItem("selectedSalePrices", JSON.stringify(selectedSalePrices));
      }

</code></pre>

#### ‣ [커밋히스토리 보러가기](https://github.com/ryuahnee/metoo9dan_web/commits/master)

---

