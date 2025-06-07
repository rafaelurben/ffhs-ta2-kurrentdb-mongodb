/* (C) 2025 - Rafael Urben */
package ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.repository;

import ch.rafaelurben.edu.ffhs.ta2.impl1mongodb.model.ParentObjectHistory;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentObjectHistoryRepository
    extends MongoRepository<ParentObjectHistory, String> {
  Optional<ParentObjectHistory> findByParentObjectId(String parentObjectId);
}
