package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.TargetType;
import com.core.model.repository.BaseRepository;

public interface TargetTypeRepository extends BaseRepository<TargetType, Long> {
    TargetType findByName(String name);
}
