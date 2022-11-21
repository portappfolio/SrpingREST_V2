package com.portappfolio.app;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.phone.*;
import com.portappfolio.app.appUser.role.RoleService;
import com.portappfolio.app.appUser.userInfo.*;
import com.portappfolio.app.assignment.Role.AssignmentRoleService;
import com.portappfolio.app.company.branch.zipCode.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}


	@Bean
	CommandLineRunner run(RoleService roleService
			, PrefixService prefixService
			, AssignmentRoleService assignmentRoleService
			, AppUserService appUserService
		  	, JobService jobService
			, SpecialityService specialityService
		  	, GenderService genderService
		  	, UserInfoService userInfoService
		  	, PhoneService phoneService
		  	, ZipCodeService zipCodeService
		  	, CityService cityService
		  	, CountryService countryService
	) {
		return args -> {
			/*
			roleService.save(Roles.USER);
			roleService.save(Roles.MULTI_LOGIN);
			roleService.save(Roles.ADMIN_COMPANY);
			roleService.save(Roles.ADMIN_PEOPLE);
			roleService.save(Roles.ADMIN_PAYMENTS);
			roleService.save(Roles.ADMIN_PERMISIONS);
			roleService.save(Roles.ADMIN_DASHBOARD);
			assignmentRoleService.save(Roles.USER);
			assignmentRoleService.save(Roles.ADMIN_COMPANY);
			assignmentRoleService.save(Roles.ADMIN_PEOPLE);
			assignmentRoleService.save(Roles.ADMIN_PAYMENTS);
			assignmentRoleService.save(Roles.ADMIN_PERMISIONS);
			assignmentRoleService.save(Roles.ADMIN_DASHBOARD);
			//AppUser appUser = appUserService.save(new AppUser("Carlos","Alvarado","carlosalvaradom@icloud.com","12345678"));
			//appUserService.enableAppUser(appUser.getEmail());
			//appUserService.signUpUser(new AppUser("Prueba","2","carlosalvarado.ph@gmail.com","12345678"));
			//appUserService.enableAppUser("carlosalvarado.ph@gmail.com");


			Job job = jobService.save("Odontología");
			Speciality speciality = specialityService.save("Odontología General",job);
			specialityService.save("Ortodoncia",job);
			specialityService.save("Estética Dental y Rehabilitación",job);
			specialityService.save("Endodoncia",job);
			specialityService.save("Maxilofacial",job);
			specialityService.save("Odontopediatría",job);
			specialityService.save("Periodoncia",job);
			specialityService.save("Otra",job);
			Gender gender = genderService.save("Masculino");
			genderService.save("Femenino");
			genderService.save("Otro");
			genderService.save("Prefiere No Contestar");
			Country country = countryService.save(new Country("Colombia",1D,1D));
			Prefix prefix = prefixService.save("+57",country);
			countryService.save(new Country("Colombia",1D,1D));
			countryService.save(new Country("Mexico",1D,1D));
			countryService.save(new Country("Estados Unidos",1D,1D));
			countryService.save(new Country("Argentina",1D,1D));
			countryService.save(new Country("España",1D,1D));

			City city1 = cityService.save(new City("Bogotá",country,1D,1D));
			City city2 = cityService.save(new City("Medellin",country,2D,2D));
			zipCodeService.save(new ZipCode(city1,"0121212","Fontibon - Villemar",1D,1D));
			zipCodeService.save(new ZipCode(city1,"0121212","Fontibon - Atahualpa",2D,2D));
			zipCodeService.save(new ZipCode(city2,"0121212","Poblado",3D,3D));
			zipCodeService.save(new ZipCode(city2,"0121212","Centro",4D,4D));


			*/

		};
	}
}
