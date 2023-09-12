package com.core.datamodel.repository;


import com.core.datamodel.model.dbmodel.Activity;
import com.core.datamodel.model.dbmodel.FileManagerAction;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface FileManagerActionRepository extends BaseRepository<FileManagerAction, Long> {
	
    @Query(value = "SELECT distinct fma.* FROM sc_user_roles ur INNER JOIN sc_roles r on (ur.url_rol_id=r.rol_id) INNER JOIN sc_fm_action_role far on (r.rol_id=far.fmr_rol_id) INNER JOIN sc_fm_action fma on (far.fmr_fma_id=fma.fma_id) WHERE ur.url_usr_id=?1",nativeQuery = true)
	List<FileManagerAction> findUserActions(Long userId);


    @Query("select f from FileManagerAction f order by f.name")
    List<FileManagerAction> findFileManagerActions();


}
