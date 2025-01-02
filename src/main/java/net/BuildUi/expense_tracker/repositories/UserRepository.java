package net.BuildUi.expense_tracker.repositories;

import net.BuildUi.expense_tracker.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserInfo , String> {
    UserInfo findByUsername(String username);
}
