package org.example.banktransfer.repositories;

import org.example.banktransfer.entities.Usuario;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpfOuCnpj(String cpfOuCnpj);

    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> findByIdForUpdate(@Param("id") Long id);
}