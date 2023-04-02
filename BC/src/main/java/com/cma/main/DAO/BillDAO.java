package com.cma.main.DAO;

import com.cma.main.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDAO extends JpaRepository<Bill, Integer> {


    @Query("select b from Bill b order by b.id desc")
    List<Bill> getAllBills();

    @Query("select b from Bill b where b.email=:email order by b.id desc")
    List<Bill> getBillByUserName(@Param("email") String email);

    Bill findFirstByUuid(String uuid);

    Bill findFirstByEmail(String email);

    Bill findFirstById(String billId);
}
