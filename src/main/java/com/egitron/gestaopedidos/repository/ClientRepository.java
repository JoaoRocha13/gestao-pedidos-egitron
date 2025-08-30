package com.egitron.gestaopedidos.repository;

import com.egitron.gestaopedidos.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
