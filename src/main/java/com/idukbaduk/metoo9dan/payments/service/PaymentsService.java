package com.idukbaduk.metoo9dan.payments.service;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Payments;
import com.idukbaduk.metoo9dan.game.reprository.GameRepository;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.payments.reprository.PaymentsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final GameService gameService;
    private EntityManager entityManager;



    // 구매한 게임컨텐츠에 대한 정보를 가져온다.

    public Page<Payments> paymentsList(Integer memberNo,int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("paymentDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return paymentsRepository.findByMemberMemberNo(memberNo, pageable);
    }


// OrderNumber의 가장 큰 값을 가져온다.

    // OrderNumber의 가장 큰 값을 가져온다.
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


    //결제하기
    public void save(List<GameContents> selectedGameContents, Member member, String paymentMethod){
        int orderNumber =  generateOrderNumber();

        for(GameContents gameContents: selectedGameContents){
            Payments payments = new Payments();
            payments.setOrderNumber(orderNumber);
            payments.setContact(member.getTel());
            payments.setMethod(paymentMethod);
            payments.setPaymentDate(LocalDateTime.now());
            payments.setStatus("complete");     //complete,waiting,refund
            payments.setAmount(gameContents.getSalePrice());
            payments.setDepositorName(member.getName());
            payments.setGameContents(gameContents);
            payments.setMember(member);
            paymentsRepository.save(payments);
        }
    }

    public GameContents getGameContentsForPayment(Payments payment) {
        GameContents gameContents = gameService.getGameContents(payment.getGameContents().getGameContentNo());
        return gameContents;
    }

    //목록조회 (페이징처리)
    public Page<Payments> getPay(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("creationDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return paymentsRepository.findAll(pageable);
    }

    //paymentNo 값으로 해당 결제내역의 상세정보를 가져옴
    public Payments getPayment(Integer paymentNo) {

        Optional<Payments> byId = paymentsRepository.findById(paymentNo);

        Payments payments = byId.get();
        return payments;
    }



    // 월 별 데이터 조회 및 페이지네이션 처리
    public Page<Object[]> getMonthlyTotalAmounts(LocalDateTime startDate, LocalDateTime endDate, int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("paymentDate"));     //결제일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        System.out.println("startDate?-서비스"+startDate);
        System.out.println("endDate?-서비스"+endDate);

        return paymentsRepository.getMonthlyTotalAmounts(startDate, endDate, pageable);


    }

    // 일별 데이터 조회 및 페이지네이션 처리
    public Page<Object[]> getDailyPayments(LocalDateTime startDate, LocalDateTime endDate, int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("paymentDate"));   //오름차순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        // paymentsRepository를 사용하여 일별 데이터 조회
        return paymentsRepository.getDailyPaymentsWithSummary(startDate, endDate, pageable);
    }









   /* //결제 내용 저장하기 (DB)
    public void kakaosave(KakaoApproveResponse kakaoApprove){
*//*
        Payments payments = new Payments();
        payments.setOrderNumber();
        payments.setContact();
        payments.set;


        kakaoApprove.set
*//*


        paymentsRepository.save(payments);
    }
*/


}
