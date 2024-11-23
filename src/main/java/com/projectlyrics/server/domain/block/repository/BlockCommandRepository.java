package com.projectlyrics.server.domain.block.repository;

import com.projectlyrics.server.domain.block.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockCommandRepository extends JpaRepository<Block, Long> {

}
