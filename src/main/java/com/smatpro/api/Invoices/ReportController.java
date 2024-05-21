package com.smatpro.api.Invoices;





import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
@Slf4j  // to have something printed on the console
public class ReportController {


    @RequestMapping(value = "/report", method = RequestMethod.GET)
    //   public void report(@ModelAttribute(value="amortization") Transanctions amortization, Model model, @PathVariable("pageNumber") String currentpage ) throws JRException {
    //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    public void report() throws JRException {
        List<Object> empList = new ArrayList<>();
        empList.add("kkkk");
        //empList.add(transanctionsService.listAll(currentpage));
        //empList.add(clientsAccountsImplementation.listAll("7890"));
        // empList.add((Bean) new Transanctions(3L,"","","","","","",89,900,"",""));

        System.out.println("Ndakugona  : " + empList);

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(empList);
        Map<String, Object> parameters = new HashMap<>();
        JasperReport jasperReport = JasperCompileManager.compileReport("C:\\backups\\portfolio.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), jrBeanCollectionDataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\backups\\portfolio.pdf");
        // return "application/Application";

        //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    }


    @RequestMapping(value = "/report2", method = RequestMethod.POST)
    public void report2(@RequestBody @Valid EmailParams emailParams)throws Exception{

        System.out.println("report endpoint called");
        try {
        System.setProperty("java.awt.headless", "false");
        String reportFilePath = "C:\\backups\\dbo_portfolio.jrxml";

        Map<String,Object>param = new HashMap<>();
        param.put("title",emailParams.getTitle());
        param.put("address","1233 Manresa ");

        LocalDateTime now = LocalDateTime.now();
        Long timestamp = now.toEpochSecond(ZoneOffset.UTC);
        String longRepresentation = timestamp.toString();

        String fileName = emailParams.getTitle()  + longRepresentation + ".pdf";
     //   List<spEQCounterpartyPortfolio> x= new ConsolidetdPortfolioServices().getEQCounterpartyPortfolio(12721,"1 jan 2023",0);

        List<Object>x = new ArrayList<>();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(x);



            JasperReport jasperReport = JasperCompileManager.compileReport(reportFilePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, param, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\backups\\"+fileName);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            //sendEmailWithAttachment2();  //sendEmailWithAttachment("letinashe.chiyangwa@gmail.com","Equity Report","",pdfBytes,"Report","leroy.chiyangwa1994@gmail.com","L!r0y!994");

            gmailMaster(pdfBytes, emailParams.getEmail(), emailParams.getTitle());
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }




    public void gmailMaster(byte[] pdfBytes,String emailto,String emailtitle){
        String email_to=emailto;//change accordingly
        try
        {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            //props.put("mail.smtp.ssl.enable", "false");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            Session session = Session.getInstance(props, new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication("letinashe.chiyangwa@gmail.com", "owwltjhqxdxisooe");
                }
            });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("letinashe.chiyangwa@gmail.com", false));

            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email_to));
            msg.setSubject(emailtitle);
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("Good day, kindly find the attached file for "+emailtitle+" as per your request",
                    "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachPart = new MimeBodyPart();
            attachPart.setContent(pdfBytes, "application/pdf");
            attachPart.setFileName("portfolio.pdf");

            multipart.addBodyPart(attachPart);
            msg.setContent(multipart);
            Transport.send(msg);
        }
        catch (Exception exe)
        {
            exe.printStackTrace();
        }
    }

    }
