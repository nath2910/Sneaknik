package backend;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import backend.entity.SnkVente;
import backend.repository.SnkVenteRepository;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {


	public static void main(String[] args) {SpringApplication.run(BackendApplication.class, args);}
	@Autowired
	private SnkVenteRepository snkVenteRepo;

	@Override
	public void run(String... args) throws Exception {
	// 	SnkVente snkVente = SnkVente.builder()
  //   // .nomItem("Air Jordan 4")              // attention au bon nom de champ
  //   // .prixRetail(new BigDecimal("200.00"))
  //   // .prixResell(new BigDecimal("320.00"))
  //   // .argentPreleve("perso")
  //   // .date(LocalDate.now())
	// 	 .build();
		  
	// 	 snkVenteRepo.save(snkVente);
	 }

}
