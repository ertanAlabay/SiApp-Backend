package com.ertanAlabay.SiApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ertanAlabay.SiApp.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {}
