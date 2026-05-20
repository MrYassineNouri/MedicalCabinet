package services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import models.Patient;

import java.io.File;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "LOGIN";
    private static final String SMTP_PASS = "KEY";
    private static final String FROM_EMAIL = "SAME AS LOGIN";

    public static boolean sendReportToPatient(Patient patient, File pdfFile) {
        try {
            System.out.println("[EmailService] Starting send...");
            System.out.println("[EmailService] To: " + patient.getEmail());
            System.out.println("[EmailService] PDF exists: " + (pdfFile != null && pdfFile.exists()));
            System.out.println("[EmailService] PDF size: " + (pdfFile != null ? pdfFile.length() : 0) + " bytes");

            if (patient.getEmail() == null || patient.getEmail().isBlank()) {
                System.err.println("[EmailService] ERROR: Patient has no email address.");
                return false;
            }

            if (pdfFile == null || !pdfFile.exists()) {
                System.err.println("[EmailService] ERROR: PDF file is missing or invalid.");
                return false;
            }

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", SMTP_HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Medical Cabinet"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(patient.getEmail()));
            message.setSubject("Your Medical Report - " + patient.getFirstName() + " " + patient.getLastName());

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                    "Dear " + patient.getFirstName() + " " + patient.getLastName() + ",\n\n" +
                            "Please find your medical report attached to this email.\n\n" +
                            "Best regards,\nMedical Cabinet"
            );

            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(pdfFile);
            filePart.setFileName("Medical_Report_" + patient.getFirstName() + "_" + patient.getLastName() + ".pdf");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(filePart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("[EmailService] SUCCESS — sent to: " + patient.getEmail());
            return true;

        } catch (AuthenticationFailedException e) {
            System.err.println("[EmailService] AUTH FAILED: " + e.getMessage());
            return false;
        } catch (MessagingException e) {
            System.err.println("[EmailService] SMTP ERROR: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("[EmailService] ERROR: " + e.getMessage());
            return false;
        }
    }
}