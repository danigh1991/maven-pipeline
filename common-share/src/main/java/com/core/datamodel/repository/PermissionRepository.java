package com.core.datamodel.repository;


import com.core.datamodel.model.dbmodel.Activity;
import com.core.datamodel.model.dbmodel.Permission;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PermissionRepository extends BaseRepository<Permission, Long> {

	@Query("select a from Permission a  where a.id =:permissionId")
	Optional<Permission> findByEntityId(@Param("permissionId") Long permissionId);


	@Query("select a from Permission a order by a.name")
	List<Permission> findAllPermissions();


	@Query( value = "SELECT distinct p.* FROM sc_permission p \n" +
			"INNER JOIN sc_activity_permission ap ON (p.prm_id=ap.acp_prm_id)   \n" +
			"INNER JOIN sc_activity a ON (a.act_id=ap.acp_act_id)\n" +
			"INNER JOIN sc_role_activity ra ON (ra.rla_act_id=ap.acp_act_id)\n" +
			"WHERE a.act_active>0 AND  ra.rla_rol_id IN :roleIds \n",nativeQuery = true)
	List<Permission> findPermissionByRole(@Param("roleIds") List<Long> roleIds);
}
