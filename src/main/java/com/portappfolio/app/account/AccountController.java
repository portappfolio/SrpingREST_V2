package com.portappfolio.app.account;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.phone.Phone;
import com.portappfolio.app.appUser.phone.PhoneService;
import com.portappfolio.app.appUser.phone.PrefixService;
import com.portappfolio.app.appUser.role.Role;
import com.portappfolio.app.appUser.role.RoleService;
import com.portappfolio.app.appUser.role.Roles;
import com.portappfolio.app.appUser.userInfo.*;
import com.portappfolio.app.assignment.Assignment;
import com.portappfolio.app.assignment.AssignmentService;
import com.portappfolio.app.assignment.Role.AssignmentRole;
import com.portappfolio.app.company.Company;
import com.portappfolio.app.company.CompanyService;
import com.portappfolio.app.company.IdentificationType;
import com.portappfolio.app.company.branch.zipCode.ZipCodeService;
import com.portappfolio.app.models.ConfirmPhoneRequest;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.models.PhoneRequest;
import com.portappfolio.app.security.config.SessionService;
import com.portappfolio.app.security.config.UserClassSecurity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@PreAuthorize("isAuthenticated()")
@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final UserInfoService userInfoService;
    private final SessionService sessionService;
    private final PrefixService prefixService;
    private final JobService jobService;
    private final SpecialityService specialityService;
    private final GenderService genderService;
    private final PhoneService phoneService;
    private final AppUserService appUserService;
    private final AssignmentService assignmentService;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final ZipCodeService zipCodeService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/load")
    public ResponseEntity<?> get(){

        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());
        if(userClassSecurity.getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of(
                                    "completeInfo", false
                                    , "firstName", userClassSecurity.getFisrtName()
                                    , "lastName", userClassSecurity.getLastName()
                                    , "email", userClassSecurity.getUsername()
                                    ,"prefixes", prefixService.list()
                                    , "specialitys", specialityService.list()
                                    , "genders", genderService.list()
                                    ))
                            .build()
                    , HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "completeInfo", true
                                , "firstName", userClassSecurity.getFisrtName()
                                , "lastName", userClassSecurity.getLastName()
                                , "email", userClassSecurity.getUsername()
                                , "userInfo", userClassSecurity.getUserInfo()
                                , "assignments", assignmentService.getUserAssignments(appUser.get().getUserInfo().getId())
                        ))
                        .build()
                , HttpStatus.OK
        );

    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/getAssignments")
    public ResponseEntity<?> getAssignments(){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());
        if(userClassSecurity.getUserInfo() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of(
                                    "assignments",  new ArrayList<Assignment>()
                            ))
                            .build()
                    , HttpStatus.OK
            );
        }

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "assignments", assignmentService.getUserAssignments(appUser.get().getUserInfo().getId())
                        ))
                        .build()
                , HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/sendTokenToValidatePhone")
    public ResponseEntity<?> sendTokenToValidatePhone(@RequestBody PhoneRequest phone){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        return phoneService.sendTokenToValidatePhone(
                userClassSecurity.getUsername()
                , phone
        );
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/confirmPhone")
    public  ResponseEntity<?> confirmPhone(@RequestBody ConfirmPhoneRequest confirmPhoneRequest){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        return phoneService.confirmPhone(
            confirmPhoneRequest.getToken()
            , userClassSecurity.getAppUserId()
            , confirmPhoneRequest.getPrefix()
            , confirmPhoneRequest.getNumber()
            , confirmPhoneRequest.getChannels()
        );
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/saveUserInfo")
    public ResponseEntity<?> saveUserInfo(@RequestBody UserInfo userInfo) {

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
        }

        if (appUser.get().getUserInfo() != null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Registro ya fue realizado con exito.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        UserInfo persistUI = new UserInfo(
            userInfo.getPhone()
            , appUser.get()
            , userInfo.getGender()
            , userInfo.getSpeciality()
            , userInfo.getBirthday()
        );

        persistUI = userInfoService.create(persistUI);

        if (persistUI == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Información ya fue registrada.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        appUserService.addUserInfo(persistUI,appUser.get());

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Registro realizado con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/updateProfilePicture")
    public ResponseEntity<?> updateProfilePicture(@RequestParam(value = "file", required = true) MultipartFile multipartFile) throws IOException {
        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());
        //Optional<UserInfo> userInfoOptional = userInfoService.getByEmail(userClassSecurity.getUsername());
        UserInfo userInfo = appUser.get().getUserInfo();

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
        }

        if (userInfo == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha completado el registro del usuario.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        userInfo.setProfilePicture(multipartFile.getBytes());
        userInfo = userInfoService.update(userInfo);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Imagen Actualizada con Exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/existCompanyByIdentification")
    public ResponseEntity<?> existCompanyByIdentification(@RequestParam(value = "identificationType",required = true) String identificationType, @RequestParam(value="identity",required = true) String identity){


        Optional<Company> companyOptional = companyService.getByIdentificationTypeAndIdentity(IdentificationType.valueOf(identificationType),identity);
         if (companyOptional.isPresent()){
             return new ResponseEntity<>(
                     CustomResponse.builder()
                             .timeStamp(LocalDateTime.now())
                             .message("Empresa existe.")
                             .status(HttpStatus.OK)
                             .statusCode(HttpStatus.OK.value())
                             .data(Map.of(
                                     "companyExist",true
                                     ,"company",companyOptional.get()
                             ))
                             .build()
                     , HttpStatus.OK
             );
         }

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("La empresa que estás buscando no está registrada.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "companyExist",false
                        ))
                        .build()
                , HttpStatus.OK
        );
    }


    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/createCompany")
    public ResponseEntity<?> createCompany(@RequestBody Company company) {

        Company companyOptional = companyService.create(company);
        if (companyOptional == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Empresa ya fue registrada.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        return assignmentService.atCreateCompany(companyOptional);

    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/createProfilePictureCompany")
    public ResponseEntity<?> updateProfilePictureCompany(@RequestParam(value = "file", required = true) MultipartFile multipartFile, @RequestParam(value = "companyId",required = true) String companyId) throws IOException{

        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());


        Assignment assignment = assignmentService.getByCompanyAndUserEnabled(Long.parseLong(companyId),appUser.get().getUserInfo().getId());

        if (assignment == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No tiene permisos para editar la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Boolean hasAuthority = false;

        for( AssignmentRole a : assignment.getRoles()){
            if(a.getName().name().equals(Roles.ADMIN_COMPANY.name())){
              hasAuthority = true;
              break;
            }
        }

        if (!hasAuthority){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No tiene permisos para editar la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Company company = assignment.getCompany();
        company.setProfilePicture(multipartFile.getBytes());

        company = companyService.update(company);
        if (company == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se ha podido actualizar la inforrmación de la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Imagen actualizada con éxito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of("company",company))
                        .build()
                , HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @GetMapping("/getZipCodes")
    public ResponseEntity<?> getZipCodes(){
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of(
                                "zipCodes", zipCodeService.list()
                        ))
                        .build()
                , HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/onLoginAssignment")
    public ResponseEntity<?> onLoginAssignment(@RequestParam(value = "assignmentId",required = true) String assignmentId){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();
        Assignment assignment = assignmentService.getAssignmentById(Long.parseLong(assignmentId));

        return assignmentService.multiLogin(assignment,appUser);
    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/endAssignment")
    public ResponseEntity<?> endAssignment(@RequestParam(value = "assignmentId",required = true) String assignmentId){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();
        Assignment assignment = assignmentService.getAssignmentById(Long.parseLong(assignmentId));

        return assignmentService.endAssignmentUser(assignment,appUser);
    }


    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/approveAssignment")
    public ResponseEntity<?> approveAssignment(@RequestParam(value = "assignmentId",required = true) String assignmentId){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();
        Assignment assignment = assignmentService.getAssignmentById(Long.parseLong(assignmentId));

        return assignmentService.enableAssignmentUser(assignment,appUser);
    }


    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/rejectAssignment")
    public ResponseEntity<?> rejectAssignment(@RequestParam(value = "assignmentId",required = true) String assignmentId){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();
        Assignment assignment = assignmentService.getAssignmentById(Long.parseLong(assignmentId));
        return assignmentService.rejectAssignmentUser(assignment,appUser);
    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/sendRequestUserToCompany")
    public ResponseEntity<?> sendRequestUserToCompany(@RequestParam(value = "companyId",required = true) String companyId){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();
        return assignmentService.sendRequestUserToCompany(Long.parseLong(companyId),appUser);
    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getUserInfo(){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Data retrieved.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of("userInfo",appUser.getUserInfo()
                                ,"prefixes", prefixService.list()
                                , "specialitys", specialityService.list()
                                , "genders", genderService.list()
                        ))
                        .build()
                , HttpStatus.OK
        );
    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @GetMapping("/deleteProfilePicture")
    public ResponseEntity<?> deleteProfilePicture(){
        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();

        UserInfo userInfo = appUser.getUserInfo();

        if(userInfo.getProfilePicture() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La imagen ya fue eliminada o no se ha asignado imagen de perfil.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        userInfo.setProfilePicture(null);

        if(userInfoService.update(userInfo) == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Fallo al intentar eliminar imagen.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Imagen eliminada con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    @PreAuthorize("hasAuthority('MULTI_LOGIN')")
    @PostMapping("/updateUserInfo")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserInfo userInfo) {

        UserClassSecurity userClassSecurity = sessionService.getSession();
        AppUser appUser = appUserService.getByEmail(userClassSecurity.getUsername()).get();

        UserInfo userInfoBD = appUser.getUserInfo();

        userInfoBD.setFirstName(userInfo.getFirstName());
        userInfoBD.setLastName(userInfo.getLastName());
        userInfoBD.setGender(userInfo.getGender());
        userInfoBD.setSpeciality(userInfo.getSpeciality());
        userInfoBD.setBirthday(userInfo.getBirthday());


        userInfoService.update(userInfoBD);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Información actualizada con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }



}
