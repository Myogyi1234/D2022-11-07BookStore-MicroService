package com.example.transporation.dao;

import com.example.transporation.ds.TransportInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportInfoDao extends JpaRepository<TransportInfo,Integer> {
}
