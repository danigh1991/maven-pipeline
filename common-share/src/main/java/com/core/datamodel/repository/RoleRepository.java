package com.core.datamodel.repository;

import com.core.datamodel.model.dbmodel.Role;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface RoleRepository extends BaseRepository<Role, Long> {

    @Query("select r from Role r left join r.activities where r.id =:roleId")
    Optional<Role> findByEntityId(@Param("roleId") Long roleId);
	Role findByRoleName(String roleName);

	@Query("select r from Role r order by r.roleName")
	List<Role> findAllRoles();

	@Query("select r from Role r where r.active=true and r.defaultRole=true")
	List<Role> findDefaultRole();

	@Query( value = "SELECT COUNT(*) FROM sc_roles r \n" +
			"inner join sc_user_roles ur ON (r.rol_id=ur.url_rol_id) \n" +
			"WHERE r.rol_name=:roleName AND ur.url_usr_id=:userId",nativeQuery = true)
	Integer userHasRole(@Param("userId") Long userId,@Param("roleName") String roleName);

	@Query("SELECT COUNT(r) FROM Role r WHERE FUNCTION('REPLACE', r.roleName,' ','')=FUNCTION('REPLACE', :name,' ','')")
	Integer countByName(@Param("name") String name);

	@Query("SELECT COUNT(r) FROM Role r WHERE r.id<>:roleId  and FUNCTION('REPLACE', r.roleName,' ','')=FUNCTION('REPLACE', :name,' ','')")
	Integer countByName(@Param("roleId") Long roleId,@Param("name") String name);
}
