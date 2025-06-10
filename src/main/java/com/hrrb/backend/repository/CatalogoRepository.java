package com.hrrb.backend.repository;

import com.hrrb.backend.model.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long>{

}
