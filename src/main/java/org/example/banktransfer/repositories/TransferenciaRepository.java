package org.example.banktransfer.repositories;

import org.example.banktransfer.entities.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    @Query("""
         select t from Transferencia t
         join fetch t.pagador
         join fetch t.recebedor
         order by t.id asc
         """)
    List<Transferencia> findAllWithUsuarios();
}
