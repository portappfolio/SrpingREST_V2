package com.portappfolio.app.assignment;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserRepository;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.role.Role;
import com.portappfolio.app.appUser.role.RoleService;
import com.portappfolio.app.appUser.role.Roles;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.assignment.Role.AssignmentRole;
import com.portappfolio.app.assignment.Role.AssignmentRoleRepository;
import com.portappfolio.app.assignment.Role.AssignmentRoleService;
import com.portappfolio.app.company.Company;
import com.portappfolio.app.company.CompanyService;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.security.config.SessionService;
import com.portappfolio.app.security.config.UserClassSecurity;
import com.portappfolio.app.security.config.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentRoleService assignmentRoleService;
    private final SessionService sessionService;
    private final AppUserService appUserService;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final JwtProvider jwtProvider;
    private final AppUserRepository appUserRepository;


    public ResponseEntity<?> atCreateCompany(Company company){

        //Se obtiene sesion
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Usuario no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha completado información del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida no se hayan creado asignaciones previamente pues significaría no sería creación de la empresa
        Collection<Assignment> assignments = assignmentRepository.findByCompanyId(company.getId());
        if (!assignments.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Empresa ya ha sido previamente creada.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Assignment assignment = new Assignment(
                appUser.get().getUserInfo()
                , company
                , appUser.get().getUserInfo()
                , appUser.get().getUserInfo()
                , RequestedByType.admin
        );

        //Se asignan roles admin coorespondientes
        Collection<AssignmentRole> assignmentRoles = new ArrayList<>();
        assignmentRoles.add(assignmentRoleService.getByName(Roles.ADMIN_COMPANY).get());
        assignmentRoles.add(assignmentRoleService.getByName(Roles.ADMIN_PAYMENTS).get());
        assignmentRoles.add(assignmentRoleService.getByName(Roles.ADMIN_PEOPLE).get());
        assignmentRoles.add(assignmentRoleService.getByName(Roles.ADMIN_PERMISIONS).get());
        assignmentRoles.add(assignmentRoleService.getByName(Roles.ADMIN_DASHBOARD).get());

        assignment.setRoles(assignmentRoles);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Empresa creada exitosamente.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data( Map.of("assignment",assignmentRepository.save(assignment)))
                        .build()
                , HttpStatus.OK
        );
    }

    public ResponseEntity<?> sendRequestUserToCompany(Long companyId, AppUser appUser){

        //Se obtiene empresa
        Optional<Company> company = companyService.getById(companyId);

        if (company.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Datos Errados.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que el usuario no haya sido rechazado
        Collection<Assignment> assignments = assignmentRepository.findByCompanyIdAndUserInfoIdAndRejectedUser(companyId,appUser.getUserInfo().getId());
        if (!assignments.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Solicitud rechazada previamente, couniquese con el administrador de la empresa a la cual intenta asociarse.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que no tenga una asignación enabled
        Optional<Assignment> assignmentEnabled = assignmentRepository.findByCompanyIdAndUserInfoIdAndEnabled(companyId,appUser.getUserInfo().getId());
        if (assignmentEnabled.isPresent()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario ya se encuentra habilitado en dicha empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())

                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que no tenga solicitudes pendientes por parte de la empresa, si las tiene se aprueban
        Optional<Assignment> assignmentRequestedCompany = assignmentRepository.findByCompanyIdAndUserInfoIdAndPendingCompany(companyId,appUser.getUserInfo().getId());
        if(assignmentRequestedCompany.isPresent()){
            Assignment assignment = assignmentRequestedCompany.get();
            assignment.setEnabled(true);
            assignment.setApprovedBy(assignment.getRequestedBy());
            assignment.setUpdatedAt(LocalDateTime.now());

            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Vinculación exitosa.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of("assignment",assignmentRepository.save(assignment)))
                            .build()
                    , HttpStatus.OK
            );
        }

        //Se valida que no tenga solicitudes pendientes por parte del usuario porque de lo contrario no se va a crear otra
        Optional<Assignment> assignmentRequestedUser = assignmentRepository.findByCompanyIdAndUserInfoIdAndPending(companyId,appUser.getUserInfo().getId());
        if (assignmentRequestedUser.isPresent()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Ya tienes una solicitud de vinculación pendiente por aprobación de la empresa, debes esperar a que sea aprobada.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }
        //Se crea asignación despues de validar todos los criterios
        Assignment assignment = new Assignment(appUser.getUserInfo(),company.get(),appUser.getUserInfo(),RequestedByType.user);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Solicitud de vinculación creada con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of("assignment",assignmentRepository.save(assignment)))
                        .build()
                , HttpStatus.OK
        );

    }

    //Para empresa que solicita a usuario unirse
    //Si tiene solicitud vigente por parte del usuario aprobarla
    //No tener solicitudes pendientes por parte de la empresa
    //TODO: (Rol Admin Requerido)
    public ResponseEntity<?> applyCompany(UserInfo userInfo){
        //Se valida userinfo ya esté persistido
        if (userInfo.getId() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Usuario no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se obtiene sesion
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Usuario no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getCurrentAssignment() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Debe iniciar sesión en la empresa sobre la cual desea vincular otro usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha completado la información del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que no tenga una asignación enabled
        Optional<Assignment> assignmentEnabled = assignmentRepository.findByCompanyIdAndUserInfoIdAndEnabled(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        if (assignmentEnabled.isPresent()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario ya se encuentra habilitado en dicha empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())

                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que no tenga solicitudes pendientes por parte del usuario, si las tiene se aprueban
        Optional<Assignment> assignmentRequestedUser = assignmentRepository.findByCompanyIdAndUserInfoIdAndPendingUser(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        if(assignmentRequestedUser.isPresent()){
            Assignment assignment = assignmentRequestedUser.get();
            assignment.setEnabled(true);
            assignment.setApprovedBy(appUser.get().getUserInfo());
            assignment.setUpdatedAt(LocalDateTime.now());
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Vinculación exitosa.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of(
                                    "assignment",assignmentRepository.save(assignment)
                            ))
                            .build()
                    , HttpStatus.OK
            );
        }

        //Se valida que no tenga solicitudes pendientes por parte de la empresa porque de lo contrario no se va a crear otra
        Optional<Assignment> assignmentRequestedCompany = assignmentRepository.findByCompanyIdAndUserInfoIdAndPending(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        if (assignmentRequestedCompany.isPresent()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Ya tienes una solicitud de vinculación pendiente por aprobación del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }
        //Se crea asignación despues de validar todos los criterios
        Assignment assignment = new Assignment(
                userInfo
                ,appUser.get().getCurrentAssignment().getCompany()
                ,appUser.get().getUserInfo()
                ,RequestedByType.company
        );
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Solicitud de vinculación creada con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "assignment",assignmentRepository.save(assignment)
                        ))
                        .build()
                , HttpStatus.OK
        );
    }

    //Para empresa que aprueba solicitud a un usuario
    //Debe tener solicitud vigente
    //Solicitud debio haber sido creada por usuario
    // TODO: (Rol Admin Requerido)
    public ResponseEntity<?> enableAssignmentCompany(UserInfo userInfo){
        //Se obtiene sesion
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Usuario no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getCurrentAssignment() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Debe iniciar sesión en la empresa sobre la cual desea vincular otro usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha completado la información del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que no tenga una asignación enabled
        Optional<Assignment> assignmentEnabled = assignmentRepository.findByCompanyIdAndUserInfoIdAndEnabled(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        if (assignmentEnabled.isPresent()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario ya se encuentra habilitado en dicha empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())

                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que tenga solicitudes pendientes por parte del usuario
        Optional<Assignment> assignmentRequestedUser = assignmentRepository.findByCompanyIdAndUserInfoIdAndPendingUser(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        //Si el usuario no ha solicitado unirse a la empresa
        if (assignmentRequestedUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario no ha solicitado unirse a la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Assignment assignment = assignmentRequestedUser.get();
        assignment.setEnabled(true);
        assignment.setApprovedBy(appUser.get().getUserInfo());
        assignment.setUpdatedAt(LocalDateTime.now());
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Vinculación exitosa.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "assignment",assignmentRepository.save(assignment)
                        ))
                        .build()
                , HttpStatus.OK
        );
    }

    //Para empresa que rechaza solicitud usuario
    //Solicitud debe ser creada por usuario
    //Rechazar solicitud vigente
    // TODO: (Rol Admin Requerido)
    public ResponseEntity<?> rejectAssignmentCompany(UserInfo userInfo){

        //Se obtiene sesion
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Usuario no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getCurrentAssignment() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Debe iniciar sesión en la empresa sobre la cual desea vincular otro usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha completado la información del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que no tenga una asignación enabled
        Optional<Assignment> assignmentEnabled = assignmentRepository.findByCompanyIdAndUserInfoIdAndEnabled(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        if (assignmentEnabled.isPresent()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario ya se encuentra habilitado en dicha empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())

                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que tenga solicitudes pendientes por parte del usuario
        Optional<Assignment> assignmentRequestedUser = assignmentRepository.findByCompanyIdAndUserInfoIdAndPendingUser(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        //Si el usuario no ha solicitado unirse a la empresa
        if (assignmentRequestedUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario no ha solicitado unirse a la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Assignment assignment = assignmentRequestedUser.get();
        assignment.setRejected(true);
        assignment.setRejectedBy(appUser.get().getUserInfo());
        assignment.setUpdatedAt(LocalDateTime.now());
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Asignación rechazada.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "assignment",assignmentRepository.save(assignment)
                        ))
                        .build()
                , HttpStatus.OK
        );
    }

    public ResponseEntity<?> endAssignmentCompany(UserInfo userInfo){
        //Se obtiene sesion
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Usuario no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getCurrentAssignment() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Debe iniciar sesión en la empresa sobre la cual desea vincular otro usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        } else if (appUser.get().getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha completado la información del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se valida que tenga una asignación enabled
        Optional<Assignment> assignmentEnabled = assignmentRepository.findByCompanyIdAndUserInfoIdAndEnabled(appUser.get().getCurrentAssignment().getCompany().getId(),userInfo.getId());
        if (assignmentEnabled.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario no cuenta con asignaciones vigentes.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Assignment assignment = assignmentEnabled.get();
        assignment.setEnded(true);
        assignment.setEndedBy(appUser.get().getUserInfo());
        assignment.setEnabled(false);
        assignment.setUpdatedAt(LocalDateTime.now());
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Asignación terminada.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "assignment",assignmentRepository.save(assignment)
                        ))
                        .build()
                , HttpStatus.OK
        );

    }

    //TODO: Rol admin
    //Pensar cómo manejar
    public Assignment makeAdmin(Assignment assignment){
        return null;
    }

    //Pensar lógica de entre admins nadie puede quitar al super admin
    //Cuando super admin quiera retirarse cómo lo puede hacer?
    public Assignment deleteAdmin(Assignment assignment){
        return null;
    }

    //Debe tener permisos de Admin o en su defecto crear empresa
    //Según el rol, debe haber pagado
    //Debe recuperar el assign
    //TODO: Debe agregar roles tanto a la asignación cómo al app user current assgnment de ser el caso
    public Assignment assignRoles(Assignment assignment, Collection<AssignmentRole> assignmentRoles){
        return null;
    }

    //Recuperar la asignación
    //Eliminar todos los roles
    //No puede quedar sin administradores
    //Persistir Asignación
    public Boolean deleteRoles(Assignment assignment){
        return false;
    }

    public Collection<Assignment> getUserAssignments(Long id){
        return assignmentRepository.getAssignmentsByUserInfoId(id);
    }

    //Mostrar las asignaciones de dicha empresa aprobadas o pendientes de aprobación
    public Collection<Assignment> getCompanyAssignments(Long id){
        return assignmentRepository.getAssignmentsByCompanyId(id);
    }

    public Assignment getByCompanyAndUserEnabled(Long companyId, Long userInfoId){

        Optional<Assignment> assignment = assignmentRepository.findByCompanyIdAndUserInfoIdAndEnabled(companyId,userInfoId);
        if (assignment.isEmpty()){
            return null;
        }

        return assignment.get();
    }

    public Assignment getAssignmentById(Long assignmentId){
        return assignmentRepository.getById(assignmentId);
    }

    public ResponseEntity<?> endAssignmentUser(Assignment assignment,AppUser appUser){

        //Si el user info de la asignación no es el que está ejecutando entonces rechazar
        if (!assignment.getUserInfo().getId().equals(appUser.getUserInfo().getId())){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Datos errados.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (!assignment.getEnabled() || assignment.getEnded() || assignment.getRejected()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La empresa a la que está intentando desvincularse no se encuentra vinculada actualmente.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Si es asignación tipo admin que es la primera en crear entonces rechazar
        if (assignment.getRequestedByType().equals(RequestedByType.admin)){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Eres el administrador de la cuenta, si quieres eliminar los datos de la empresa o delegar un nuevo administrador debes ir al perfil de la empresa allí enconrtaras dichas opciones.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Si la empresa de la cual se está terminando la asignación es la actual entonces vaciar current assignment y permisos asociados
        if (Objects.equals(appUser.getCurrentAssignment().getId(), assignment.getId())){
            appUserService.logOutMultiLogin(appUser);
        }

        assignment.setEnded(true);
        assignment.setEnabled(false);
        assignment.setEndedBy(appUser.getUserInfo());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignment.setRoles(null);
        assignmentRepository.save(assignment);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Empresa desvinculada.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    public ResponseEntity<?> enableAssignmentUser(Assignment assignment, AppUser appUser){

        //Si el user info de la asignación no es el que está ejecutando entonces rechazar
        if (!assignment.getUserInfo().getId().equals(appUser.getUserInfo().getId())){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Datos errados.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (assignment.getEnabled() || assignment.getEnded() || assignment.getRejected()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Anteriormente ya se había aceptado la solicitud de vinculación.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Si es asignación no proviene de una empresa entonces rechazar
        if (!assignment.getRequestedByType().equals(RequestedByType.company)){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La solicitud de vinculación no fue realizada por la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        assignment.setEnabled(true);
        assignment.setApprovedBy(appUser.getUserInfo());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Emprsa vinculada con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    public ResponseEntity<?> rejectAssignmentUser(Assignment assignment, AppUser appUser){

        //Si el user info de la asignación no es el que está ejecutando entonces rechazar
        if (!assignment.getUserInfo().getId().equals(appUser.getUserInfo().getId())){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Datos errados.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (assignment.getEnabled() || assignment.getEnded() || assignment.getRejected()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La solicitud ya había sido habilitada o rechazada.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Si es asignación no proviene de una empresa entonces rechazar
        if (!assignment.getRequestedByType().equals(RequestedByType.company)){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La solicitud de vinculación no fue realizada por la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Si la empresa de la cual se está terminando la asignación es la actual entonces vaciar current assignment y permisos asociados
        if (Objects.equals(appUser.getCurrentAssignment().getId(), assignment.getId())){
            appUserService.logOutMultiLogin(appUser);
        }

        assignment.setRejected(true);
        assignment.setRejectedBy(appUser.getUserInfo());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignment.setRoles(null);
        assignmentRepository.save(assignment);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("La solicitud de vinculación a dicha empresa fue rechazada.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }

    public ResponseEntity<?> multiLogin(Assignment assignment, AppUser appUser){

        if (!assignment.getEnabled() || assignment.getEnded() || assignment.getRejected()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La empresa a la que intentó acceder ha denegado los permisos, intete solicitar vincularse de nuevo o comunicarse directamente con ellos.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (!assignment.getUserInfo().getId().equals(appUser.getUserInfo().getId())){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Datos errados.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //TODO: cuando se haga una compra o se elimine asignación se debe validar si la asignación actual en la tabla appuser es la misma a la cual se le realizó la compra, con el objetivo de
        if (appUser.getCurrentAssignment() != null && appUser.getCurrentAssignment().getId().equals(assignment.getId()) ){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Login exitoso, la empresa era la sesión actual.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of(
                                    "firstName",appUser.getFisrtName() != null ? appUser.getFisrtName() : ""
                                    ,"lastName",appUser.getLastName() != null ? appUser.getLastName() : ""
                                    ,"profilePicture",appUser.getUserInfo().getProfilePicture() != null ? appUser.getUserInfo().getProfilePicture() : ""
                                    //,"authorities",appUser.getRoles() != null ? appUser.getRoles() : ""
                                    , "token", jwtProvider.updateToken(appUser)
                                    ,"companyName",assignment.getCompany().getName() != null ? assignment.getCompany().getName() : ""
                                    ,"companyProfilePicture",assignment.getCompany().getProfilePicture() != null ? assignment.getCompany().getProfilePicture() : ""
                            ))
                            .build()
                    , HttpStatus.OK
            );
        }

        Collection<Role> rolesToAdd = new ArrayList<>();
        assignment.getRoles().forEach(assignmentRole -> rolesToAdd.add(roleService.getByName(assignmentRole.getName()).get()));
        appUser.getRoles().removeIf(role -> (!role.getName().equals(Roles.USER) && !role.getName().equals(Roles.MULTI_LOGIN)));
        appUser.getRoles().addAll(rolesToAdd);
        appUser.setCurrentAssignment(assignment);

        appUserRepository.save(appUser);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Login exitoso, cambio de empresa.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "firstName",appUser.getFisrtName() != null ? appUser.getFisrtName() : ""
                                ,"lastName",appUser.getLastName() != null ? appUser.getLastName() : ""
                                ,"profilePicture",appUser.getUserInfo().getProfilePicture() != null ? appUser.getUserInfo().getProfilePicture() : ""
                                //,"authorities",appUser.getRoles() != null ? appUser.getRoles() : ""
                                , "token", jwtProvider.updateToken(appUser)
                                ,"companyName",assignment.getCompany().getName() != null ? assignment.getCompany().getName() : ""
                                ,"companyProfilePicture",assignment.getCompany().getProfilePicture() != null ? assignment.getCompany().getProfilePicture() : ""
                        ))
                        .build()
                , HttpStatus.OK
        );
    }
}
