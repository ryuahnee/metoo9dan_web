package com.idukbaduk.metoo9dan.payments.service;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Member;
import com.idukbaduk.metoo9dan.common.entity.Payments;
import com.idukbaduk.metoo9dan.game.service.GameService;
import com.idukbaduk.metoo9dan.member.repository.MemberRepository;
import com.idukbaduk.metoo9dan.payments.exception.PaymentFailedException;
import com.idukbaduk.metoo9dan.payments.repository.PaymentsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final GameService gameService;
    private final MemberRepository memberRepository;
    private EntityManager entityManager;

    // 구매한 게임컨텐츠에 대한 정보를 가져온다.
    public Page<Payments> paymentsList(Integer memberNo,int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("paymentDate"));     //등록일순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return paymentsRepository.findByMemberMemberNo(memberNo, pageable);
    }

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

    @Transactional
    public void processPayment(List<GameContents> selectedGameContents, Member member, String paymentMethod) throws PaymentFailedException {
        int orderNumber = generateOrderNumber();

        for (GameContents gameContents : selectedGameContents) {
            // 예외 처리: paymentMethod가 비어있으면 PaymentFailedException 발생
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                throw new PaymentFailedException("Payment method is empty or null");
            }

            Payments payments = new Payments(orderNumber, member.getTel(), paymentMethod,
                    LocalDateTime.now(), "complete", gameContents.getSalePrice(),
                    member.getName(), gameContents, member);

            paymentsRepository.save(payments);
        }

        // 모든 결제 처리 후 멤버십 상태 변경
        member.setMembershipStatus("유료회원");
        memberRepository.save(member);
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

        return paymentsRepository.getMonthlyTotalAmounts(startDate, endDate, pageable);
    }

    // 월 별 조회 후 매출 합계
    public List<Object[]> getMonthlySummaries(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentsRepository.getMonthlyTotalAmounts(startDate, endDate);
    }

    // 일별 데이터 조회 및 페이지네이션 처리
    public Page<Object[]> getDailyPayments(LocalDateTime startDate, LocalDateTime endDate, int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("paymentDate"));   //오름차순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        // paymentsRepository를 사용하여 일별 데이터 조회
        return paymentsRepository.getDailyPaymentsWithSummary(startDate, endDate, pageable);
    }

    // 일 별 조회 후 매출 합계
    public List<Object[]> getDailySummaries(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentsRepository.getDailyPaymentsWithSummary(startDate, endDate);
    }

    public List<Object[]> getFormattedDailySummaries(LocalDateTime startDate, LocalDateTime endDate, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Object[]> dailyPaymentsPage = paymentsRepository.getDailyPaymentsWithSummary(startDate, endDate, pageable);

        List<Object[]> dailySummaries = dailyPaymentsPage.getContent();

        List<Object[]> formattedSummaries = new ArrayList<>();
        for (Object[] summary : dailySummaries) {
            Payments payment = (Payments) summary[0];
            LocalDateTime paymentDate = payment.getPaymentDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = paymentDate.format(formatter);

            summary[0] = formattedDate;
            formattedSummaries.add(summary);
        }

        return formattedSummaries;
    }

    public List<Object[]> getMonthlySummaries(LocalDateTime startDate, LocalDateTime endDate, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Object[]> monthlyPaymentsPage = paymentsRepository.getMonthlyTotalAmounts(startDate, endDate, pageable);

        return monthlyPaymentsPage.getContent();
    }

// 월별 매출조회 합계
    public List<Object[]> getMonthlyTotalAmounts(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentsRepository.getMonthlyTotalAmounts(startDate, endDate);
    }

    // 해당 월 상세 조회
    public Page<Payments> getMonthlyPayments(int month,int page){

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("paymentDate"));   //오름차순
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        return paymentsRepository.getMonthlyPayments(month, pageable);
    }

}
