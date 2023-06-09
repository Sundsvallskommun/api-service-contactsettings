package se.sundsvall.contactsettings;

import static org.springframework.boot.SpringApplication.run;

import se.sundsvall.dept44.ServiceApplication;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedReport;

@ServiceApplication
@ExcludeFromJacocoGeneratedReport
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
