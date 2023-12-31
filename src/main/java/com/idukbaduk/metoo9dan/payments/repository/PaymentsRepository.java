package com.idukbaduk.metoo9dan.payments.repository;

import com.idukbaduk.metoo9dan.common.entity.GameContents;
import com.idukbaduk.metoo9dan.common.entity.Payments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

    // 가장 큰 OrderNumber값 가져옴
    @Query("SELECT MAX(p.orderNumber) FROM Payments p")
    Integer findMaxOrderNumber();

    Page<Payments> findByMemberMemberNo(Integer memberNo, Pageable pageable);

    //paymentNo로 게임 값 가져오기
    GameContents findGameContentsByPaymentNo(Integer paymentNo);

    //해당월로 Payments 정보조회
    @Query("SELECT p FROM Payments p WHERE MONTH(p.paymentDate) = :month")
    Page<Payments> getMonthlyPayments(@Param("month") int month, Pageable pageable);

    // 월별 매출조회
    @Query("SELECT MONTH(p.paymentDate) AS month, COUNT(p) AS transactionCount, SUM(p.amount) AS totalAmount " +
            "FROM Payments p " +
            "WHERE p.paymentDate >= :startDate AND p.paymentDate <= :endDate " +
            "GROUP BY MONTH(p.paymentDate)")
    Page<Object[]> getMonthlyTotalAmounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,Pageable pageable);


// 월별 매출조회 합계
    @Query("SELECT MONTH(p.paymentDate) AS month, COUNT(p) AS transactionCount, SUM(p.amount) AS totalAmount " +
            "FROM Payments p " +
            "WHERE p.paymentDate >= :startDate AND p.paymentDate <= :endDate " +
            "GROUP BY MONTH(p.paymentDate)")
    List<Object[]> getMonthlyTotalAmounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    //일별 매출조회
    @Query("SELECT p, COUNT(p) as paymentCount, SUM(p.amount) as totalAmount " +
            "FROM Payments p " +
            "WHERE DATE(p.paymentDate) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY DATE(p.paymentDate)")
    Page<Object[]> getDailyPaymentsWithSummary(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    //일별 매출 합계
    @Query("SELECT p, COUNT(p) as paymentCount, SUM(p.amount) as totalAmount " +
            "FROM Payments p " +
            "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.paymentDate")
    List<Object[]> getDailyPaymentsWithSummary(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    //MemNo있는지 찾기 : 유무료 회원 구분 YJ 추가
    boolean existsByMemberMemberNo(Integer memberNo);

    //회원 결제횟수 찾기 : YJ 추가
    int countPaymentNoByMemberMemberNo(Integer memberNo);

}
