package com.cladware.repositories;

import com.cladware.entities.CladwareUser;
import org.springframework.data.repository.CrudRepository;

public interface CladwareUserRepository extends CrudRepository<CladwareUser, String> {
}
