package com.example.military_managment_system.controller;
import com.example.military_managment_system.model.Soldier;
import com.example.military_managment_system.securityconfig.EmailSenderServiceConfig;
import com.example.military_managment_system.service.DatabasePDFService;
import com.example.military_managment_system.service.SoldierService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@Controller
public class SoldierController {

    @Autowired
   private SoldierService soldierService;
    @Autowired
    private EmailSenderServiceConfig emailSenderServiceConfig;

    @GetMapping("/")
    public String home(){
        return "index";
    }
    @GetMapping("/home")
    public String homePage(Model model){
        return   findPaginated(1,"firstName","asc",model);
    }


    @GetMapping ("/search")
    public String searchMethod(Model model){
        model.addAttribute("search",new Soldier());
        return "findOne";
    }

    @PostMapping("/search")
    public String getEmployee(@ModelAttribute("search") Soldier soldier, Model model){
        Soldier soldier1=soldierService.findSoldier(soldier.getRegNo());
       if (soldier1!=null) {
           model.addAttribute("soldier1",soldier1);
           return "findOne";
       }else {
           model.addAttribute("error","He/She is not found");
           return "findOne";
       }
    }


    @GetMapping("/employee_page")
    public String getEmployee(Model model){
        model.addAttribute("student",new Soldier());
        return "employee";
    }


    @GetMapping("/reg")
    public String registerStudentPage(Model model){

        model.addAttribute("student",new Soldier());
        return "registration-page";
    }

    @PostMapping(value = "/registerUser",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String userRegisterForm(@ModelAttribute("student") @Validated Soldier theSoldier, BindingResult result) throws ParseException, IOException, MessagingException {

        Soldier savedSoldier = soldierService.saveSoldier(theSoldier);
        emailSenderServiceConfig.sendCitizenEmail(theSoldier.getEmail(),"SOLDIER REGISTRATION",
                theSoldier.getFirstName()+" "+theSoldier.getLastName());
        if(savedSoldier != null){
            return "redirect:/employee_page?success";
        }
        return "redirect:/employee_page?error";
    }


    @PostMapping(value = "/register",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String registerStudentInDb(@ModelAttribute("student") @Validated Soldier theSoldier, BindingResult result) throws ParseException, IOException, MessagingException {
        Soldier savedSoldier = soldierService.saveSoldier(theSoldier);
        emailSenderServiceConfig.sendCitizenEmail(theSoldier.getEmail(),"SOLDIER REGISTRATION",
                theSoldier.getFirstName()+" "+theSoldier.getLastName());
        if(savedSoldier != null){
            return "redirect:/reg?success";
        }
        return "redirect:/reg?error";
    }

    @GetMapping("/home/edit/{studentID}")
    public String editStudent(@PathVariable String studentID, Model model){
        Long StudId=Long.parseLong(studentID);
        model.addAttribute("student",soldierService.findSoldier(StudId));
        return "edit-student";
    }

    @PostMapping(value = "/home/{studentID}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateStudent(@PathVariable String studentID, @ModelAttribute("student") @Validated Soldier soldier , BindingResult result, @RequestParam("picture") MultipartFile file) throws IOException {
        Long StudId=Long.parseLong(studentID);
        System.out.println(StudId);
       Soldier exitingSoldier=soldierService.findSoldier(StudId);

        if (exitingSoldier!=null) {

            exitingSoldier.setRegNo(soldier.getRegNo());
            exitingSoldier.setNationalId(soldier.getNationalId());
            exitingSoldier.setNationality(soldier.getNationality());

            exitingSoldier.setFirstName(soldier.getFirstName());

            exitingSoldier.setLastName(soldier.getLastName());

            exitingSoldier.setEmail(soldier.getEmail());

            exitingSoldier.setDob(soldier.getDob());

            exitingSoldier.setRank(soldier.getRank());
            exitingSoldier.setGender(soldier.getGender());


            Soldier savedSoldier = soldierService.saveSoldier(exitingSoldier);
            return "redirect:/home";
        }
        return "home-page";
    }
    @GetMapping ("/home/{studentID}")
    public String detleteStudent(@PathVariable String studentID ){
        Long StudId=Long.parseLong(studentID);
        soldierService.deleteSoldier(StudId);
        return "redirect:/home";
    }

    @GetMapping("/exportCsv")
    public void exportCSV(HttpServletResponse response)
            throws Exception {

        String filename = "Soldiers-data.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        StatefulBeanToCsv<Soldier> writer = new StatefulBeanToCsvBuilder<Soldier>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false)
                .build();
        writer.write(soldierService.getAllSoldier());

    }

    @GetMapping(value = "/exportPdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> volunteerReport()  throws IOException {
        List<Soldier> volunteers = (List<Soldier>) soldierService.getAllSoldier();

        ByteArrayInputStream bis = DatabasePDFService.employeePDFReport(volunteers);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=SoldierReport.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,Model model){
        int pageSize=5;
        Page<Soldier> page=soldierService.pagenateStudent(pageNo,pageSize,sortField,sortDir);
        List<Soldier> studentList=page.getContent();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPage",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listStudents",studentList);
        return "home-page";
    }

}
