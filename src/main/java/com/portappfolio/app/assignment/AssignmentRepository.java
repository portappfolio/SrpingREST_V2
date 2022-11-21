package com.portappfolio.app.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long> {

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 order by a.updatedAt desc")
    Collection<Assignment> findByCompanyIdAndUserInfoId(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 order by a.updatedAt desc")
    Collection<Assignment> findByCompanyId(Long companyId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.ended = false and a.rejected = false and a.enabled = false")
    Optional<Assignment> findByCompanyIdAndUserInfoIdAndPending(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.requestedByType = 'user' and a.ended = false and a.rejected = false and a.enabled = false")
    Optional<Assignment> findByCompanyIdAndUserInfoIdAndPendingUser(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.requestedByType = 'company' and a.ended = false and a.rejected = false and a.enabled = false")
    Optional<Assignment> findByCompanyIdAndUserInfoIdAndPendingCompany(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.requestedByType = 'user' and a.rejected = true order by a.updatedAt desc ")
    Collection<Assignment> findByCompanyIdAndUserInfoIdAndRejectedUser(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.requestedByType = 'company' and a.rejected = true order by a.updatedAt desc ")
    Collection<Assignment> findByCompanyIdAndUserInfoIdAndRejectedCompany(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.rejected = true order by a.updatedAt desc ")
    Collection<Assignment> findByCompanyIdAndUserInfoIdAndRejected(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.enabled = true")
    Optional<Assignment> findByCompanyIdAndUserInfoIdAndEnabled(Long companyId, Long userInfoId);

    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.userInfo.id = ?2 and a.ended = true order by a.updatedAt desc")
    Collection<Assignment> findByCompanyIdAndUserInfoIdAndEnded(Long companyId, Long userInfoId);

    //Para admind de empresa que pueda aprobar nuevas solicitudes o manejar las vigentes
    @Query(value = "select a from Assignment a where a.company.id = ?1 and a.ended = false and a.rejected = false order by a.updatedAt desc")
    Collection<Assignment> findByCompanyIdAndNotRejectedAndNotEnded(Long companyId);

    //Para listar las que ve el usuario
    //Ultima modificación eliminar de la quiery: a.requestedByType = 'company' and
    @Query(value = "select a from Assignment a where a.userInfo.id = ?1 and (a.enabled = true or (a.ended = false and a.rejected = false and a.enabled = false))")
    Collection<Assignment> getAssignmentsByUserInfoId(Long userInfo);

    //TODO: Rol Admin
    //Para listar las que ve la empresa
    @Query(value = "select a from Assignment a where a.company.id = ?1 and (a.enabled = true or (a.requestedByType = 'user' and a.ended = false and a.rejected = false and a.enabled = false))")
    Collection<Assignment> getAssignmentsByCompanyId(Long companyId);



}
