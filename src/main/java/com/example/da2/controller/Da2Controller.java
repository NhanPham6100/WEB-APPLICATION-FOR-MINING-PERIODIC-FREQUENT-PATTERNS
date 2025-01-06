package com.example.da2.controller;

import com.example.da2.Da2Application;
import com.example.da2.algorithms.frequentpatterns.pfpm.AlgoPFPM;
import com.example.da2.entity.Dataset;
import com.example.da2.repository.DatasetRepository;
import com.example.da2.service.DatasetService;
import com.example.da2.tools.ResultConverter;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class Da2Controller {
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // Directory to save upload files
    private static String UPLOAD_DIR = "uploads/";

    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private DatasetService datasetService;

    @GetMapping("/")
    public String welcome(HttpSession session) {
        session.removeAttribute("filePath");
        return "welcome";
    }

    @GetMapping("/showFileDirectory")
    public String showFileDirectory(HttpSession session, Model model) {
        if (session.getAttribute("filePath") != null){
            // Get the data from session
            String filePath = (String) session.getAttribute("filePath");
            String fileName = (String) session.getAttribute("fileName");
            if (filePath != null) {
                // Send the data into the model to use in views
                model.addAttribute("filePath", filePath);
                model.addAttribute("fileName", fileName);
                // Fetch data from the service (or directly from the repository)
                List<Dataset> datasets = datasetService.getAllDatasets();
                //Add the datasets to the model to be used in the view
                model.addAttribute("datasets", datasets);
            }
            return "welcome";
        }
        return "welcome";
    }

    public String helloWorld(Model model) {
        // Sending data to view (jsp page)
        String myName = "REPORT";
        model.addAttribute("myNameValue", myName);
        // Just return the page name
        // No Path, no extension
        return "demo";
    }

//    public String setting(@RequestParam(required = false, defaultValue = "1") String minPeriodicityStr,
//                          @RequestParam(required = false, defaultValue = "1000") String maxPeriodicityStr,
//                          @RequestParam(required = false, defaultValue = "5") String minAveragePeriodicityStr,
//                          @RequestParam(required = false, defaultValue = "2000") String maxAveragePeriodicityStr,
//                          Model model,
//                          HttpSession session) throws IOException {
    @PostMapping("/setting")
    public String setting(@RequestParam() String minPeriodicityStr,
            @RequestParam() String maxPeriodicityStr,
            @RequestParam() String minAveragePeriodicityStr,
            @RequestParam() String maxAveragePeriodicityStr,
            Model model,
            HttpSession session) throws IOException {

        if (minPeriodicityStr.isEmpty() ||
                maxPeriodicityStr.isEmpty() ||
                minAveragePeriodicityStr.isEmpty() ||
                maxAveragePeriodicityStr.isEmpty() ||
                (session.getAttribute("filePath") == null) ){
            return "notification";
        } else {

            // Check if  parameters are passed or not
            Boolean isMinPeriodicityPresent = (minPeriodicityStr != null);
            Boolean isMaxPeriodicityPresent = (maxPeriodicityStr != null);
            Boolean isMinAveragePeriodicityPresent = (minAveragePeriodicityStr != null);
            Boolean isMaxAveragePeriodicityPresent = (maxAveragePeriodicityStr != null);

            // Convert to Integer if not empty
            Integer minPeriodicity = parseInteger(minPeriodicityStr);
            Integer maxPeriodicity = parseInteger(maxPeriodicityStr);
            Integer minAveragePeriodicity = parseInteger(minAveragePeriodicityStr);
            Integer maxAveragePeriodicity = parseInteger(maxAveragePeriodicityStr);

            String output = "output.txt";
            //        String inputPath = "static/fruithut_original.txt";
            String input = (String) session.getAttribute("filePath");
            model.addAttribute("filePath", input);

            // EXAMPLE FROM THE REPORT:
//        String input = fileToPath(inputPath);

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

            String pfpmStatistics = "=============  PFPM ALGORITHM v2.17 ESCP: true =====\n" +
                    " Database size: " + (String.valueOf(algoPFPM.databaseSize)) + " transactions\n" +
                    " Time : " + (String.valueOf(algoPFPM.totalExecutionTime)) + " ms\n" +
                    " Memory ~ " + (String.valueOf(algoPFPM.maximumMemoryUsage)) + " MB\n" +
                    " Periodic Itemsets count : " + (String.valueOf(algoPFPM.phuiCount)) + "\n" +
                    " Candidate count : " + (String.valueOf(algoPFPM.candidateCount)) + "\n" +
                    " Gamma (support prunning threshold): " + (String.valueOf(algoPFPM.supportPruningThreshold)) + "\n" +
                    "----------------------------------------------------\n" +
                    "  with: \n" +
                    "   Min Periodicity Threshold : " + (minPeriodicity.toString()) +  "\n" +
                    "   Max Periodicity Threshold : " + (maxPeriodicity.toString()) +  "\n" +
                    "   Min Average Periodicity Threshold : " + (minAveragePeriodicity.toString()) + "\n" +
                    "   Max Average Periodicity Threshold : " + (maxAveragePeriodicity.toString()) + "\n" +
                    "   Path Containing Your Dataset: " + input + "\n" +
                    "----------------------------------------------------\n";

            Path path = Paths.get("statistics.txt");
            try {
                Files.write(path, pfpmStatistics.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // convert the output file
            String output_converted = "output_converted.txt";

            ResultConverter converter = new ResultConverter();
            converter.convert(input, output, output_converted, Charset.forName("UTF-8"));

            model.addAttribute("minPe", minPeriodicityStr);
            model.addAttribute("maxPe", maxPeriodicityStr);
            model.addAttribute("minAvgPe", minAveragePeriodicityStr);
            model.addAttribute("maxAvgPe", maxAveragePeriodicityStr);
            model.addAttribute("isMinPeriodicityPresent", isMinPeriodicityPresent);
            model.addAttribute("isMaxPeriodicityPresent", isMaxPeriodicityPresent);
            model.addAttribute("isMinAveragePeriodicityPresent", isMinAveragePeriodicityPresent);
            model.addAttribute("isMaxAveragePeriodicityPresent", isMaxAveragePeriodicityPresent);
            return "setting";

        }
    }

    private static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Da2Application.class.getClassLoader().getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }

    // Utility method to convert string to integer
    private Integer parseInteger(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Handle the error
                return null;
            }
        }
        return null;
    }

    // Handle the file upload
    @PostMapping("/upload")
    public String upload(@RequestParam("file")MultipartFile file, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            session.setAttribute("filePath", null);
            return "upload-status";
        }

        try {
            // save the file into the database
            // check if the file is a plain text file
            if (!file.getContentType().equals("text/plain")) {
                return "redirect:/?error=not-a-text-file";
            }
            // save the file into the database
            Dataset fileDataset = new Dataset(file.getOriginalFilename(), file.getBytes());
            // Debugging: Log the file size
            byte[] fileData = file.getBytes(); // Get the file content as a byte array
            System.out.println("Dataset byte array length: " + fileData.length); // Log byte array length
            System.out.println("Dataset name: " + file.getOriginalFilename()); // Log dataset name
            System.out.println("file size " + file.getSize() + "bytes");
            if (file.getOriginalFilename().length() > 255) {
                return "redirect:/?error=filename-too-long";
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return "redirect:/?error=file-too-large";
            } else {
                datasetRepository.save(fileDataset);
            }
//            redirectAttributes.addFlashAttribute("message", "File uploaded successfully.");

            // Save the file locally
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.write(path, bytes);
            String filePath = path.toAbsolutePath().toString();
            file.transferTo(new File(filePath));
//            redirectAttributes.addAttribute("fileDirectory", path.toAbsolutePath().toString());
//            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'.");
            model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'.");
            session.setAttribute("filePath", filePath);
            String fileName = file.getOriginalFilename();
            session.setAttribute("fileName", fileName);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error uploading file: " + e.getMessage());
            return "redirect:/?error=upload-failed";
        }
        return "upload-status";
    }

    // Serve the upload status page
    @GetMapping("/upload-status")
    public String uploadStatus() {
        return "redirect:/showFileDirectory";
    }

    @GetMapping("/output")
    public String output(Model model) {
        // Define the path to the .txt file
        Path statisticsPath = Paths.get("statistics.txt");
        Path filePath = Paths.get("output_converted.txt");
        try {
            // Read the file content
            String statistics = new String(Files.readAllBytes(statisticsPath));
            String fileContent = new String(Files.readAllBytes(filePath));
            // Add the file content to the model
            model.addAttribute("statistics", statistics);
            model.addAttribute("fileContent", fileContent);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("fileContent", "Error reading file.");
        }
        // Return the name of the Thymeleaf template
        return "output";
    }
}
