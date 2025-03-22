package com.mit.rma_web_application.repository;

import com.mit.rma_web_application.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.deletedAt IS NULL")
    List<Request> findAllActiveRequests();
}