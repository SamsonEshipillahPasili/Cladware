package com.cladware.repositories;

import com.cladware.entities.CladwareOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<CladwareOrder, Long> {
}
