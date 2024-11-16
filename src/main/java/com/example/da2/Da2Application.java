package com.example.da2;

import com.example.da2.algorithms.frequentpatterns.pfpm.AlgoPFPM;
import com.example.da2.tools.ResultConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

@SpringBootApplication
public class Da2Application {

	public static void main(String[] args) throws IOException {

		SpringApplication.run(Da2Application.class, args);
		WelcomeMessage var = new WelcomeMessage();
		System.out.println(var.welcomeMess());
//		ConfigurableApplicationContext context = SpringApplication.run(Da2Application.class, args);
		String output = "output.txt";
		String inputPath = "static/contextTest.txt";
		// ===
		// EXAMPLE FROM THE REPORT:
		String input = fileToPath(inputPath);

		int minPeriodicity = 1; 			// transactions
		int maxPeriodicity = 3; 			// transactions
		int minAveragePeriodicity = 1; 		// transactions
		int maxAveragePeriodicity = 2;	// transactions
		// ===
		//===== Optional parameters ==//
		// Minimum number of items that patterns should contain
		int minimumLength = 1;
		// Maximum number of items that patterns should contain
		int maximumLength = Integer.MAX_VALUE;
		//===========================//z

		// Create the algorithm
		AlgoPFPM algoPFPM = new AlgoPFPM();
		// Enable some optimization
		algoPFPM.setEnableESCP(true);

		// Set the pattern length constraints
		algoPFPM.setMinimumLength(minimumLength);
		algoPFPM.setMaximumLength(maximumLength);

		// Run the algorithm
		algoPFPM.runAlgorithm(input, output,
				minPeriodicity, maxPeriodicity,
				minAveragePeriodicity, maxAveragePeriodicity);

		// display statistics about the algorithm execution
		System.out.println("Here are the statistics illustrating the default case:");
		algoPFPM.printStats();

//		// convert the output file
//		String output_converted = "output_converted.txt";
//
//		ResultConverter converter = new ResultConverter();
//		converter.convert(input, output, output_converted, Charset.forName("UTF-8"));
	}

	private static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = Da2Application.class.getClassLoader().getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
	}

}
